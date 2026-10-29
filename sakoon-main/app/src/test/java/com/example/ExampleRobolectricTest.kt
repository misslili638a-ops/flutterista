package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.SakoonRepository
import com.example.prayer.CityLocation
import com.example.prayer.JuristicMethod
import com.example.prayer.LocationManagerHelper
import com.example.prayer.PrayerTimesCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Date
import java.util.TimeZone

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var context: Context
    private lateinit var repository: SakoonRepository
    private lateinit var locationHelper: LocationManagerHelper

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        repository = SakoonRepository(context)
        locationHelper = LocationManagerHelper(context)
        repository.clearAllData()
    }

    @Test
    fun `read string from context`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("FLUTTERISTA", appName)
    }

    @Test
    fun `water counter edge cases - cannot go below zero`() {
        assertEquals(0, repository.waterCount.value)
        repository.decrementWater()
        assertEquals(0, repository.waterCount.value)

        repository.incrementWater()
        repository.incrementWater()
        assertEquals(2, repository.waterCount.value)

        repository.decrementWater()
        assertEquals(1, repository.waterCount.value)

        repository.resetWater()
        assertEquals(0, repository.waterCount.value)
    }

    @Test
    fun `daily habits toggle persists and updates correctly`() {
        assertTrue(repository.completedHabits.value.isEmpty())
        repository.toggleDailyHabit("self_time")
        assertTrue(repository.completedHabits.value.contains("self_time"))

        repository.toggleDailyHabit("self_time")
        assertFalse(repository.completedHabits.value.contains("self_time"))
    }

    @Test
    fun `study task CRUD rejects blank and handles completion and deletion`() {
        // Blank task rejected
        val blankAdded = repository.addStudyTask("   ")
        assertFalse(blankAdded)
        assertTrue(repository.studyTasks.value.isEmpty())

        // Valid task added
        val validAdded = repository.addStudyTask("অধ্যায় ৫ রিভিশন")
        assertTrue(validAdded)
        assertEquals(1, repository.studyTasks.value.size)
        val taskId = repository.studyTasks.value.first().id

        // Toggle task
        repository.toggleStudyTask(taskId)
        assertTrue(repository.studyTasks.value.first().isCompleted)

        // Clear completed
        repository.clearCompletedStudyTasks()
        assertTrue(repository.studyTasks.value.isEmpty())
    }

    @Test
    fun `profile update saves display and partner names`() {
        repository.saveUserProfile("সাদিয়া", "শোহান")
        assertEquals("সাদিয়া", repository.userProfile.value.displayName)
        assertEquals("শোহান", repository.userProfile.value.partnerName)
    }

    @Test
    fun `prayer times calculation calculates chronological times for Dhaka`() {
        val schedule = PrayerTimesCalculator.calculate(
            latitude = 23.8103,
            longitude = 90.4125,
            date = Date(),
            timeZone = TimeZone.getTimeZone("Asia/Dhaka"),
            juristicMethod = JuristicMethod.HANAFI,
            locationName = "ঢাকা"
        )

        // Verify chronological sequence: Fajr < Sunrise < Dhuhr < Asr < Maghrib < Isha
        assertTrue("Fajr must be before Sunrise", schedule.fajrTimeMillis < schedule.sunriseTimeMillis)
        assertTrue("Sunrise must be before Dhuhr", schedule.sunriseTimeMillis < schedule.dhuhrTimeMillis)
        assertTrue("Dhuhr must be before Asr", schedule.dhuhrTimeMillis < schedule.asrTimeMillis)
        assertTrue("Asr must be before Maghrib", schedule.asrTimeMillis < schedule.maghribTimeMillis)
        assertTrue("Maghrib must be before Isha", schedule.maghribTimeMillis < schedule.ishaTimeMillis)

        // Verify formatted time string
        val fajrFormatted = schedule.getFormattedTime(schedule.fajrTimeMillis)
        assertTrue(fajrFormatted.contains("AM"))
    }

    @Test
    fun `asr calculation differs between Hanafi and Standard Shafi_i juristic methods`() {
        val hanafiSchedule = PrayerTimesCalculator.calculate(
            latitude = 23.8103,
            longitude = 90.4125,
            juristicMethod = JuristicMethod.HANAFI
        )
        val shafiiSchedule = PrayerTimesCalculator.calculate(
            latitude = 23.8103,
            longitude = 90.4125,
            juristicMethod = JuristicMethod.STANDARD
        )

        // Hanafi 2x shadow Asr time is later in the afternoon than 1x shadow Shafi'i
        assertTrue(hanafiSchedule.asrTimeMillis > shafiiSchedule.asrTimeMillis)
    }

    @Test
    fun `location helper saves and retrieves location`() {
        val testCity = CityLocation("চট্টগ্রাম", "Chattogram", 22.3569, 91.7832)
        locationHelper.saveLocation(testCity)
        val saved = locationHelper.getSavedLocation()
        assertEquals("চট্টগ্রাম", saved.nameBn)
        assertEquals(22.3569, saved.latitude, 0.01)
    }

    @Test
    fun `dhikr counter increments and updates lifetime total`() {
        assertEquals(0, repository.dhikrTodayCount.value)
        val initialLifetime = repository.dhikrLifetimeTotal.value

        val reached1 = repository.incrementDhikr()
        assertEquals(1, repository.dhikrTodayCount.value)
        assertEquals(initialLifetime + 1, repository.dhikrLifetimeTotal.value)
        assertFalse(reached1)

        // Set a small goal and test goal completion signal
        repository.setDhikrGoal(3)
        repository.incrementDhikr() // count = 2
        val reachedGoal = repository.incrementDhikr() // count = 3
        assertEquals(3, repository.dhikrTodayCount.value)
        assertTrue(reachedGoal)
    }

    @Test
    fun `dhikr counter decrement does not go below zero`() {
        assertEquals(0, repository.dhikrTodayCount.value)
        repository.decrementDhikr()
        assertEquals(0, repository.dhikrTodayCount.value)

        repository.incrementDhikr()
        repository.incrementDhikr()
        assertEquals(2, repository.dhikrTodayCount.value)

        repository.decrementDhikr()
        assertEquals(1, repository.dhikrTodayCount.value)
    }

    @Test
    fun `dhikr reset zeroes today count while preserving lifetime total`() {
        repository.incrementDhikr()
        repository.incrementDhikr()
        repository.incrementDhikr()
        val lifetime = repository.dhikrLifetimeTotal.value

        assertEquals(3, repository.dhikrTodayCount.value)
        repository.resetDhikr()

        assertEquals(0, repository.dhikrTodayCount.value)
        assertEquals(lifetime, repository.dhikrLifetimeTotal.value)
    }

    @Test
    fun `dhikr phrase selection and goal switching`() {
        assertEquals("subhanallah", repository.selectedDhikrId.value)
        repository.selectDhikrPhrase("astaghfirullah")
        assertEquals("astaghfirullah", repository.selectedDhikrId.value)
        assertEquals(100, repository.dhikrGoal.value)

        repository.setDhikrGoal(500)
        assertEquals(500, repository.dhikrGoal.value)
    }

    @Test
    fun `ai companion navigation and message clearing test`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = com.example.ui.viewmodel.SakoonViewModel(app)

        assertFalse(viewModel.isAiCompanionOpen.value)
        viewModel.openAiCompanion()
        assertTrue(viewModel.isAiCompanionOpen.value)

        viewModel.closeAiCompanion()
        assertFalse(viewModel.isAiCompanionOpen.value)

        // Initial message should be the Sakoon AI greeting
        val initialMessages = viewModel.aiMessages.value
        assertEquals(1, initialMessages.size)
        assertEquals(com.example.ai.MessageSender.SAKOON_AI, initialMessages.first().sender)
        assertTrue(initialMessages.first().text.contains("FLUTTERISTA"))

        // Prompts should be non-empty
        assertTrue(viewModel.suggestedAiPrompts.isNotEmpty())

        viewModel.clearAiChat()
        assertEquals(1, viewModel.aiMessages.value.size)
    }
}

