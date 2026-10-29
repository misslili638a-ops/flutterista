package com.example.prayer

import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.*

enum class JuristicMethod(val labelBn: String) {
    HANAFI("হানাফি (প্রচলিত)"),
    STANDARD("শাফেয়ী / মালেকী / হাম্বলী")
}

data class PrayerSchedule(
    val fajrTimeMillis: Long,
    val sunriseTimeMillis: Long,
    val dhuhrTimeMillis: Long,
    val asrTimeMillis: Long,
    val maghribTimeMillis: Long,
    val ishaTimeMillis: Long,
    val ishraqTimeMillis: Long,
    val midnightTimeMillis: Long,
    val locationName: String,
    val juristicMethod: JuristicMethod,
    val timeZone: TimeZone = TimeZone.getDefault()
) {
    fun getFormattedTime(timeMillis: Long): String {
        val cal = Calendar.getInstance(timeZone).apply { timeInMillis = timeMillis }
        val hour24 = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val hour12 = if (hour24 % 12 == 0) 12 else hour24 % 12
        val amPm = if (hour24 < 12) "AM" else "PM"

        val hourStr = toBanglaNumber(hour12)
        val minStr = String.format(Locale.US, "%02d", minute).map { toBanglaDigit(it) }.joinToString("")
        return "$hourStr:$minStr $amPm"
    }

    private fun toBanglaDigit(c: Char): Char = when (c) {
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
        else -> c
    }

    private fun toBanglaNumber(num: Int): String {
        return num.toString().map { toBanglaDigit(it) }.joinToString("")
    }
}

data class ActivePrayerInfo(
    val currentPrayerName: String,
    val nextPrayerName: String,
    val remainingMillis: Long,
    val remainingFormatted: String,
    val currentWindowFormatted: String
)

object PrayerTimesCalculator {

    fun calculate(
        latitude: Double,
        longitude: Double,
        date: Date = Date(),
        timeZone: TimeZone = TimeZone.getDefault(),
        juristicMethod: JuristicMethod = JuristicMethod.HANAFI,
        fajrAngle: Double = 18.0,
        ishaAngle: Double = 18.0,
        locationName: String = "ঢাকা"
    ): PrayerSchedule {
        val cal = Calendar.getInstance(timeZone).apply { time = date }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val tzOffsetHours = timeZone.getOffset(date.time) / 3600000.0
        val jd = julianDate(year, month, day)

        // Midday (Noon)
        val sun = sunCoordinates(jd)
        val midDay = 12.0 + tzOffsetHours - (longitude / 15.0) - sun.equationOfTime

        // Sun angle times
        val fajrDiff = sunAngleTime(jd, fajrAngle, latitude, isMorning = true)
        val sunriseDiff = sunAngleTime(jd, 0.833, latitude, isMorning = true)
        val sunsetDiff = sunAngleTime(jd, 0.833, latitude, isMorning = false)
        val ishaDiff = sunAngleTime(jd, ishaAngle, latitude, isMorning = false)

        val shadowFactor = if (juristicMethod == JuristicMethod.HANAFI) 2 else 1
        val asrDiff = asrHour(jd, latitude, shadowFactor)

        val fajrHours = midDay + fajrDiff
        val sunriseHours = midDay + sunriseDiff
        val dhuhrHours = midDay + (2.0 / 60.0) // 2 min safety margin
        val asrHours = midDay + asrDiff
        val maghribHours = midDay + sunsetDiff + (2.0 / 60.0)
        val ishaHours = midDay + ishaDiff
        val ishraqHours = sunriseHours + (15.0 / 60.0) // 15 mins after sunrise

        val midnightHours = (sunsetDiff + fajrDiff + 24.0) / 2.0 + midDay

        return PrayerSchedule(
            fajrTimeMillis = hoursToMillis(cal, fajrHours),
            sunriseTimeMillis = hoursToMillis(cal, sunriseHours),
            dhuhrTimeMillis = hoursToMillis(cal, dhuhrHours),
            asrTimeMillis = hoursToMillis(cal, asrHours),
            maghribTimeMillis = hoursToMillis(cal, maghribHours),
            ishaTimeMillis = hoursToMillis(cal, ishaHours),
            ishraqTimeMillis = hoursToMillis(cal, ishraqHours),
            midnightTimeMillis = hoursToMillis(cal, midnightHours),
            locationName = locationName,
            juristicMethod = juristicMethod,
            timeZone = timeZone
        )
    }

