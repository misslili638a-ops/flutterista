package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.StudyTask
import com.example.data.model.UserProfile
import com.example.data.repository.SakoonRepository
import com.example.prayer.ActivePrayerInfo
import com.example.prayer.CityLocation
import com.example.prayer.JuristicMethod
import com.example.prayer.LocationManagerHelper
import com.example.prayer.PrayerSchedule
import com.example.prayer.PrayerTimesCalculator
import android.util.Log
import com.example.ai.AiChatMessage
import com.example.ai.AiChatState
import com.example.ai.MessageSender
import com.example.ai.SakoonAiService
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SakoonTab(val title: String) {
    TODAY("আজ"),
    HEALTH("স্বাস্থ্য"),
    FAITH("ইবাদত"),
    STUDY("পড়াশোনা"),
    HEART("মন")
}

enum class TimerStatus {
    IDLE, RUNNING, PAUSED, COMPLETED
}

enum class BreathingPhase(val label: String, val seconds: Int) {
    INHALE("ধীরে শ্বাস নিন", 4),
    HOLD("ধরে রাখুন", 4),
    EXHALE("ধীরে শ্বাস ছাড়ুন", 4)
}

class SakoonViewModel(application: Application) : AndroidViewModel(application) {

    val repository = SakoonRepository(application)
    val locationHelper = LocationManagerHelper(application)

    // Current navigation tab
    private val _currentTab = MutableStateFlow(SakoonTab.TODAY)
    val currentTab: StateFlow<SakoonTab> = _currentTab.asStateFlow()

    // Transient UI message / toast
    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents: SharedFlow<String> = _uiEvents.asSharedFlow()

    // Date display
    val todayBengali: String = repository.getTodayBengaliDisplay()

    // Pass through repository flows
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val completedHabits: StateFlow<Set<String>> = repository.completedHabits
    val waterCount: StateFlow<Int> = repository.waterCount
    val movementActivity: StateFlow<String> = repository.movementActivity
    val movementMinutes: StateFlow<Int> = repository.movementMinutes
    val movementDone: StateFlow<Boolean> = repository.movementDone
    val completedFaith: StateFlow<Set<String>> = repository.completedFaith
    val studyTasks: StateFlow<List<StudyTask>> = repository.studyTasks
    val mood: StateFlow<String?> = repository.mood
    val journalText: StateFlow<String> = repository.journalText
    val missingDraft: StateFlow<String> = repository.missingDraft

    // --- Daily Dhikr State ---
    val selectedDhikrId: StateFlow<String> = repository.selectedDhikrId
    val dhikrTodayCount: StateFlow<Int> = repository.dhikrTodayCount
    val dhikrGoal: StateFlow<Int> = repository.dhikrGoal
    val dhikrLifetimeTotal: StateFlow<Int> = repository.dhikrLifetimeTotal

    // --- Daily Prayer Times (Salat) State ---
    private val _currentLocation = MutableStateFlow(locationHelper.getSavedLocation())
    val currentLocation: StateFlow<CityLocation> = _currentLocation.asStateFlow()

    private val _juristicMethod = MutableStateFlow(locationHelper.getSavedJuristicMethod())
    val juristicMethod: StateFlow<JuristicMethod> = _juristicMethod.asStateFlow()

    private val _prayerSchedule = MutableStateFlow(calculateCurrentPrayerSchedule())
    val prayerSchedule: StateFlow<PrayerSchedule> = _prayerSchedule.asStateFlow()

    private val _activePrayerInfo = MutableStateFlow(
        PrayerTimesCalculator.getActivePrayerInfo(_prayerSchedule.value)
    )
    val activePrayerInfo: StateFlow<ActivePrayerInfo> = _activePrayerInfo.asStateFlow()

