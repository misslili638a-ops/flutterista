package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DhikrPhrase
import com.example.data.repository.SakoonRepository
import com.example.ui.theme.SakoonBlue
import com.example.ui.theme.SakoonBlueBg
import com.example.ui.theme.SakoonGreen
import com.example.ui.theme.SakoonGreenBg
import com.example.ui.theme.SakoonOutline
import com.example.ui.theme.SakoonPrimary
import com.example.ui.theme.SakoonPrimaryContainer
import com.example.ui.theme.SakoonPrimaryLight
import com.example.ui.theme.SakoonPrimaryVariant
import com.example.ui.theme.SakoonSurface
import com.example.ui.theme.SakoonTextPrimary
import com.example.ui.theme.SakoonTextSecondary
import com.example.ui.viewmodel.SakoonViewModel

@Composable
fun DailyDhikrCard(
    viewModel: SakoonViewModel,
    modifier: Modifier = Modifier
) {
    val selectedId by viewModel.selectedDhikrId.collectAsState()
    val todayCount by viewModel.dhikrTodayCount.collectAsState()
    val goal by viewModel.dhikrGoal.collectAsState()
    val lifetimeTotal by viewModel.dhikrLifetimeTotal.collectAsState()

    val currentPhrase = SakoonRepository.DEFAULT_DHIKR_PHRASES.find { it.id == selectedId }
        ?: SakoonRepository.DEFAULT_DHIKR_PHRASES.first()

    var showPhraseDialog by remember { mutableStateOf(false) }
    var isTappedState by remember { mutableStateOf(false) }

    val tapScale by animateFloatAsState(
        targetValue = if (isTappedState) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        finishedListener = { isTappedState = false },
        label = "dhikr_tap_scale"
    )

    val progress = if (goal > 0) (todayCount.toFloat() / goal).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "dhikr_progress"
    )

    val isGoalReached = todayCount >= goal && goal > 0

    val primaryAccentColor = if (isGoalReached) SakoonGreen else SakoonPrimary
    val primaryContainerColor = if (isGoalReached) SakoonGreenBg else SakoonPrimaryContainer

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = primaryAccentColor.copy(alpha = 0.12f)
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
            // Card Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "দৈনিক তাসবিহ ও জিকির",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SakoonTextPrimary,
                                fontSize = 18.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "📿", fontSize = 16.sp)
                    }
                    Text(
                        text = "স্মরণ ও প্রশান্তির মাধ্যমে দিনকে আলোকিত রাখুন",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SakoonTextSecondary,
                            fontSize = 11.5.sp
                        )
                    )
                }

                // Change Phrase Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(primaryContainerColor)
                        .clickable { showPhraseDialog = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("dhikr_select_phrase_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FormatListBulleted,
                            contentDescription = null,
                            tint = primaryAccentColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "জিকির পরিবর্তন",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = primaryAccentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Current Phrase Spotlight Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                primaryContainerColor.copy(alpha = 0.7f),
                                primaryContainerColor.copy(alpha = 0.3f)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        primaryAccentColor.copy(alpha = 0.2f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Arabic script with clean font
                    Text(
                        text = currentPhrase.arabic,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SakoonTextPrimary,
                            fontSize = 24.sp,
                            lineHeight = 36.sp,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currentPhrase.transliteration,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = primaryAccentColor,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "“${currentPhrase.meaning}”",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SakoonTextSecondary,
                            fontSize = 12.5.sp,
                            textAlign = TextAlign.Center
                        )
                    )

                    if (currentPhrase.virtue.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.85f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "✨ ${currentPhrase.virtue}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SakoonTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Giant Tap Circular Counter with Animated Progress Ring
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(tapScale)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isTappedState = true
                        viewModel.incrementDhikr()
                    }
                    .testTag("dhikr_tap_increment_button"),
                contentAlignment = Alignment.Center
            ) {
                // Circular Progress Arc
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 10.dp.toPx()
                    // Track circle
                    drawCircle(
                        color = Color(0xFFE2E8F0),
                        style = Stroke(width = strokeWidth)
                    )
                    // Progress arc
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = if (isGoalReached) {
                                listOf(SakoonGreen, Color(0xFF10B981), SakoonGreen)
                            } else {
                                listOf(SakoonPrimary, SakoonPrimaryVariant, SakoonPrimary)
                            }
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Inner Tap Pad
                Box(
                    modifier = Modifier
                        .size(164.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = primaryAccentColor.copy(alpha = 0.15f),
                            spotColor = primaryAccentColor.copy(alpha = 0.25f)
                        )
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    SakoonSurface,
                                    primaryContainerColor.copy(alpha = 0.45f)
                                )
                            )
                        )
                        .border(1.5.dp, primaryAccentColor.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = viewModel.repository.toBanglaNumber(todayCount),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 44.sp,
                                color = primaryAccentColor,
                                letterSpacing = (-0.5).sp
                            )
                        )

                        Text(
                            text = if (isGoalReached) "লক্ষ্য সম্পন্ন! 🌸" else "লক্ষ্য: ${viewModel.repository.toBanglaNumber(goal)} বার",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isGoalReached) SakoonGreen else SakoonTextSecondary,
                                fontSize = 11.5.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = primaryAccentColor.copy(alpha = 0.8f),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "ট্যাপ করুন",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = primaryAccentColor.copy(alpha = 0.8f),
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Goal Progress Bar & Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(9.dp)
                        .clip(RoundedCornerShape(50))
                        .background(primaryContainerColor.copy(alpha = 0.6f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = animatedProgress)
                            .height(9.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = if (isGoalReached) {
                                        listOf(SakoonGreen, Color(0xFF10B981))
                                    } else {
                                        listOf(SakoonPrimary, SakoonPrimaryVariant)
                                    }
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "${viewModel.repository.toBanglaNumber((progress * 100).toInt())}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryAccentColor,
                        fontSize = 12.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Goal Presets (৩৩, ১০০, ৫০০, ১০০০)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(33, 100, 500, 1000).forEach { presetGoal ->
                    val isSelected = goal == presetGoal
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) primaryContainerColor else SakoonSurface)
                            .border(
                                width = 1.2.dp,
                                color = if (isSelected) primaryAccentColor else SakoonOutline,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.setDhikrGoal(presetGoal) }
                            .padding(vertical = 7.dp)
                            .testTag("dhikr_goal_preset_$presetGoal"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${viewModel.repository.toBanglaNumber(presetGoal)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) primaryAccentColor else SakoonTextPrimary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Undo (-1) and Reset Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lifetime total count badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "মোট পাঠ: ",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SakoonTextSecondary,
                            fontSize = 11.5.sp
                        )
                    )
                    Text(
                        text = "${viewModel.repository.toBanglaNumber(lifetimeTotal)} বার",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SakoonTextPrimary,
                            fontSize = 12.sp
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Undo -1 button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SakoonSurface)
                            .border(1.dp, SakoonOutline, RoundedCornerShape(12.dp))
                            .clickable { viewModel.decrementDhikr() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("dhikr_undo_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "১ কমান",
                                tint = SakoonTextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "১ কম",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SakoonTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    // Reset button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SakoonSurface)
                            .border(1.dp, SakoonOutline, RoundedCornerShape(12.dp))
                            .clickable { viewModel.resetDhikr() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("dhikr_reset_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = "রিসেট",
                                tint = SakoonTextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "রিসেট",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SakoonTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet / Dialog to Choose Dhikr Phrase
    if (showPhraseDialog) {
        DhikrPhrasePickerDialog(
            currentPhraseId = selectedId,
            onSelect = { phrase ->
                viewModel.selectDhikrPhrase(phrase.id)
                showPhraseDialog = false
            },
            onDismiss = { showPhraseDialog = false }
        )
    }
}

@Composable
fun DhikrPhrasePickerDialog(
    currentPhraseId: String,
    onSelect: (DhikrPhrase) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = SakoonSurface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "জিকির ও তাসবিহ তালিকা",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SakoonTextPrimary
                            )
                        )
                        Text(
                            text = "প্রতিদিনের আমলের জন্য একটি জিকির বেছে নিন",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SakoonTextSecondary,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "বন্ধ করুন",
                            tint = SakoonTextSecondary
                        )
                    }
                }

                HorizontalDivider(
                    color = SakoonOutline.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                SakoonRepository.DEFAULT_DHIKR_PHRASES.forEach { phrase ->
                    val isSelected = phrase.id == currentPhraseId

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) SakoonPrimaryContainer else SakoonSurface)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) SakoonPrimary else SakoonOutline.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onSelect(phrase) }
                            .padding(14.dp)
                            .testTag("dhikr_phrase_option_${phrase.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = phrase.arabic,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = if (isSelected) SakoonPrimary else SakoonTextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${phrase.transliteration} • ${phrase.meaning}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = SakoonTextSecondary,
                                        fontSize = 12.sp
                                    )
                                )
                                if (phrase.virtue.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "💡 ${phrase.virtue}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SakoonTextSecondary.copy(alpha = 0.8f),
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(SakoonPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