    fun getActivePrayerInfo(schedule: PrayerSchedule, nowMillis: Long = System.currentTimeMillis()): ActivePrayerInfo {
        val prayers = listOf(
            Triple("ফজর", schedule.fajrTimeMillis, schedule.sunriseTimeMillis),
            Triple("সূর্যোদয়", schedule.sunriseTimeMillis, schedule.dhuhrTimeMillis),
            Triple("জোহর", schedule.dhuhrTimeMillis, schedule.asrTimeMillis),
            Triple("আসর", schedule.asrTimeMillis, schedule.maghribTimeMillis),
            Triple("মাগরিব", schedule.maghribTimeMillis, schedule.ishaTimeMillis),
            Triple("এশা", schedule.ishaTimeMillis, schedule.fajrTimeMillis + 24 * 3600 * 1000L)
        )

        var currentPrayer = "এশা"
        var nextPrayer = "ফজর"
        var nextPrayerTime = schedule.fajrTimeMillis + 24 * 3600 * 1000L
        var currentWindow = "এশার ওয়াক্ত"

        if (nowMillis < schedule.fajrTimeMillis) {
            currentPrayer = "তাহাজ্জুদ / সেহরি"
            nextPrayer = "ফজর"
            nextPrayerTime = schedule.fajrTimeMillis
            currentWindow = "সেহরি শেষ ও ফজরের অপেক্ষা"
        } else if (nowMillis in schedule.fajrTimeMillis until schedule.sunriseTimeMillis) {
            currentPrayer = "ফজর"
            nextPrayer = "সূর্যোদয়"
            nextPrayerTime = schedule.sunriseTimeMillis
            currentWindow = "ফজরের ওয়াক্ত চলছে"
        } else if (nowMillis in schedule.sunriseTimeMillis until schedule.dhuhrTimeMillis) {
            currentPrayer = "ইশরাক / চাশত"
            nextPrayer = "জোহর"
            nextPrayerTime = schedule.dhuhrTimeMillis
            currentWindow = "জোহরের ওয়াক্তের অপেক্ষা"
        } else if (nowMillis in schedule.dhuhrTimeMillis until schedule.asrTimeMillis) {
            currentPrayer = "জোহর"
            nextPrayer = "আসর"
            nextPrayerTime = schedule.asrTimeMillis
            currentWindow = "জোহরের ওয়াক্ত চলছে"
        } else if (nowMillis in schedule.asrTimeMillis until schedule.maghribTimeMillis) {
            currentPrayer = "আসর"
            nextPrayer = "মাগরিব (ইফতার)"
            nextPrayerTime = schedule.maghribTimeMillis
            currentWindow = "আসরের ওয়াক্ত চলছে"
        } else if (nowMillis in schedule.maghribTimeMillis until schedule.ishaTimeMillis) {
            currentPrayer = "মাগরিব"
            nextPrayer = "এশা"
            nextPrayerTime = schedule.ishaTimeMillis
            currentWindow = "মাগরিবের ওয়াক্ত চলছে"
        } else {
            currentPrayer = "এশা"
            nextPrayer = "ফজর (আগামীকাল)"
            nextPrayerTime = schedule.fajrTimeMillis + 24 * 3600 * 1000L
            currentWindow = "এশার ওয়াক্ত চলছে"
        }

        val remainingMillis = (nextPrayerTime - nowMillis).coerceAtLeast(0L)
        val remainingHours = remainingMillis / 3600000L
        val remainingMins = (remainingMillis % 3600000L) / 60000L

        val remText = if (remainingHours > 0) {
            "${toBangla(remainingHours.toInt())} ঘণ্টা ${toBangla(remainingMins.toInt())} মিনিট"
        } else {
            "${toBangla(remainingMins.toInt())} মিনিট"
        }

        return ActivePrayerInfo(
            currentPrayerName = currentPrayer,
            nextPrayerName = nextPrayer,
            remainingMillis = remainingMillis,
            remainingFormatted = remText,
            currentWindowFormatted = currentWindow
        )
    }