    // --- Study Focus Timer ---
    private val _timerMinutes = MutableStateFlow(25)
    val timerMinutes: StateFlow<Int> = _timerMinutes.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(25 * 60)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private val _timerStatus = MutableStateFlow(TimerStatus.IDLE)
    val timerStatus: StateFlow<TimerStatus> = _timerStatus.asStateFlow()

    private var timerJob: Job? = null

    // --- Heart Breathing Guide (60 seconds) ---
    private val _breathingActive = MutableStateFlow(false)
    val breathingActive: StateFlow<Boolean> = _breathingActive.asStateFlow()

    private val _breathingPhase = MutableStateFlow(BreathingPhase.INHALE)
    val breathingPhase: StateFlow<BreathingPhase> = _breathingPhase.asStateFlow()

    private val _breathingRemaining = MutableStateFlow(60)
    val breathingRemaining: StateFlow<Int> = _breathingRemaining.asStateFlow()

    private var breathingJob: Job? = null

    // --- Sakoon AI Companion State ---
    private val aiService = SakoonAiService()

    private val _isAiCompanionOpen = MutableStateFlow(false)
    val isAiCompanionOpen: StateFlow<Boolean> = _isAiCompanionOpen.asStateFlow()

    private fun getInitialAiGreeting(): AiChatMessage {
        return AiChatMessage(
            sender = MessageSender.SAKOON_AI,
            text = "আসসালামু আলাইকুম! আমি আপনার FLUTTERISTA এআই সঙ্গী 🌸 মনের অবস্থা, পড়াশোনার কৌশল, দু'আ-যিকির কিংবা দৈনন্দিন স্বাস্থ্যরীতি—যে কোনো বিষয়ে আমাকে নির্দ্বিধায় বলতে পারেন। আজ আপনাকে কীভাবে সঙ্গ দিতে পারি?"
        )
    }

    private val _aiMessages = MutableStateFlow<List<AiChatMessage>>(listOf(getInitialAiGreeting()))
    val aiMessages: StateFlow<List<AiChatMessage>> = _aiMessages.asStateFlow()

    private val _aiChatState = MutableStateFlow<AiChatState>(AiChatState.Idle)
    val aiChatState: StateFlow<AiChatState> = _aiChatState.asStateFlow()

    val suggestedAiPrompts = listOf(
        "🌸 আজকের একটি দু'আ ও উপদেশ দিন",
        "🤍 মন খুব অশান্ত লাগছে, একটু সান্ত্বনা দাও",
        "📖 পড়ার টেবিলে মনোযোগ বাড়ানোর সহজ কৌশল",
        "🌙 তাহাজ্জুদ ও রাতের যিকিরের ফজিলত কী?",
        "💪 অলসতা ও দীর্ঘসূত্রতা দূর করব কীভাবে?",
        "💧 সুস্থ থাকতে দৈনিক ৩টি সহজ ভালো অভ্যাস"
    )

    init {
        // Periodic ticker to keep prayer countdown up-to-date
        viewModelScope.launch {
            while (true) {
                delay(20000)
                _activePrayerInfo.value = PrayerTimesCalculator.getActivePrayerInfo(_prayerSchedule.value)
            }
        }
    }

    fun selectTab(tab: SakoonTab) {
        _currentTab.value = tab
    }

    // --- Prayer Times Actions ---
    private fun calculateCurrentPrayerSchedule(): PrayerSchedule {
        val loc = _currentLocation.value
        val method = _juristicMethod.value
        return PrayerTimesCalculator.calculate(
            latitude = loc.latitude,
            longitude = loc.longitude,
            juristicMethod = method,
            locationName = loc.nameBn
        )
    }

    private fun refreshPrayerSchedule() {
        val schedule = calculateCurrentPrayerSchedule()
        _prayerSchedule.value = schedule
        _activePrayerInfo.value = PrayerTimesCalculator.getActivePrayerInfo(schedule)
    }

