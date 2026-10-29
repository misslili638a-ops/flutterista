package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.material.icons.filled.Mosque
import com.example.ui.theme.SakoonAmber
import com.example.ui.theme.SakoonAmberBg
import com.example.ui.theme.SakoonBlue
import com.example.ui.theme.SakoonBlueBg
import com.example.ui.theme.SakoonGreen
import com.example.ui.theme.SakoonGreenBg
import com.example.ui.theme.SakoonOutline
import com.example.ui.theme.SakoonPrimary
import com.example.ui.theme.SakoonPrimaryContainer
import com.example.ui.theme.SakoonPrimaryDark
import com.example.ui.theme.SakoonPrimaryGlow
import com.example.ui.theme.SakoonPrimaryLight
import com.example.ui.theme.SakoonPrimaryVariant
import com.example.ui.theme.SakoonRose
import com.example.ui.theme.SakoonRoseBg
import com.example.ui.theme.SakoonSurface
import com.example.ui.theme.SakoonTextPrimary
import com.example.ui.theme.SakoonTextSecondary
import com.example.ui.theme.SakoonTextTertiary
import com.example.ui.viewmodel.SakoonTab
import com.example.ui.viewmodel.SakoonViewModel

@Composable
fun TodayScreen(
    viewModel: SakoonViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val completedHabits by viewModel.completedHabits.collectAsState()
    val activePrayerInfo by viewModel.activePrayerInfo.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val totalHabits = SakoonRepository.DEFAULT_DAILY_HABITS.size
    val completedCount = completedHabits.size
    val progressRatio = if (totalHabits > 0) completedCount.toFloat() / totalHabits else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progressRatio,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "progress"
    )

    var editDisplayName by remember(userProfile.displayName) { mutableStateOf(userProfile.displayName) }
    var editPartnerName by remember(userProfile.partnerName) { mutableStateOf(userProfile.partnerName) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 90.dp) // extra padding so content flows cleanly above floating nav bar
    ) {
        // Hero Digital Iris Mesh Card (Android 16 / iOS 18 style)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = SakoonPrimary.copy(alpha = 0.25f),
                    spotColor = SakoonPrimary.copy(alpha = 0.38f)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            SakoonPrimary,
                            SakoonPrimaryVariant,
                            Color(0xFF9333EA)
                        )
                    )
                )
        ) {
            // Subtle glowing background ornament
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            )

            Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.14f),
                modifier = Modifier
                    .size(130.dp)
                    .align(Alignment.BottomEnd)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                // Header Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.20f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "আজকের ছোট্ট মনে করিয়ে দেওয়া",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    Text(
                        text = "✨ FLUTTERISTA ডেইলি",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "আপনি যত্ন পাওয়ার যোগ্য, ${userProfile.displayName} 💜",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        lineHeight = 30.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "সবকিছু একদিনে বদলাতে হবে না। নিজের জন্য একটি ছোট ভালো কাজ করাই আজকের সুন্দর শুরু।",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.95f),
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "নিজের গতি • নিজের পছন্দ",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SakoonPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.22f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "চাপমুক্ত জীবন",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --- Sakoon AI Companion Card (Android 16 / iOS 18 Glassmorphic Design) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = SakoonPrimary.copy(alpha = 0.12f),
                    spotColor = SakoonPrimary.copy(alpha = 0.22f)
                )
                .clip(RoundedCornerShape(26.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            SakoonSurface,
                            Color(0xFFF7F3FF),
                            Color(0xFFFAF5FF)
                        )
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.horizontalGradient(
                        listOf(SakoonPrimary.copy(alpha = 0.5f), Color(0xFFCE93D8).copy(alpha = 0.4f))
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
                .clickable { viewModel.openAiCompanion() }
                .padding(18.dp)
                .testTag("ai_companion_hero_card")
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        listOf(SakoonPrimary, SakoonPrimaryVariant)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "FLUTTERISTA এআই সঙ্গী",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SakoonTextPrimary,
                                        fontSize = 17.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(Color(0xFFE8F5E9))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "অনলাইন",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF2E7D32),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.5.sp
                                        )
                                    )
                                }
                            }
                            Text(
                                text = "Gemini 3.8 Flash • দৈনন্দিন যত্ন ও মনের প্রশান্তি",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SakoonTextSecondary,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    // Open Arrow
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SakoonPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "কথা বলুন",
                            tint = SakoonPrimaryDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "মনের কথা, পড়াশোনার কৌশল, দু'আ-যিকির কিংবা স্বাস্থ্যরীতি—যে কোনো বিষয়ে আন্তরিক সহায়তা পেতে এআই সঙ্গীর সাথে কথা বলুন।",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SakoonTextPrimary.copy(alpha = 0.85f),
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Prompt Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickPrompts = listOf(
                        "🌸 দু'আ ও উপদেশ" to "আজকের দিনের জন্য একটি সুন্দর দু'আ ও উপদেশ দিন",
                        "🤍 মন শান্ত করো" to "মন অশান্ত লাগছে, শান্ত হওয়ার জন্য কিছু ভালো কথা বলো",
                        "📖 পড়ার কৌশল" to "পড়ায় গভীর মনোযোগ ধরে রাখার বাস্তবসম্মত কৌশল কী?"
                    )
                    quickPrompts.forEach { (label, fullPrompt) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .border(1.dp, SakoonOutline.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                                .clickable { viewModel.openAiCompanion(fullPrompt) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SakoonPrimaryDark,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Daily Habits Checklist Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = Color.Black.copy(alpha = 0.04f),
                    spotColor = SakoonPrimary.copy(alpha = 0.08f)
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
                            text = "আজকের যত্নের তালিকা",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SakoonTextPrimary,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = "প্রতিটি ছোট কাজ আপনাকে প্রশান্তি দেয়",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SakoonTextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(SakoonPrimaryContainer)
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "${viewModel.repository.toBanglaNumber(completedCount)}/${viewModel.repository.toBanglaNumber(totalHabits)} সম্পন্ন",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SakoonPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Modern Linear Progress Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(9.dp)
                            .clip(RoundedCornerShape(50))
                            .background(SakoonPrimaryContainer.copy(alpha = 0.6f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = animatedProgress.coerceIn(0f, 1f))
                                .height(9.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(SakoonPrimary, SakoonPrimaryVariant)
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "${viewModel.repository.toBanglaNumber((progressRatio * 100).toInt())}%",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = SakoonPrimary,
                            fontSize = 13.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Modern Custom Checklist Items
                SakoonRepository.DEFAULT_DAILY_HABITS.forEachIndexed { index, habit ->
                    val isChecked = completedHabits.contains(habit.id)

                    val itemBg by animateColorAsState(
                        targetValue = if (isChecked) SakoonPrimaryLight else Color.Transparent,
                        label = "item_bg"
                    )

                    val checkScale by animateFloatAsState(
                        targetValue = if (isChecked) 1.05f else 1.0f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "check_scale"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(itemBg)
                            .clickable { viewModel.toggleHabit(habit.id) }
                            .padding(vertical = 11.dp, horizontal = 10.dp)
                            .testTag("habit_item_${habit.id}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Digital Circular Interactive Checkbox
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .scale(checkScale)
                                .clip(CircleShape)
                                .background(if (isChecked) SakoonPrimary else SakoonSurface)
                                .border(
                                    width = 1.5.dp,
                                    color = if (isChecked) SakoonPrimary else SakoonOutline,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isChecked) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = habit.title,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Bold,
                                    fontSize = 14.5.sp,
                                    color = if (isChecked) SakoonPrimary else SakoonTextPrimary
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

                    if (index < totalHabits - 1) {
                        HorizontalDivider(
                            color = SakoonOutline.copy(alpha = 0.5f),
                            thickness = 0.7.dp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Daily Prayer Times Quick Glance Pill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(22.dp),
                    ambientColor = Color.Black.copy(alpha = 0.03f),
                    spotColor = SakoonBlue.copy(alpha = 0.08f)
                )
                .clip(RoundedCornerShape(22.dp))
                .background(SakoonBlueBg)
                .border(
                    width = 1.dp,
                    color = SakoonBlue.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(22.dp)
                )
                .clickable { viewModel.selectTab(SakoonTab.FAITH) }
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("today_salat_glance")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SakoonBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mosque,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "পরবর্তী ওয়াক্ত: ${activePrayerInfo.nextPrayerName}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SakoonBlue,
                                    fontSize = 14.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(${currentLocation.nameBn})",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SakoonTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Text(
                            text = "আর ${activePrayerInfo.remainingFormatted} বাকি • ওয়াক্ত দেখুন",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SakoonTextSecondary,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = SakoonBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Daily Plan Shortcuts Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "দিনের সহজ পরিকল্পনা",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SakoonTextPrimary
                )
            )
            Text(
                text = "চাপ নয়, সহায়তা",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SakoonTextSecondary,
                    fontSize = 12.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2 Digital Shortcut Cards with Modern Gradients
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Health shortcut
            Box(
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(22.dp),
                        ambientColor = Color.Black.copy(alpha = 0.03f),
                        spotColor = SakoonGreen.copy(alpha = 0.08f)
                    )
                    .clip(RoundedCornerShape(22.dp))
                    .background(SakoonGreenBg)
                    .border(
                        width = 1.dp,
                        color = SakoonGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .clickable { viewModel.selectTab(SakoonTab.HEALTH) }
                    .padding(16.dp)
                    .testTag("shortcut_health")
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🥗", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "স্বাস্থ্য ও খাবার",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SakoonGreen,
                            fontSize = 15.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "সহজ খাবার ও পানির যত্ন",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SakoonTextSecondary,
                            fontSize = 11.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "স্বাস্থ্য খুলুন",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SakoonGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.5.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = SakoonGreen,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            // Study/Routine shortcut
            Box(
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(22.dp),
                        ambientColor = Color.Black.copy(alpha = 0.03f),
                        spotColor = SakoonAmber.copy(alpha = 0.08f)
                    )
                    .clip(RoundedCornerShape(22.dp))
                    .background(SakoonAmberBg)
                    .border(
                        width = 1.dp,
                        color = SakoonAmber.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .clickable { viewModel.selectTab(SakoonTab.STUDY) }
                    .padding(16.dp)
                    .testTag("shortcut_study")
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📖", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "পড়াশোনা ও রুটিন",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SakoonAmber,
                            fontSize = 15.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "ছোট সেশন ও টাস্ক",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SakoonTextSecondary,
                            fontSize = 11.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "রুটিন দেখুন",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SakoonAmber,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.5.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = SakoonAmber,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Gentle Affirmation Card (একটু মনে রাখুন)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(SakoonRoseBg)
                .border(
                    width = 1.dp,
                    color = SakoonRose.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(18.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "একটু মনে রাখুন",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SakoonRose,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "🌷", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "কিউট হওয়া মানে শুধু চেহারা নয়",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = SakoonTextPrimary,
                        fontSize = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "আপনার কোমলতা, পরিচ্ছন্নতা, আত্মবিশ্বাস, হাসি, জ্ঞান আর অন্যের প্রতি সদয় আচরণও আপনাকে ভীষণ সুন্দরভাবে প্রকাশ করে।",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SakoonTextSecondary,
                        lineHeight = 19.sp,
                        fontSize = 13.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Digital Quick Mind Gateway
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = Color.Black.copy(alpha = 0.03f),
                    spotColor = SakoonPrimary.copy(alpha = 0.06f)
                )
                .clip(RoundedCornerShape(24.dp))
                .background(SakoonSurface)
                .border(
                    width = 1.dp,
                    color = SakoonOutline.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(18.dp)
        ) {
            Column {
                Text(
                    text = "আজ নিজের মনের খবর কেমন?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SakoonTextPrimary
                    )
                )
                Text(
                    text = "একটি শব্দে নিজের অবস্থা বোঝার চেষ্টা করুন।",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SakoonTextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SakoonPrimaryContainer)
                            .clickable { viewModel.selectTab(SakoonTab.HEART) }
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💜 মনের যত্নে যান",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = SakoonPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SakoonAmberBg)
                            .clickable { viewModel.selectTab(SakoonTab.STUDY) }
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📚 পড়তে বসি",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = SakoonAmber
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Profile / Personal Settings Card (PRD FR-12)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = Color.Black.copy(alpha = 0.03f),
                    spotColor = SakoonPrimary.copy(alpha = 0.06f)
                )
                .clip(RoundedCornerShape(24.dp))
                .background(SakoonSurface)
                .border(
                    width = 1.dp,
                    color = SakoonOutline.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "আপনার ছোট্ট ব্যক্তিগত সেটিংস",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SakoonTextPrimary
                    )
                )
                Text(
                    text = "নাম দুটি শুধু এই ডিভাইসেই সুরক্ষিত থাকবে।",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SakoonTextSecondary,
                        fontSize = 12.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "আপনাকে কী নামে ডাকবে?",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = SakoonTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = editDisplayName,
                    onValueChange = { editDisplayName = it },
                    placeholder = { Text("প্রিয় মানুষ") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SakoonPrimary,
                        unfocusedBorderColor = SakoonOutline,
                        focusedContainerColor = SakoonSurface,
                        unfocusedContainerColor = SakoonSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("display_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "প্রিয় মানুষের নাম",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = SakoonTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = editPartnerName,
                    onValueChange = { editPartnerName = it },
                    placeholder = { Text("Shohan") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SakoonPrimary,
                        unfocusedBorderColor = SakoonOutline,
                        focusedContainerColor = SakoonSurface,
                        unfocusedContainerColor = SakoonSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("partner_name_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.updateProfile(editDisplayName, editPartnerName)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SakoonPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("নাম সংরক্ষণ করুন", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Refined Minimalist Footer
        Text(
            text = "FLUTTERISTA • আপনার গতি, আপনার পছন্দ • কোনো বিচার নয়, শুধু যত্ন 💜",
            style = MaterialTheme.typography.bodySmall.copy(
                color = SakoonTextTertiary,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