    private fun hoursToMillis(baseCal: Calendar, hours: Double): Long {
        val cal = baseCal.clone() as Calendar
        var normalizedHours = hours
        var dayOffset = 0
        while (normalizedHours >= 24.0) {
            normalizedHours -= 24.0
            dayOffset += 1
        }
        while (normalizedHours < 0.0) {
            normalizedHours += 24.0
            dayOffset -= 1
        }
        val intHour = normalizedHours.toInt()
        val remainderMinutes = (normalizedHours - intHour) * 60.0
        val intMinute = remainderMinutes.toInt()
        val intSecond = ((remainderMinutes - intMinute) * 60.0).toInt()

        cal.add(Calendar.DAY_OF_MONTH, dayOffset)
        cal.set(Calendar.HOUR_OF_DAY, intHour)
        cal.set(Calendar.MINUTE, intMinute)
        cal.set(Calendar.SECOND, intSecond)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun julianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private data class SunCoords(val declination: Double, val equationOfTime: Double)

    private fun sunCoordinates(jd: Double): SunCoords {
        val d = jd - 2451545.0
        val g = Math.toRadians((357.529 + 0.98560028 * d) % 360.0)
        val q = (280.459 + 0.98564736 * d) % 360.0
        val l = Math.toRadians((q + 1.915 * sin(g) + 0.020 * sin(2 * g)) % 360.0)
        val e = Math.toRadians(23.439 - 0.00000036 * d)

        val ra = atan2(cos(e) * sin(l), cos(l))
        var raDeg = Math.toDegrees(ra)
        if (raDeg < 0) raDeg += 360.0
        val raHours = raDeg / 15.0

        val sinDec = sin(e) * sin(l)
        val dec = asin(sinDec)

        val lHours = (Math.toDegrees(l) % 360.0) / 15.0
        var eqt = lHours - raHours
        if (eqt > 12) eqt -= 24
        if (eqt < -12) eqt += 24

        return SunCoords(dec, eqt)
    }

    private fun sunAngleTime(jd: Double, angle: Double, lat: Double, isMorning: Boolean): Double {
        val sun = sunCoordinates(jd)
        val latRad = Math.toRadians(lat)
        val sinAngle = sin(Math.toRadians(-angle))
        val cosH = (sinAngle - sin(latRad) * sin(sun.declination)) / (cos(latRad) * cos(sun.declination))
        if (cosH > 1.0) return if (isMorning) -12.0 else 12.0
        if (cosH < -1.0) return 0.0
        val h = Math.toDegrees(acos(cosH)) / 15.0
        return if (isMorning) -h else h
    }

    private fun asrHour(jd: Double, lat: Double, shadowFactor: Int): Double {
        val sun = sunCoordinates(jd)
        val latRad = Math.toRadians(lat)
        val diff = abs(latRad - sun.declination)
        val angleRad = atan(1.0 / (shadowFactor + tan(diff)))
        val sinAngle = sin(angleRad)
        val cosH = (sinAngle - sin(latRad) * sin(sun.declination)) / (cos(latRad) * cos(sun.declination))
        if (cosH > 1.0 || cosH < -1.0) return 3.0
        return Math.toDegrees(acos(cosH)) / 15.0
    }

    private fun toBangla(num: Int): String {
        return num.toString().map {
            when (it) {
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
                else -> it
            }
        }.joinToString("")
    }
}