    fun selectCityLocation(city: CityLocation) {
        locationHelper.saveLocation(city)
        _currentLocation.value = city
        refreshPrayerSchedule()
        emitMessage("নামাজের ওয়াক্তের স্থান: ${city.nameBn}")
    }

    fun refreshGpsLocation() {
        locationHelper.requestCurrentGpsLocation { loc ->
            _currentLocation.value = loc
            refreshPrayerSchedule()
            emitMessage("জিপিএস অবস্থান হালনাগাদ: ${loc.nameBn}")
        }
    }

    fun toggleJuristicMethod() {
        val newMethod = if (_juristicMethod.value == JuristicMethod.HANAFI) {
            JuristicMethod.STANDARD
        } else {
            JuristicMethod.HANAFI
        }
        locationHelper.saveJuristicMethod(newMethod)
        _juristicMethod.value = newMethod
        refreshPrayerSchedule()
        emitMessage("আসরের ওয়াক্ত পরিবর্তন: ${newMethod.labelBn}")
    }

    // Profile
    fun updateProfile(displayName: String, partnerName: String) {
        repository.saveUserProfile(displayName, partnerName)
        emitMessage("নাম পরিবর্তন সংরক্ষণ করা হয়েছে")
    }

    // Habits
    fun toggleHabit(habitId: String) {
        repository.toggleDailyHabit(habitId)
    }

    // Water
    fun addWater() {
        repository.incrementWater()
    }

    fun removeWater() {
        repository.decrementWater()
    }

    fun resetWater() {
        repository.resetWater()
    }

    // Movement
    fun setMovement(activity: String, minutes: Int, isDone: Boolean) {
        repository.updateMovement(activity, minutes, isDone)
    }

    fun resetMovement() {
        repository.resetMovement()
    }

    // Faith
    fun toggleFaith(faithId: String) {
        repository.toggleFaithHabit(faithId)
    }

    // Daily Dhikr Actions
    fun selectDhikrPhrase(phraseId: String) {
        repository.selectDhikrPhrase(phraseId)
        val phrase = SakoonRepository.DEFAULT_DHIKR_PHRASES.find { it.id == phraseId }
        emitMessage("নির্বাচিত জিকির: ${phrase?.transliteration ?: ""}")
    }

    fun setDhikrGoal(goal: Int) {
        repository.setDhikrGoal(selectedDhikrId.value, goal)
        emitMessage("লক্ষ্য নির্ধারণ: ${repository.toBanglaNumber(goal)} বার")
    }

    fun incrementDhikr() {
        val reachedGoal = repository.incrementDhikr()
        triggerGentleTapFeedback()
        if (reachedGoal) {
            triggerVibration()
            emitMessage("মাশাআল্লাহ! আজকের লক্ষ্য সম্পন্ন হয়েছে 🌸")
        }
    }

    fun decrementDhikr() {
        repository.decrementDhikr()
    }

    fun resetDhikr() {
        repository.resetDhikr()
        emitMessage("তাসবিহ গণনা রিসেট করা হয়েছে")
    }

