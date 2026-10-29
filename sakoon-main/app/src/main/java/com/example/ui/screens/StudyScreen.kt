package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SakoonAmber
import com.example.ui.theme.SakoonAmberBg
import com.example.ui.theme.SakoonAmberBorder
import com.example.ui.theme.SakoonOutline
import com.example.ui.theme.SakoonPrimary
import com.example.ui.theme.SakoonPrimaryContainer
import com.example.ui.theme.SakoonSurface
import com.example.ui.theme.SakoonTextPrimary
import com.example.ui.theme.SakoonTextSecondary
import com.example.ui.viewmodel.SakoonViewModel
import com.example.ui.viewmodel.TimerStatus

@Composable
fun StudyScreen(
    viewModel: SakoonViewModel,
    modifier: Modifier = Modifier
) {
    val studyTasks by viewModel.studyTasks.collectAsState()
    val timerMinutes by viewModel.timerMinutes.collectAsState()
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()
    val timerStatus by viewModel.timerStatus.collectAsState()

    var newTaskText by remember { mutableStateOf("") }

    val presets = listOf(5, 15, 25, 45)

    val totalSeconds = timerMinutes * 60
    val progress = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds else 0f

    val minutesPart = remainingSeconds / 60
    val secondsPart = remainingSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutesPart, secondsPart)
        .map { viewModel.repository.toBanglaDigit(it) }
        .joinToString("")

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
                text = "পড়াশোনা ও ফোকাস সহায়িকা",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = SakoonTextPrimary
                )
            )
            Text(
                text = "বড় মানসিক চাপ নয়, শান্ত মনে ছোট ছোট সেশনে পড়াশোনা",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SakoonTextSecondary
                )
            )
        }

        // Digital Glowing Focus Timer Card (FR-08)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color.Black.copy(alpha = 0.04f),
                    spotColor = SakoonAmber.copy(alpha = 0.12f)
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
                            text = "ডিজিটাল ফোকাস টাইমার",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SakoonTextPrimary
                            )
                        )
                        Text(
                            text = "একটি বিষয় বেছে নিয়ে একমনে পড়ুন",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SakoonTextSecondary,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(SakoonAmberBg)
                            .border(1.dp, SakoonAmberBorder, RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = when (timerStatus) {
                                TimerStatus.RUNNING -> "চলছে..."
                                TimerStatus.PAUSED -> "স্থগিত"
                                TimerStatus.COMPLETED -> "সম্পন্ন! 🎉"
                                TimerStatus.IDLE -> "প্রস্তুত"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SakoonAmber,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Presets Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { mins ->
                        val isSelected = timerMinutes == mins
                        val bg by animateColorAsState(
                            targetValue = if (isSelected) SakoonAmberBg else SakoonSurface,
                            label = "preset_bg"
                        )
                        val border by animateColorAsState(
                            targetValue = if (isSelected) SakoonAmber else SakoonOutline,
                            label = "preset_border"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bg)
                                .border(1.2.dp, border, RoundedCornerShape(12.dp))
                                .clickable { viewModel.setTimerPreset(mins) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${viewModel.repository.toBanglaNumber(mins)} মিনিট",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isSelected) SakoonAmber else SakoonTextPrimary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Modern Circular Stopwatch Ring
                Box(
                    modifier = Modifier
                        .size(190.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 9.dp.toPx()
                        // Track
                        drawCircle(
                            color = Color(0xFFFEF3C7),
                            style = Stroke(width = strokeWidth)
                        )
                        // Progress Arc
                        drawArc(
                            color = Color(0xFFD97706),
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 38.sp,
                                letterSpacing = (-0.5).sp,
                                color = SakoonTextPrimary
                            )
                        )
                        Text(
                            text = "অবশিষ্ট সময়",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SakoonTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Timer Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (timerStatus == TimerStatus.RUNNING) {
                        Button(
                            onClick = { viewModel.pauseTimer() },
                            colors = ButtonDefaults.buttonColors(containerColor = SakoonAmber),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("timer_pause_button")
                        ) {
                            Icon(Icons.Default.Pause, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("বিরতি দিন", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { viewModel.startTimer() },
                            colors = ButtonDefaults.buttonColors(containerColor = SakoonAmber),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("timer_start_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (timerStatus == TimerStatus.PAUSED) "চালিয়ে যান" else "শুরু করুন",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.resetTimer() },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("timer_reset_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("রিসেট", fontWeight = FontWeight.SemiBold)
                    }
                }

                if (timerStatus == TimerStatus.COMPLETED) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SakoonPrimaryContainer)
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "চমৎকার কাজ! এবার ৫ মিনিট বিরতি নিয়ে একটু হেঁটে আসুন 🌸",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SakoonPrimary,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tasks Card (FR-07)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = Color.Black.copy(alpha = 0.04f),
                    spotColor = SakoonAmber.copy(alpha = 0.08f)
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
                    Text(
                        text = "পড়ার টাস্ক তালিকা",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SakoonTextPrimary
                        )
                    )

                    if (studyTasks.any { it.isCompleted }) {
                        Text(
                            text = "সম্পন্নগুলো মুছুন",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SakoonAmber,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { viewModel.clearCompletedTasks() }
                                .padding(4.dp)
                                .testTag("clear_completed_tasks_button")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Add Task Field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newTaskText,
                        onValueChange = { if (it.length <= 100) newTaskText = it },
                        placeholder = { Text("নতুন টাস্ক (যেমন: গণিত ৫টি সমাধান)") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SakoonAmber,
                            unfocusedBorderColor = SakoonOutline
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("new_task_input")
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(14.dp),
                                ambientColor = SakoonAmber.copy(alpha = 0.25f)
                            )
                            .clip(RoundedCornerShape(14.dp))
                            .background(SakoonAmber)
                            .clickable {
                                if (newTaskText.isNotBlank()) {
                                    viewModel.addTask(newTaskText)
                                    newTaskText = ""
                                }
                            }
                            .testTag("add_task_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "যোগ করুন",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (studyTasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "এখনও কোনো টাস্ক যোগ করা হয়নি। ছোট কোনো পড়া দিয়ে শুরু করুন!",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SakoonTextSecondary
                            )
                        )
                    }
                } else {
                    studyTasks.forEachIndexed { index, task ->
                        val scale by animateFloatAsState(
                            targetValue = if (task.isCompleted) 1.05f else 1.0f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "task_scale"
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.toggleTask(task.id) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Digital Circle Checkbox
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .scale(scale)
                                    .clip(CircleShape)
                                    .background(if (task.isCompleted) SakoonAmber else SakoonSurface)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (task.isCompleted) SakoonAmber else SakoonOutline,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (task.isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                                    color = if (task.isCompleted) SakoonTextSecondary else SakoonTextPrimary,
                                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = { viewModel.deleteTask(task.id) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("delete_task_${task.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "মুছুন",
                                    tint = SakoonTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        if (index < studyTasks.size - 1) {
                            HorizontalDivider(
                                color = SakoonOutline.copy(alpha = 0.5f),
                                thickness = 0.6.dp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Strategy Tip Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SakoonAmberBg)
                .border(1.dp, SakoonAmber.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "💡 সহজ পড়ার কৌশল",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SakoonAmber
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• কঠিন বিষয়ে ১টি প্যারাগ্রাফ বা ৫ মিনিট সময় দিয়ে শুরু করুন।\n• বড় অধ্যায়কে ৩টি ছোট প্রশ্নে ভাগ করে নিন।\n• ক্লান্ত লাগলে টাইমার বন্ধ রেখে একটু বিশ্রাম নেওয়াও কোনো অপরাধ নয়।",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SakoonTextSecondary,
                        lineHeight = 18.sp,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}
