package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AiChatMessage
import com.example.ai.AiChatState
import com.example.ai.MessageSender
import com.example.ui.theme.SakoonBackground
import com.example.ui.theme.SakoonBlue
import com.example.ui.theme.SakoonOutline
import com.example.ui.theme.SakoonPrimary
import com.example.ui.theme.SakoonPrimaryContainer
import com.example.ui.theme.SakoonPrimaryDark
import com.example.ui.theme.SakoonPrimaryVariant
import com.example.ui.theme.SakoonRose
import com.example.ui.theme.SakoonSurface
import com.example.ui.theme.SakoonTextPrimary
import com.example.ui.theme.SakoonTextSecondary
import com.example.ui.theme.SakoonTextTertiary
import com.example.ui.viewmodel.SakoonViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    viewModel: SakoonViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.aiMessages.collectAsState()
    val chatState by viewModel.aiChatState.collectAsState()
    val suggestedPrompts = viewModel.suggestedAiPrompts

    var inputText by remember { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    // Voice recognition launcher for Bengali speech input
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                inputText = if (inputText.isBlank()) spokenText else "$inputText $spokenText"
            }
        }
    }

    // Auto-scroll to the bottom when new message arrives or streaming updates
    LaunchedEffect(messages.size, messages.lastOrNull()?.text?.length) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    BackHandler {
        onBackClick()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SakoonBackground)
            .imePadding()
    ) {
        // --- Top Bar Header (iOS 18 / Android 16 Glassmorphism) ---
        Surface(
            color = SakoonSurface,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("ai_chat_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান",
                            tint = SakoonTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // AI Avatar with animated pulse ring
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(SakoonPrimary, SakoonPrimaryVariant)
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

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "FLUTTERISTA এআই সঙ্গী",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = SakoonTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Green Active Dot
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2E7D32))
                            )
                        }
                        Text(
                            text = "Gemini 3.8 Flash • সার্বক্ষণিক সহায়তা",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = SakoonTextSecondary
                            )
                        )
                    }
                }

                // Action buttons: Clear Chat
                IconButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier.testTag("ai_clear_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "কথোপকথন পরিষ্কার করুন",
                        tint = SakoonTextSecondary
                    )
                }
            }
        }

        // --- Quick Prompts Carousel ---
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(suggestedPrompts) { prompt ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SakoonPrimaryContainer.copy(alpha = 0.7f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SakoonOutline.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .clickable {
                            inputText = prompt
                            viewModel.sendAiMessage(prompt)
                            inputText = ""
                        }
                        .testTag("ai_prompt_chip_${prompt.take(6)}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            tint = SakoonPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SakoonPrimaryDark
                            )
                        )
                    }
                }
            }
        }

        // --- Messages LazyColumn ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatMessageBubble(
                        message = message,
                        onCopyText = { text -> viewModel.copyToClipboard(text) },
                        onRetry = { viewModel.retryLastAiMessage() }
                    )
                }
            }
        }

        // --- Floating Input Dock (Capsule Layout) ---
        Surface(
            color = SakoonSurface,
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Voice Input Button
                    IconButton(
                        onClick = {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                )
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-BD")
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "বাংলায় আপনার কথা বলুন...")
                            }
                            try {
                                speechLauncher.launch(intent)
                            } catch (e: Exception) {
                                // If speech recognizer is not available on this device, show snackbar
                                viewModel.copyToClipboard("ভয়েস ইনপুট সমর্থিত নয়")
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SakoonPrimaryContainer.copy(alpha = 0.5f))
                            .testTag("ai_voice_input_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "ভয়েস ইনপুট",
                            tint = SakoonPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Text Input Field
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                "মনের কথা বা প্রশ্ন লিখুন...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = SakoonTextTertiary,
                                    fontSize = 14.sp
                                )
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SakoonBackground,
                            unfocusedContainerColor = SakoonBackground,
                            focusedBorderColor = SakoonPrimary,
                            unfocusedBorderColor = SakoonOutline
                        ),
                        shape = RoundedCornerShape(22.dp),
                        maxLines = 4,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_message_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Send Button
                    val isSendEnabled = inputText.isNotBlank() && chatState !is AiChatState.Loading
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSendEnabled) {
                                    Brush.linearGradient(listOf(SakoonPrimary, SakoonPrimaryVariant))
                                } else {
                                    Brush.linearGradient(listOf(Color(0xFFD1C4E9), Color(0xFFE1BEE7)))
                                }
                            )
                            .clickable(enabled = isSendEnabled) {
                                val textToSend = inputText
                                inputText = ""
                                viewModel.sendAiMessage(textToSend)
                            }
                            .testTag("ai_send_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (chatState is AiChatState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "পাঠান",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "FLUTTERISTA এআই প্রয়োজনে পরামর্শ দেয়। জটিল বিষয়ে উপযুক্ত বিশেষজ্ঞের মতামত কাম্য।",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = SakoonTextTertiary
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp)
                )
            }
        }
    }

    // Confirmation dialog before clearing chat
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text("কথোপকথন পরিষ্কার করবেন?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("আপনার আগের সমস্ত মেসেজ মুছে নতুন করে আলাপ শুরু হবে।")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAiChat()
                        showClearDialog = false
                    }
                ) {
                    Text("মুছে ফেলুন", color = SakoonRose, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun ChatMessageBubble(
    message: AiChatMessage,
    onCopyText: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.sender == MessageSender.USER
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val timeString = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    // Blinking cursor transition for streaming assistant messages
    val infiniteTransition = rememberInfiniteTransition(label = "cursor_transition")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // Assistant Avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SakoonPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = SakoonPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = if (isUser) 4.dp else 2.dp,
                        shape = if (isUser) {
                            RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
                        } else {
                            RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
                        },
                        ambientColor = Color.Black.copy(alpha = 0.05f),
                        spotColor = SakoonPrimary.copy(alpha = 0.1f)
                    )
                    .clip(
                        if (isUser) {
                            RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
                        } else {
                            RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
                        }
                    )
                    .background(
                        if (isUser) {
                            Brush.linearGradient(
                                colors = listOf(SakoonPrimary, SakoonPrimaryDark)
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(SakoonSurface, Color(0xFFFAFAFE))
                            )
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = if (isUser) Color.Transparent else SakoonOutline.copy(alpha = 0.7f),
                        shape = if (isUser) {
                            RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
                        } else {
                            RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
                        }
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column {
                    if (message.text.isEmpty() && message.isStreaming) {
                        // Typing indicator dots
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "FLUTTERISTA ভাবছে...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = SakoonTextSecondary,
                                    fontSize = 13.sp
                                )
                            )
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = SakoonPrimary,
                                strokeWidth = 1.5.dp
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = message.text,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isUser) Color.White else SakoonTextPrimary,
                                    fontSize = 14.5.sp,
                                    lineHeight = 22.sp
                                ),
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (message.isStreaming) {
                                Text(
                                    text = " ▋",
                                    color = SakoonPrimary.copy(alpha = cursorAlpha),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    if (message.hasError) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFEBEE))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .clickable { onRetry() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "পুনরায় চেষ্টা",
                                tint = SakoonRose,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "আবার চেষ্টা করুন",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SakoonRose,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            // Bottom metadata row (Time + Copy action)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = SakoonTextTertiary
                    )
                )

                if (!isUser && message.text.isNotBlank()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "কপি করুন",
                        tint = SakoonTextTertiary,
                        modifier = Modifier
                            .size(12.dp)
                            .clickable { onCopyText(message.text) }
                    )
                }
            }
        }
    }
}