    private fun triggerGentleTapFeedback() {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            }
        } catch (_: Exception) {}
    }

    // Study
    fun addTask(title: String) {
        val success = repository.addStudyTask(title)
        if (!success) {
            emitMessage("টাস্কের নাম ফাঁকা রাখা যাবে না")
        }
    }

    fun toggleTask(taskId: String) {
        repository.toggleStudyTask(taskId)
    }

    fun deleteTask(taskId: String) {
        repository.deleteStudyTask(taskId)
    }

    fun clearCompletedTasks() {
        repository.clearCompletedStudyTasks()
        emitMessage("সম্পন্ন টাস্কগুলো মুছে ফেলা হয়েছে")
    }

    // Timer Controls
    fun setTimerPreset(minutes: Int) {
        timerJob?.cancel()
        _timerMinutes.value = minutes
        _remainingSeconds.value = minutes * 60
        _timerStatus.value = TimerStatus.IDLE
    }

    fun startTimer() {
        if (_timerStatus.value == TimerStatus.RUNNING) return
        _timerStatus.value = TimerStatus.RUNNING
        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0) {
                delay(1000)
                _remainingSeconds.value -= 1
            }
            _timerStatus.value = TimerStatus.COMPLETED
            triggerVibration()
            emitMessage("অভিনন্দন! আপনার ফোকাস সেশন সমাপ্ত হয়েছে 🌸")
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _timerStatus.value = TimerStatus.PAUSED
    }

    fun resetTimer() {
        timerJob?.cancel()
        _remainingSeconds.value = _timerMinutes.value * 60
        _timerStatus.value = TimerStatus.IDLE
    }

    private fun triggerVibration() {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 200), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 200), -1)
                )
            }
        } catch (_: Exception) {
            // Safe fallback if vibration hardware unavailable
        }
    }

    // Heart / Mood
    fun selectMood(moodLabel: String) {
        repository.setMood(moodLabel)
        emitMessage("মনোভাব সংরক্ষিত হয়েছে")
    }

    fun updateJournal(text: String) {
        repository.saveJournalText(text)
    }

    fun saveDraft(draft: String) {
        repository.saveMissingDraft(draft)
        emitMessage("মেসেজের ড্রাফট সংরক্ষিত হয়েছে")
    }

    fun resetDraft() {
        repository.resetMissingDraft()
        emitMessage("মেসেজের ড্রাফট ডিফল্টে ফিরিয়ে নেওয়া হয়েছে")
    }

    fun copyToClipboard(text: String) {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("FLUTTERISTA Draft", text)
        clipboard?.setPrimaryClip(clip)
        emitMessage("মেসেজ ক্লিপবোর্ডে কপি হয়েছে 📋")
    }

    // Breathing Guide
    fun startBreathing() {
        breathingJob?.cancel()
        _breathingActive.value = true
        _breathingRemaining.value = 60
        _breathingPhase.value = BreathingPhase.INHALE

        breathingJob = viewModelScope.launch {
            var phaseCounter = 0
            val phases = listOf(BreathingPhase.INHALE, BreathingPhase.HOLD, BreathingPhase.EXHALE)
            var currentPhaseIndex = 0

            while (_breathingRemaining.value > 0) {
                delay(1000)
                _breathingRemaining.value -= 1
                phaseCounter += 1

                if (phaseCounter >= phases[currentPhaseIndex].seconds) {
                    phaseCounter = 0
                    currentPhaseIndex = (currentPhaseIndex + 1) % phases.size
                    _breathingPhase.value = phases[currentPhaseIndex]
                }
            }
            _breathingActive.value = false
            emitMessage("শ্বাস-প্রশ্বাসের অনুশীলন সম্পন্ন হয়েছে 🌸 প্রশান্তি অনুভব করুন।")
        }
    }

    fun stopBreathing() {
        breathingJob?.cancel()
        _breathingActive.value = false
        _breathingRemaining.value = 60
        _breathingPhase.value = BreathingPhase.INHALE
    }

    // Data Management
    fun clearAllData() {
        timerJob?.cancel()
        breathingJob?.cancel()
        _timerStatus.value = TimerStatus.IDLE
        _remainingSeconds.value = 25 * 60
        _breathingActive.value = false
        repository.clearAllData()
        selectCityLocation(LocationManagerHelper.DEFAULT_CITIES.first())
        emitMessage("সব তথ্য মুছে ফেলা হয়েছে")
    }

    private fun emitMessage(msg: String) {
        viewModelScope.launch {
            _uiEvents.emit(msg)
        }
    }

    // --- Sakoon AI Companion Actions ---

    fun openAiCompanion(initialPrompt: String? = null) {
        _isAiCompanionOpen.value = true
        if (!initialPrompt.isNullOrBlank()) {
            sendAiMessage(initialPrompt)
        }
    }

    fun closeAiCompanion() {
        _isAiCompanionOpen.value = false
    }

    fun sendAiMessage(prompt: String) {
        val cleanPrompt = prompt.trim()
        if (cleanPrompt.isBlank()) return

        val userMessage = AiChatMessage(
            sender = MessageSender.USER,
            text = cleanPrompt
        )
        val assistantMessageId = UUID.randomUUID().toString()
        val initialAssistantMessage = AiChatMessage(
            id = assistantMessageId,
            sender = MessageSender.SAKOON_AI,
            text = "",
            isStreaming = true
        )

        val previousHistory = _aiMessages.value.filter { !it.isStreaming && !it.hasError }
        _aiMessages.value = _aiMessages.value + userMessage + initialAssistantMessage
        _aiChatState.value = AiChatState.Streaming("")

        viewModelScope.launch {
            val accumulated = StringBuilder()
            try {
                aiService.streamChatResponse(previousHistory, cleanPrompt).collect { chunk ->
                    accumulated.append(chunk)
                    val currentText = accumulated.toString()
                    _aiChatState.value = AiChatState.Streaming(currentText)
                    _aiMessages.value = _aiMessages.value.map { msg ->
                        if (msg.id == assistantMessageId) {
                            msg.copy(text = currentText, isStreaming = true)
                        } else msg
                    }
                }

                val finalText = accumulated.toString()
                if (finalText.isNotBlank()) {
                    _aiMessages.value = _aiMessages.value.map { msg ->
                        if (msg.id == assistantMessageId) {
                            msg.copy(text = finalText, isStreaming = false)
                        } else msg
                    }
                    _aiChatState.value = AiChatState.Idle
                } else {
                    val fallbackMsg = "মডেল থেকে কোনো উত্তর পাওয়া যায়নি।"
                    _aiMessages.value = _aiMessages.value.map { msg ->
                        if (msg.id == assistantMessageId) {
                            msg.copy(text = fallbackMsg, isStreaming = false, hasError = true)
                        } else msg
                    }
                    _aiChatState.value = AiChatState.Error(fallbackMsg)
                }
            } catch (e: TimeoutCancellationException) {
                Log.e("SakoonViewModel", "AI streaming timed out", e)
                val errorMsg = "অনুরোধের সময় শেষ হয়ে গেছে (৬০ সেকেন্ড)। অনুগ্রহ করে আবার চেষ্টা করুন।"
                _aiMessages.value = _aiMessages.value.map { msg ->
                    if (msg.id == assistantMessageId) {
                        msg.copy(text = errorMsg, isStreaming = false, hasError = true)
                    } else msg
                }
                _aiChatState.value = AiChatState.Error(errorMsg)
            } catch (e: CancellationException) {
                // Must always rethrow CancellationException
                throw e
            } catch (e: Exception) {
                Log.e("SakoonViewModel", "AI streaming error", e)
                val errorMsg = e.localizedMessage ?: "একটি ত্রুটি হয়েছে। অনুগ্রহ করে আবার চেষ্টা করুন।"
                _aiMessages.value = _aiMessages.value.map { msg ->
                    if (msg.id == assistantMessageId) {
                        msg.copy(text = errorMsg, isStreaming = false, hasError = true)
                    } else msg
                }
                _aiChatState.value = AiChatState.Error(errorMsg)
            }
        }
    }

    fun clearAiChat() {
        _aiMessages.value = listOf(getInitialAiGreeting())
        _aiChatState.value = AiChatState.Idle
        emitMessage("কথোপকথন পরিষ্কার করা হয়েছে")
    }

    fun retryLastAiMessage() {
        val lastUserMessage = _aiMessages.value.lastOrNull { it.sender == MessageSender.USER }
        if (lastUserMessage != null) {
            _aiMessages.value = _aiMessages.value.filter { !it.hasError && it.id != lastUserMessage.id }
            sendAiMessage(lastUserMessage.text)
        }
    }
}
