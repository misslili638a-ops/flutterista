package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SakoonRepository
import com.example.prayer.JuristicMethod
import com.example.ui.components.DailyDhikrCard
import com.example.ui.components.PrayerLocationDialog
import com.example.ui.theme.SakoonBlue
import com.example.ui.theme.SakoonBlueBg
import com.example.ui.theme.SakoonBlueBorder
import com.example.ui.theme.SakoonGreen
import com.example.ui.theme.SakoonOutline
import com.example.ui.theme.SakoonPrimary
import com.example.ui.theme.SakoonPrimaryContainer
import com.example.ui.theme.SakoonSurface
import com.example.ui.theme.SakoonTextPrimary
import com.example.ui.theme.SakoonTextSecondary
import com.example.ui.viewmodel.SakoonViewModel

@Composable
fun FaithScreen(
    viewModel: SakoonViewModel,
    modifier: Modifier = Modifier
) {
    val completedFaith by viewModel.completedFaith.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val juristicMethod by viewModel.juristicMethod.collectAsState()
    val schedule by viewModel.prayerSchedule.collectAsState()
    val activeInfo by viewModel.activePrayerInfo.collectAsState()

    var showLocationDialog by remember { mutableStateOf(false) }

    // Location permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
        if (granted) {
            viewModel.refreshGpsLocation()
        }
    }

    val dailyPrayerItems = listOf(
        PrayerDisplayItem("fajr", "ফজর", "ভোরের নামাজ", schedule.fajrTimeMillis, "০৪:৪৮ AM", "dawn"),
        PrayerDisplayItem("sunrise", "সূর্যোদয়", "নামাজ নিষিদ্ধ সময়", schedule.sunriseTimeMillis, "০৬:০৩ AM", "sunrise", isPrayer = false),
        PrayerDisplayItem("dhuhr", "জোহর", "দুপুরের নামাজ", schedule.dhuhrTimeMillis, "১১:৪৫ AM", "noon"),
        PrayerDisplayItem("asr", "আসর", "বিকালের নামাজ (${juristicMethod.labelBn.split(" ")[0]})", schedule.asrTimeMillis, "০৩:৫৬ PM", "afternoon"),
        PrayerDisplayItem("maghrib", "মাগরিব", "সূর্যাস্ত ও ইফতারের সময়", schedule.maghribTimeMillis, "০৫:২৪ PM", "sunset"),
        PrayerDisplayItem("isha", "এশা", "রাতের নামাজ", schedule.ishaTimeMillis, "০৬:৩৮ PM", "night")
    )

    val additionalHabits = listOf(
        SakoonRepository.DEFAULT_FAITH_HABITS.find { it.id == "quran" } ?: SakoonRepository.DEFAULT_FAITH_HABITS[5],
        SakoonRepository.DEFAULT_FAITH_HABITS.find { it.id == "dhikr" } ?: SakoonRepository.DEFAULT_FAITH_HABITS[6],
        SakoonRepository.DEFAULT_FAITH_HABITS.find { it.id == "kindness" } ?: SakoonRepository.DEFAULT_FAITH_HABITS[7]
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 90.dp)
    ) {
        // Section Title
        Column(modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)) {
            Text(
                text = "ইবাদত ও নামাজের সময়সূচি",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = SakoonTextPrimary
                )
            )
            Text(
                text = "আপনার অবস্থানের ভিত্তিতে নির্ভরযোগ্য নামাজের ওয়াক্ত ও আত্মিক প্রশান্তি",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SakoonTextSecondary
                )
            )
        }

        // Celestial Blue Digital Hero Card (Salat Dashboard)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color(0xFF1E3A8A).copy(alpha = 0.3f),
                    spotColor = SakoonBlue.copy(alpha = 0.45f)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1E3A8A),
                            Color(0xFF2563EB),
                            Color(0xFF3B82F6)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column {
                // Top Row: Location Pill & GPS action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.20f))
                            .clickable { showLocationDialog = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("location_pill_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = currentLocation.nameBn,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.25f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = activeInfo.currentWindowFormatted,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Next Prayer Countdown
                Text(
                    text = "পরবর্তী ওয়াক্ত: ${activeInfo.nextPrayerName}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        letterSpacing = (-0.3).sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "আর ${activeInfo.remainingFormatted} বাকি",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.95f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Fasting Sehri & Iftar Banner Pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌙 সেহরি শেষ: ", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                            Text(
                                text = schedule.getFormattedTime(schedule.fajrTimeMillis),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌅 ইফতার: ", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                            Text(
                                text = schedule.getFormattedTime(schedule.maghribTimeMillis),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Controls Pill Row (GPS Refresh & Juristic Toggle)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SakoonBlueBg)
                    .border(1.dp, SakoonBlue.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                    .clickable {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                    .padding(vertical = 9.dp, horizontal = 10.dp)
                    .testTag("gps_refresh_quick_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = null,
                        tint = SakoonBlue,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "জিপিএস রিফ্রেশ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SakoonBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SakoonSurface)
                    .border(1.dp, SakoonOutline, RoundedCornerShape(14.dp))
                    .clickable { viewModel.toggleJuristicMethod() }
                    .padding(vertical = 9.dp, horizontal = 10.dp)
                    .testTag("juristic_toggle_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = SakoonTextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "আসর: ${if (juristicMethod == JuristicMethod.HANAFI) "হানাফি" else "শাফেয়ী"}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SakoonTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Daily Prayer Times Table Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = Color.Black.copy(alpha = 0.04f),
                    spotColor = SakoonBlue.copy(alpha = 0.08f)
                )
                .clip(RoundedCornerShape(26.dp))
                .background(SakoonSurface)
                .border(
                    width = 1.dp,
                    color = SakoonOutline.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "দৈনিক নামাজের সঠিক ওয়াক্ত",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SakoonTextPrimary
                            )
                        )
                        Text(
                            text = "অক্ষাংশ ও দ্রাঘিমাংশ অনুসারে নিখুঁত হিসাব",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SakoonTextSecondary,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(SakoonBlueBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ওয়াক্ত তালিকা",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SakoonBlue,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                dailyPrayerItems.forEachIndexed { index, item ->
                    val isChecked = completedFaith.contains(item.id)
                    val formattedTime = schedule.getFormattedTime(item.timeMillis)
                    val isCurrent = activeInfo.currentPrayerName == item.name

                    val itemBg by animateColorAsState(
                        targetValue = if (isCurrent) SakoonBlueBg else Color.Transparent,
                        label = "prayer_bg"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(itemBg)
                            .clickable(enabled = item.isPrayer) {
                                viewModel.toggleFaith(item.id)
                            }
                            .padding(vertical = 10.dp, horizontal = 10.dp)
                            .testTag("prayer_row_${item.id}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (item.isPrayer) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isChecked) SakoonBlue else SakoonSurface)
                                        .border(1.5.dp, if (isChecked) SakoonBlue else SakoonOutline, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isChecked) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                            } else {
                                Icon(
                                    imageVector = Icons.Default.WbSunny,
                                    contentDescription = null,
                                    tint = SakoonTextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                            }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Bold,
                                            color = if (isCurrent) SakoonBlue else SakoonTextPrimary,
                                            fontSize = 15.sp
                                        )
                                    )
                                    if (isCurrent) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .background(SakoonBlue)
                                                .padding(horizontal = 7.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "চলতি ওয়াক্ত",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = item.subtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SakoonTextSecondary,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }

                        // Time pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isCurrent) SakoonBlue else SakoonSurface)
                                .border(1.dp, if (isCurrent) SakoonBlue else SakoonOutline, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = formattedTime,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isCurrent) Color.White else SakoonTextPrimary,
                                    fontSize = 12.5.sp
                                )
                            )
                        }
                    }

                    if (index < dailyPrayerItems.size - 1) {
                        HorizontalDivider(
                            color = SakoonOutline.copy(alpha = 0.5f),
                            thickness = 0.7.dp,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Daily Dhikr & Digital Tasbih Interactive Component
        DailyDhikrCard(viewModel = viewModel)

        Spacer(modifier = Modifier.height(20.dp))

        // Spiritual Habits Checklist (Quran, Dhikr, Kindness)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = Color.Black.copy(alpha = 0.04f),
                    spotColor = SakoonBlue.copy(alpha = 0.08f)
                )
                .clip(RoundedCornerShape(26.dp))
                .background(SakoonSurface)
                .border(
                    width = 1.dp,
                    color = SakoonOutline.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "দৈনিক আত্মিক আমল",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SakoonTextPrimary
                    )
                )
                Text(
                    text = "কুরআন তিলাওয়াত, দোয়া ও মানবকল্যাণ",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SakoonTextSecondary,
                        fontSize = 11.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                additionalHabits.forEachIndexed { index, habit ->
                    val isChecked = completedFaith.contains(habit.id)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.toggleFaith(habit.id) }
                            .padding(vertical = 10.dp, horizontal = 4.dp)
                            .testTag("spiritual_habit_${habit.id}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isChecked) SakoonBlue else SakoonSurface)
                                .border(1.5.dp, if (isChecked) SakoonBlue else SakoonOutline, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isChecked) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = habit.title,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Bold,
                                    fontSize = 14.5.sp,
                                    color = if (isChecked) SakoonBlue else SakoonTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = habit.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SakoonTextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    if (index < additionalHabits.size - 1) {
                        HorizontalDivider(
                            color = SakoonOutline.copy(alpha = 0.5f),
                            thickness = 0.7.dp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Gentle Reflective Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SakoonBlueBg)
                .border(1.dp, SakoonBlue.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🤍", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "ওয়াক্ত অনুযায়ী নামাজ মানসিক প্রশান্তি ও জীবনশৈলীতে শৃঙ্খলা আনে। যেকোনো প্রয়োজনে সময়সূচি মিলিয়ে নিন।",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SakoonTextSecondary,
                        lineHeight = 18.sp,
                        fontSize = 12.5.sp
                    )
                )
            }
        }
    }

    if (showLocationDialog) {
        PrayerLocationDialog(
            currentLocation = currentLocation,
            onSelectCity = { city -> viewModel.selectCityLocation(city) },
            onRequestGps = {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            onDismiss = { showLocationDialog = false }
        )
    }
}

private data class PrayerDisplayItem(
    val id: String,
    val name: String,
    val subtitle: String,
    val timeMillis: Long,
    val fallbackTime: String,
    val iconKey: String,
    val isPrayer: Boolean = true
)
