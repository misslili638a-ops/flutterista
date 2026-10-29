package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.theme.SakoonOutline
import com.example.ui.theme.SakoonPrimary
import com.example.ui.theme.SakoonPrimaryContainer
import com.example.ui.theme.SakoonPrimaryDark
import com.example.ui.theme.SakoonPrimaryGlow
import com.example.ui.theme.SakoonPrimaryLight
import com.example.ui.theme.SakoonPrimaryVariant
import com.example.ui.theme.SakoonRose
import com.example.ui.theme.SakoonRoseBg
import com.example.ui.theme.SakoonRoseBorder
import com.example.ui.theme.SakoonSurface
import com.example.ui.theme.SakoonTextPrimary
import com.example.ui.theme.SakoonTextSecondary
import com.example.ui.viewmodel.BreathingPhase
import com.example.ui.viewmodel.SakoonViewModel

@Composable
fun HeartScreen(
    viewModel: SakoonViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val mood by viewModel.mood.collectAsState()
    val journalText by viewModel.journalText.collectAsState()
    val missingDraft by viewModel.missingDraft.collectAsState()

    val breathingActive by viewModel.breathingActive.collectAsState()
    val breathingPhase by viewModel.breathingPhase.collectAsState()
    val breathingRemaining by viewModel.breathingRemaining.collectAsState()

    var editableDraft by remember(missingDraft) { mutableStateOf(missingDraft) }

    val moods = listOf(
        "ভালো" to ("🌸" to "আজকের ভালো অনুভূতিকে উপভোগ করুন এবং নিজের চেষ্টাগুলোর জন্য কৃতজ্ঞ হোন।"),
        "স্বাভাবিক" to ("🍃" to "একটি সাধারণ শান্ত দিনও ভীষণ সুন্দর ও মূল্যবান। নিজের স্বাভাবিক গতিতে চলুন।"),
        "মন খারাপ" to ("🌧️" to "মন খারাপ হওয়া সম্পূর্ণ স্বাভাবিক। নিজের প্রতি কোনো অবিচার না করে বিশ্রাম নিন।"),
        "অস্থির" to ("⚡" to "গভীর শ্বাস নিন। মনে রাখুন সব অনুভূতি সাময়িক, আপনি একদম একা নন।"),
        "ক্লান্ত" to ("🌙" to "আপনার শরীর কিছুটা বিরতি চাইছে। নিজেকে অযথা চাপ দেবেন না।")
    )

    // Breathing circle multi-layered animation scale
    val targetScale = when {
        !breathingActive -> 1.0f
        breathingPhase == BreathingPhase.INHALE -> 1.35f
        breathingPhase == BreathingPhase.HOLD -> 1.35f
        else -> 0.92f
    }
    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 3800, easing = FastOutSlowInEasing),
        label = "breath_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 90.dp)
    ) {
        // Header
        Column(modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)) {
            Text(
                text = "মন ও আবেগের যত্ন",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = SakoonTextPrimary
                )
            )
            Text(
                text = "নিজের অনুভূতির প্রতি পূর্ণ সম্মান, কোনো বিচার বা তুলনা নয়",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SakoonTextSecondary
                )
            )
        }

        // Mood Check-in Card (FR-09)
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
                Text(
                    text = "আজকের মনের খবর কেমন?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SakoonTextPrimary
                    )
                )
                Text(
                    text = "কোনো কারণ বিশ্লেষণ না করেও অনুভূতি প্রকাশ করতে পারেন",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SakoonTextSecondary,
                        fontSize = 12.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    moods.forEach { (label, data) ->
                        val (emoji, _) = data
                        val isSelected = mood == label

                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.08f else 1.0f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "mood_scale"
                        )

                        val bg by animateColorAsState(
                            targetValue = if (isSelected) SakoonPrimaryContainer else Color.Transparent,
                            label = "mood_bg"
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .scale(scale)
                                .clip(RoundedCornerShape(16.dp))
                                .background(bg)
                                .clickable { viewModel.selectMood(label) }
                                .padding(horizontal = 8.dp, vertical = 10.dp)
                                .testTag("mood_option_$label")
                        ) {
                            Text(text = emoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) SakoonPrimary else SakoonTextPrimary,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }
                }

                if (mood != null) {
                    val message = moods.find { it.first == mood }?.second?.second ?: ""
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SakoonRoseBg)
                            .border(1.dp, SakoonRose.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "💜 $message",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SakoonRose,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 19.sp,
                                fontSize = 13.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SakoonPrimaryContainer.copy(alpha = 0.5f))
                            .border(1.dp, SakoonOutline.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .clickable {
                                viewModel.openAiCompanion("আজ আমার মনটা '$mood' লাগছে। আমাকে একটু মানসিক স্বস্তি, ভালোবাসা ও ইতিবাচক পরামর্শ দিন 🌸")
                            }
                            .padding(horizontal = 12.dp, vertical = 9.dp)
                            .testTag("ai_mood_talk_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = SakoonPrimaryDark,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "এআই সঙ্গীর সাথে অনুভূতি শেয়ার করুন",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SakoonPrimaryDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 60-Second Guided Breathing (FR-11 - Apple Mindfulness / Calm style)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color.Black.copy(alpha = 0.04f),
                    spotColor = SakoonPrimary.copy(alpha = 0.14f)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(SakoonSurface)
                .border(
                    width = 1.dp,
                    color = SakoonOutline.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "৬০ সেকেন্ড শান্ত শ্বাস-প্রশ্বাস",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SakoonTextPrimary
                            )
                        )
                        Text(
                            text = "অস্থিরতা কমাতে ৪ সেকেন্ড শ্বাস নেওয়া ও ছাড়ার চর্চা",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SakoonTextSecondary,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SakoonPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Air,
                            contentDescription = null,
                            tint = SakoonPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Multi-layered Pulsing Breathing Orb
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(SakoonPrimaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer aura
                    Box(
                        modifier = Modifier
                            .size((130 * animatedScale).dp)
                            .clip(CircleShape)
                            .background(SakoonPrimaryContainer.copy(alpha = 0.65f))
                    )

                    // Inner core
                    Box(
                        modifier = Modifier
                            .size((90 * animatedScale).dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        SakoonPrimaryVariant,
                                        SakoonPrimary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (breathingActive) breathingPhase.label else "শান্ত হন",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 12.5.sp
                                )
                            )
                            if (breathingActive) {
                                Text(
                                    text = "${viewModel.repository.toBanglaNumber(breathingRemaining)} সে.",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                if (breathingActive) {
                    Button(
                        onClick = { viewModel.stopBreathing() },
                        colors = ButtonDefaults.buttonColors(containerColor = SakoonRose),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("breathing_stop_button")
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("থামুন", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { viewModel.startBreathing() },
                        colors = ButtonDefaults.buttonColors(containerColor = SakoonPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("breathing_start_button")
                    ) {
                        Icon(Icons.Default.Air, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("অনুশীলন শুরু করুন", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "কোনো অস্বস্তি বোধ হলে তাৎক্ষণিক থামুন।",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SakoonTextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Private Journal Card (FR-09)
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SakoonPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = SakoonPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "ব্যক্তিগত ডায়রি (অটো-সেভ)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SakoonTextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "🔒 এই লেখা সম্পূর্ণ গোপনীয়। কোনো সার্ভার বা ${userProfile.partnerName}-এর ফোনে যায় না। শুধুমাত্র আপনার ফোনেই সংরক্ষিত থাকে।",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SakoonTextSecondary,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = journalText,
                    onValueChange = { viewModel.updateJournal(it) },
                    placeholder = { Text("আজকের ভাবনা, অনুভূতি বা মনের গোপন কথা লিখে রাখুন...") },
                    minLines = 4,
                    maxLines = 8,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SakoonPrimary,
                        unfocusedBorderColor = SakoonOutline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("journal_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (journalText.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SakoonPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ডিভাইসে সংরক্ষিত",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SakoonPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.updateJournal("") },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("লেখা মুছুন", fontSize = 11.5.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Missing Shohan Section (FR-10, PRD Section 07)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = Color.Black.copy(alpha = 0.04f),
                    spotColor = SakoonRose.copy(alpha = 0.1f)
                )
                .clip(RoundedCornerShape(26.dp))
                .background(SakoonRoseBg)
                .border(
                    width = 1.dp,
                    color = SakoonRose.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SakoonRose),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "${userProfile.partnerName}-কে মনে পড়লে",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SakoonRose
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "প্রিয়জনকে মনে পড়া একটি সুন্দর অনুভূতি। তবে আপনার নিজের সময়, লক্ষ্য ও ব্যক্তিগত যত্নও আপনার জন্য সমান গুরুত্বপূর্ণ।",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SakoonTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "সম্পাদনাযোগ্য ভালোবাসার বার্তা ড্রাফট:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SakoonTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = editableDraft,
                    onValueChange = {
                        editableDraft = it
                        viewModel.saveDraft(it)
                    },
                    minLines = 3,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SakoonRose,
                        unfocusedBorderColor = SakoonRoseBorder,
                        focusedContainerColor = SakoonSurface,
                        unfocusedContainerColor = SakoonSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("draft_message_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.copyToClipboard(editableDraft) },
                        colors = ButtonDefaults.buttonColors(containerColor = SakoonRose),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("copy_draft_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("মেসেজ কপি করুন", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.resetDraft()
                            editableDraft = viewModel.missingDraft.value
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("reset_draft_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("রিসেট", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "⚠️ এই মেসেজটি অ্যাপ নিজে থেকে পাঠাবে না। প্রয়োজন হলে আপনি কপি করে মেসেঞ্জারে পাঠাতে পারেন।",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SakoonTextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
