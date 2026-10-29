package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.DhikrPhrase
import com.example.data.model.HabitItem
import com.example.data.model.StudyTask
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class SakoonRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sakoon_local_data", Context.MODE_PRIVATE)

    companion object {
        val DEFAULT_DAILY_HABITS = listOf(
            HabitItem(
                id = "self_time",
                title = "নিজের জন্য ৫ মিনিট সময় রাখা",
                subtitle = "আজকের দিনটা ধীরে শুরু করুন।"
            ),
            HabitItem(
                id = "movement",
                title = "শরীরকে আরামদায়কভাবে নড়াচড়া করানো",
                subtitle = "হাঁটা, নাচ, স্ট্রেচিং বা পছন্দের কিছু।"
            ),
            HabitItem(
                id = "nourish_meal",
                title = "পরিপূর্ণ ও পুষ্টিকর একটি খাবার খাওয়া",
                subtitle = "ক্ষুধা ও শরীরের প্রয়োজনকে সম্মান করুন।"
            ),
            HabitItem(
                id = "study_focus",
                title = "পড়াশোনায় অন্তত একটি ফোকাস সেশন",
                subtitle = "অল্প অল্প করেও এগোনো যায়।"
            ),
            HabitItem(
                id = "kind_self_talk",
                title = "নিজের সঙ্গে সদয় থাকা",
                subtitle = "আপনার মূল্য শরীরের মাপ দিয়ে নির্ধারিত নয়।"
            )
        )

        val DEFAULT_FAITH_HABITS = listOf(
            HabitItem("fajr", "ফজরের নামাজ", "ভোরের শুরু আল্লাহর স্মরণে ও শান্তিতে"),
            HabitItem("dhuhr", "জোহরের নামাজ", "দিনের ব্যস্ততার মাঝে প্রশান্তিকর বিরতি"),
            HabitItem("asr", "আসরের নামাজ", "বিকালে আত্মিক সংযোগ ও স্থিরতা"),
            HabitItem("maghrib", "মাগরিবের নামাজ", "সূর্যাস্তের সময় শুকরিয়া ও দোয়া"),
            HabitItem("isha", "এশার নামাজ", "দিনের সমাপ্তিতে একান্ত প্রার্থনা"),
            HabitItem("quran", "কুরআন তিলাওয়াত ও অর্থ", "কমপক্ষে ২-৩টি আয়াত অর্থসহ অনুধাবন"),
            HabitItem("dhikr", "দোয়া ও জিকির", "সকাল-সন্ধ্যার প্রশান্তিকর তাসবিহ"),
            HabitItem("kindness", "অন্যের প্রতি সদয় আচরণ", "একটি সুন্দর কথা, হাসিমুখ বা সাধ্যমতো দান")
        )

        val DEFAULT_DHIKR_PHRASES = listOf(
            DhikrPhrase(
                id = "subhanallah",
                arabic = "سُبْحَانَ اللَّهِ",
                transliteration = "সুবহানাল্লাহ",
                meaning = "আল্লাহ মহাপবিত্র ও নিষ্কলুষ",
                virtue = "প্রতিটি পাঠে জান্নাতে একটি করে বৃক্ষ রোপিত হয়",
                defaultGoal = 33
            ),
            DhikrPhrase(
                id = "alhamdulillah",
                arabic = "الْحَمْدُ لِلَّهِ",
                transliteration = "আলহামদুলিল্লাহ",
                meaning = "সকল প্রশংসা একমাত্র আল্লাহর জন্য",
                virtue = "মিজানের পাল্লাকে নেকি দিয়ে ভরপুর করে দেয়",
                defaultGoal = 33
            ),
            DhikrPhrase(
                id = "allahuakbar",
                arabic = "اللَّهُ أَكْبَرُ",
                transliteration = "আল্লাহু আকবার",
                meaning = "আল্লাহ সর্বশ্রেষ্ঠ ও মহান",
                virtue = "আসমান ও জমিনের মধ্যবর্তী শূন্যস্থান পূর্ণ করে",
                defaultGoal = 34
            ),
            DhikrPhrase(
                id = "lailahaillallah",
                arabic = "لَا إِلَٰهَ إِلَّا اللَّهُ",
                transliteration = "লা ইলাহা ইল্লাল্লাহ",
                meaning = "আল্লাহ ছাড়া সত্য কোনো উপাস্য নেই",
                virtue = "সর্বশ্রেষ্ঠ জিকির ও জান্নাতের চাবিকাঠি",
                defaultGoal = 100
            ),
            DhikrPhrase(
                id = "astaghfirullah",
                arabic = "أَسْتَغْفِرُ اللَّهَ",
                transliteration = "আস্তাগফিরুল্লাহ",
                meaning = "আমি আল্লাহর নিকট ক্ষমা প্রার্থনা করছি",
                virtue = "রিজিকে বরকত আনে ও সকল দুশ্চিন্তা দূর করে",
                defaultGoal = 100
            ),
            DhikrPhrase(
                id = "subhanallah_bihamdihi",
                arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
                transliteration = "সুবহানাল্লাহি ওয়া বিহামদিহী, সুবহানাল্লাহিল আজিম",
                meaning = "আল্লাহর পবিত্রতা ঘোষণা করছি তাঁর প্রশংসার সাথে, মহান আল্লাহ অতি পবিত্র",
                virtue = "মুখে উচ্চারণে সহজ কিন্তু মিজানের পাল্লায় অত্যন্ত ভারী",
                defaultGoal = 100
            ),
            DhikrPhrase(
                id = "durood",
                arabic = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ",
                transliteration = "আল্লাহুম্মা সাল্লি আলা মুহাম্মাদ",
                meaning = "হে আল্লাহ! মুহাম্মদ (সা.)-এর ওপর শান্তি ও রহমত বর্ষণ করুন",
                virtue = "একবার পাঠে ১০টি রহমত, ১০টি গুনাহ মাফ ও মর্যাদা বৃদ্ধি পায়",
                defaultGoal = 100
            ),
            DhikrPhrase(
                id = "lahawla",
                arabic = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
                transliteration = "লা হাওলা ওয়ালা কুওয়াতা ইল্লা বিল্লাহ",
                meaning = "আল্লাহর সাহায্য ছাড়া কোনো শক্তি ও সামর্থ্য নেই",
                virtue = "জান্নাতের অমূল্য রত্নভাণ্ডারের অন্যতম ধন",
                defaultGoal = 100
            )
        )
    }

    // Date Utilities
    fun getTodayDateKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    fun getTodayBengaliDisplay(): String {
        val cal = Calendar.getInstance()
        val dayOfWeek = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "রবি"
            Calendar.MONDAY -> "সোম"
            Calendar.TUESDAY -> "মঙ্গল"
            Calendar.WEDNESDAY -> "বুধ"
            Calendar.THURSDAY -> "বৃহঃ"
            Calendar.FRIDAY -> "শুক্র"
            Calendar.SATURDAY -> "শনি"
            else -> ""
        }
        val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH).toString().map { toBanglaDigit(it) }.joinToString("")
        val month = when (cal.get(Calendar.MONTH)) {
            Calendar.JANUARY -> "জানু"
            Calendar.FEBRUARY -> "ফেব্রু"
            Calendar.MARCH -> "মার্চ"
            Calendar.APRIL -> "এপ্রিল"
            Calendar.MAY -> "মে"
            Calendar.JUNE -> "জুন"
            Calendar.JULY -> "জুলাই"
            Calendar.AUGUST -> "আগস্ট"
            Calendar.SEPTEMBER -> "সেপ্টে"
            Calendar.OCTOBER -> "অক্টো"
            Calendar.NOVEMBER -> "নভে"
            Calendar.DECEMBER -> "ডিসে"
            else -> ""
        }
        return "$dayOfWeek $dayOfMonth $month"
    }

    fun toBanglaDigit(char: Char): Char = when (char) {
        '0' -> '০'
        '1' -> '১'
        '2' -> '২'
        '3' -> '৩'
        '4' -> '৪'
        '5' -> '৫'
        '6' -> '৬'
        '7' -> '৭'
        '8' -> '৮'
        '9' -> '৯'
        else -> char
    }

    fun toBanglaNumber(num: Int): String {
        return num.toString().map { toBanglaDigit(it) }.joinToString("")
    }

    // State Flows
    private val _userProfile = MutableStateFlow(loadUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _completedHabits = MutableStateFlow(loadCompletedHabits())
    val completedHabits: StateFlow<Set<String>> = _completedHabits.asStateFlow()

    private val _waterCount = MutableStateFlow(loadWaterCount())
    val waterCount: StateFlow<Int> = _waterCount.asStateFlow()

    private val _movementActivity = MutableStateFlow(loadMovementActivity())
    val movementActivity: StateFlow<String> = _movementActivity.asStateFlow()

    private val _movementMinutes = MutableStateFlow(loadMovementMinutes())
    val movementMinutes: StateFlow<Int> = _movementMinutes.asStateFlow()

    private val _movementDone = MutableStateFlow(loadMovementDone())
    val movementDone: StateFlow<Boolean> = _movementDone.asStateFlow()

    private val _completedFaith = MutableStateFlow(loadCompletedFaith())
    val completedFaith: StateFlow<Set<String>> = _completedFaith.asStateFlow()

    private val _studyTasks = MutableStateFlow(loadStudyTasks())
    val studyTasks: StateFlow<List<StudyTask>> = _studyTasks.asStateFlow()

    private val _mood = MutableStateFlow(loadMood())
    val mood: StateFlow<String?> = _mood.asStateFlow()

    private val _journalText = MutableStateFlow(loadJournalText())
    val journalText: StateFlow<String> = _journalText.asStateFlow()

    private val _missingDraft = MutableStateFlow(loadMissingDraft())
    val missingDraft: StateFlow<String> = _missingDraft.asStateFlow()

    // Dhikr State
    private val _selectedDhikrId = MutableStateFlow(prefs.getString("selected_dhikr_id", "subhanallah") ?: "subhanallah")
    val selectedDhikrId: StateFlow<String> = _selectedDhikrId.asStateFlow()

    private val _dhikrTodayCount = MutableStateFlow(loadDhikrTodayCount(_selectedDhikrId.value))
    val dhikrTodayCount: StateFlow<Int> = _dhikrTodayCount.asStateFlow()

    private val _dhikrGoal = MutableStateFlow(loadDhikrGoal(_selectedDhikrId.value))
    val dhikrGoal: StateFlow<Int> = _dhikrGoal.asStateFlow()

    private val _dhikrLifetimeTotal = MutableStateFlow(loadDhikrLifetimeTotal(_selectedDhikrId.value))
    val dhikrLifetimeTotal: StateFlow<Int> = _dhikrLifetimeTotal.asStateFlow()

    // Profile
    private fun loadUserProfile(): UserProfile {
        val display = prefs.getString("user_display_name", "প্রিয় মানুষ") ?: "প্রিয় মানুষ"
        val partner = prefs.getString("user_partner_name", "Shohan") ?: "Shohan"
        return UserProfile(display, partner)
    }

    fun saveUserProfile(displayName: String, partnerName: String) {
        val sanitizedDisplay = displayName.trim().ifEmpty { "প্রিয় মানুষ" }
        val sanitizedPartner = partnerName.trim().ifEmpty { "Shohan" }
        prefs.edit()
            .putString("user_display_name", sanitizedDisplay)
            .putString("user_partner_name", sanitizedPartner)
            .apply()
        _userProfile.value = UserProfile(sanitizedDisplay, sanitizedPartner)

        // If draft has not been modified manually, update draft with partner's name
        if (!prefs.getBoolean("has_custom_draft", false)) {
            val defaultDraft = createDefaultDraft(sanitizedPartner)
            _missingDraft.value = defaultDraft
        }
    }

    // Daily Habits
    private fun loadCompletedHabits(): Set<String> {
        val date = getTodayDateKey()
        return prefs.getStringSet("habits_$date", emptySet()) ?: emptySet()
    }

    fun toggleDailyHabit(habitId: String) {
        val date = getTodayDateKey()
        val current = _completedHabits.value.toMutableSet()
        if (current.contains(habitId)) {
            current.remove(habitId)
        } else {
            current.add(habitId)
        }
        prefs.edit().putStringSet("habits_$date", current).apply()
        _completedHabits.value = current
    }

    // Water Logger
    private fun loadWaterCount(): Int {
        val date = getTodayDateKey()
        return prefs.getInt("water_$date", 0)
    }

    fun incrementWater() {
        val date = getTodayDateKey()
        val updated = _waterCount.value + 1
        prefs.edit().putInt("water_$date", updated).apply()
        _waterCount.value = updated
    }

    fun decrementWater() {
        if (_waterCount.value <= 0) return
        val date = getTodayDateKey()
        val updated = (_waterCount.value - 1).coerceAtLeast(0)
        prefs.edit().putInt("water_$date", updated).apply()
        _waterCount.value = updated
    }

    fun resetWater() {
        val date = getTodayDateKey()
        prefs.edit().putInt("water_$date", 0).apply()
        _waterCount.value = 0
    }

    // Movement Tracker
    private fun loadMovementActivity(): String {
        val date = getTodayDateKey()
        return prefs.getString("move_act_$date", "হাঁটা") ?: "হাঁটা"
    }

    private fun loadMovementMinutes(): Int {
        val date = getTodayDateKey()
        return prefs.getInt("move_min_$date", 15)
    }

    private fun loadMovementDone(): Boolean {
        val date = getTodayDateKey()
        return prefs.getBoolean("move_done_$date", false)
    }

    fun updateMovement(activity: String, minutes: Int, isDone: Boolean) {
        val date = getTodayDateKey()
        prefs.edit()
            .putString("move_act_$date", activity)
            .putInt("move_min_$date", minutes)
            .putBoolean("move_done_$date", isDone)
            .apply()
        _movementActivity.value = activity
        _movementMinutes.value = minutes
        _movementDone.value = isDone
    }

    fun resetMovement() {
        val date = getTodayDateKey()
        prefs.edit()
            .putString("move_act_$date", "হাঁটা")
            .putInt("move_min_$date", 15)
            .putBoolean("move_done_$date", false)
            .apply()
        _movementActivity.value = "হাঁটা"
        _movementMinutes.value = 15
        _movementDone.value = false
    }

    // Faith Habits
    private fun loadCompletedFaith(): Set<String> {
        val date = getTodayDateKey()
        return prefs.getStringSet("faith_$date", emptySet()) ?: emptySet()
    }

    fun toggleFaithHabit(faithId: String) {
        val date = getTodayDateKey()
        val current = _completedFaith.value.toMutableSet()
        if (current.contains(faithId)) {
            current.remove(faithId)
        } else {
            current.add(faithId)
        }
        prefs.edit().putStringSet("faith_$date", current).apply()
        _completedFaith.value = current
    }

    // Study Tasks
    private fun loadStudyTasks(): List<StudyTask> {
        val jsonStr = prefs.getString("study_tasks", null) ?: return emptyList()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<StudyTask>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    StudyTask(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        isCompleted = obj.optBoolean("isCompleted", false),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun persistStudyTasks(tasks: List<StudyTask>) {
        val array = JSONArray()
        for (task in tasks) {
            val obj = JSONObject()
            obj.put("id", task.id)
            obj.put("title", task.title)
            obj.put("isCompleted", task.isCompleted)
            obj.put("createdAt", task.createdAt)
            array.put(obj)
        }
        prefs.edit().putString("study_tasks", array.toString()).apply()
        _studyTasks.value = tasks
    }

    fun addStudyTask(title: String): Boolean {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return false
        val cleanTitle = if (trimmed.length > 100) trimmed.substring(0, 100) else trimmed
        val newTask = StudyTask(
            id = UUID.randomUUID().toString(),
            title = cleanTitle,
            isCompleted = false
        )
        val updated = _studyTasks.value + newTask
        persistStudyTasks(updated)
        return true
    }

    fun toggleStudyTask(taskId: String) {
        val updated = _studyTasks.value.map {
            if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it
        }
        persistStudyTasks(updated)
    }

    fun deleteStudyTask(taskId: String) {
        val updated = _studyTasks.value.filter { it.id != taskId }
        persistStudyTasks(updated)
    }

    fun clearCompletedStudyTasks() {
        val updated = _studyTasks.value.filter { !it.isCompleted }
        persistStudyTasks(updated)
    }

    // Mood & Journal
    private fun loadMood(): String? {
        val date = getTodayDateKey()
        return prefs.getString("mood_$date", null)
    }

    fun setMood(moodLabel: String) {
        val date = getTodayDateKey()
        prefs.edit().putString("mood_$date", moodLabel).apply()
        _mood.value = moodLabel
    }

    private fun loadJournalText(): String {
        val date = getTodayDateKey()
        return prefs.getString("journal_$date", "") ?: ""
    }

    fun saveJournalText(text: String) {
        val date = getTodayDateKey()
        prefs.edit().putString("journal_$date", text).apply()
        _journalText.value = text
    }

    // Missing Draft
    private fun createDefaultDraft(partnerName: String): String {
        return "$partnerName, আজ তোমাকে একটু বেশিই মনে পড়ছে। ❤️ আশা করি তুমি ভালো আছো। আমিও নিজের দিনটা সুন্দরভাবে কাজে লাগানোর চেষ্টা করছি - তুমি ফ্রি হয়ে টেক্সট দিও।"
    }

    private fun loadMissingDraft(): String {
        val custom = prefs.getString("custom_draft", null)
        if (custom != null) return custom
        val partner = prefs.getString("user_partner_name", "Shohan") ?: "Shohan"
        return createDefaultDraft(partner)
    }

    fun saveMissingDraft(draft: String) {
        prefs.edit()
            .putString("custom_draft", draft)
            .putBoolean("has_custom_draft", true)
            .apply()
        _missingDraft.value = draft
    }

    fun resetMissingDraft() {
        val partner = _userProfile.value.partnerName
        val defaultDraft = createDefaultDraft(partner)
        prefs.edit()
            .remove("custom_draft")
            .putBoolean("has_custom_draft", false)
            .apply()
        _missingDraft.value = defaultDraft
    }

    // Dhikr Persistence & Actions
    private fun loadDhikrTodayCount(phraseId: String): Int {
        val date = getTodayDateKey()
        return prefs.getInt("dhikr_${phraseId}_$date", 0)
    }

    private fun loadDhikrGoal(phraseId: String): Int {
        val phrase = DEFAULT_DHIKR_PHRASES.find { it.id == phraseId } ?: DEFAULT_DHIKR_PHRASES.first()
        return prefs.getInt("dhikr_goal_$phraseId", phrase.defaultGoal)
    }

    private fun loadDhikrLifetimeTotal(phraseId: String): Int {
        return prefs.getInt("dhikr_total_$phraseId", 0)
    }

    fun selectDhikrPhrase(phraseId: String) {
        prefs.edit().putString("selected_dhikr_id", phraseId).apply()
        _selectedDhikrId.value = phraseId
        _dhikrTodayCount.value = loadDhikrTodayCount(phraseId)
        _dhikrGoal.value = loadDhikrGoal(phraseId)
        _dhikrLifetimeTotal.value = loadDhikrLifetimeTotal(phraseId)
    }

    fun setDhikrGoal(newGoal: Int) {
        setDhikrGoal(_selectedDhikrId.value, newGoal)
    }

    fun setDhikrGoal(phraseId: String, newGoal: Int) {
        val sanitized = newGoal.coerceIn(1, 10000)
        prefs.edit().putInt("dhikr_goal_$phraseId", sanitized).apply()
        if (_selectedDhikrId.value == phraseId) {
            _dhikrGoal.value = sanitized
        }
    }

    fun incrementDhikr(): Boolean {
        val phraseId = _selectedDhikrId.value
        val date = getTodayDateKey()
        val current = _dhikrTodayCount.value
        val newCount = current + 1
        val currentGoal = _dhikrGoal.value
        val newLifetime = _dhikrLifetimeTotal.value + 1

        prefs.edit()
            .putInt("dhikr_${phraseId}_$date", newCount)
            .putInt("dhikr_total_$phraseId", newLifetime)
            .apply()

        _dhikrTodayCount.value = newCount
        _dhikrLifetimeTotal.value = newLifetime

        return newCount == currentGoal
    }

    fun decrementDhikr() {
        val current = _dhikrTodayCount.value
        if (current <= 0) return
        val phraseId = _selectedDhikrId.value
        val date = getTodayDateKey()
        val newCount = current - 1
        val newTotal = (_dhikrLifetimeTotal.value - 1).coerceAtLeast(0)

        prefs.edit()
            .putInt("dhikr_${phraseId}_$date", newCount)
            .putInt("dhikr_total_$phraseId", newTotal)
            .apply()

        _dhikrTodayCount.value = newCount
        _dhikrLifetimeTotal.value = newTotal
    }

    fun resetDhikr() {
        val phraseId = _selectedDhikrId.value
        val date = getTodayDateKey()
        prefs.edit().putInt("dhikr_${phraseId}_$date", 0).apply()
        _dhikrTodayCount.value = 0
    }

    // Clear All Stored Data
    fun clearAllData() {
        prefs.edit().clear().apply()
        _userProfile.value = UserProfile("প্রিয় মানুষ", "Shohan")
        _completedHabits.value = emptySet()
        _waterCount.value = 0
        _movementActivity.value = "হাঁটা"
        _movementMinutes.value = 15
        _movementDone.value = false
        _completedFaith.value = emptySet()
        _studyTasks.value = emptyList()
        _mood.value = null
        _journalText.value = ""
        _missingDraft.value = createDefaultDraft("Shohan")

        _selectedDhikrId.value = "subhanallah"
        _dhikrTodayCount.value = 0
        _dhikrGoal.value = 33
        _dhikrLifetimeTotal.value = 0
    }
}
