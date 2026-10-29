package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.SakoonRepository
import com.example.ui.components.BottomNavBar
import com.example.ui.components.SettingsDialog
import com.example.ui.components.TopHeader
import com.example.ui.screens.AiChatScreen
import com.example.ui.screens.FaithScreen
import com.example.ui.screens.HealthScreen
import com.example.ui.screens.HeartScreen
import com.example.ui.screens.StudyScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SakoonAmber
import com.example.ui.theme.SakoonAmberBg
import com.example.ui.theme.SakoonBackground
import com.example.ui.theme.SakoonBlue
import com.example.ui.theme.SakoonBlueBg
import com.example.ui.theme.SakoonGreen
import com.example.ui.theme.SakoonGreenBg
import com.example.ui.theme.SakoonOutline
import com.example.ui.theme.SakoonPrimary
import com.example.ui.theme.SakoonPrimaryContainer
import com.example.ui.theme.SakoonPrimaryDark
import com.example.ui.theme.SakoonPrimaryLight
import com.example.ui.theme.SakoonPrimaryVariant
import com.example.ui.theme.SakoonRose
import com.example.ui.theme.SakoonRoseBg
import com.example.ui.theme.SakoonSurface
import com.example.ui.theme.SakoonTextPrimary
import com.example.ui.theme.SakoonTextSecondary
import com.example.ui.viewmodel.SakoonTab
import com.example.ui.viewmodel.SakoonViewModel
import com.example.util.configureAppCheck
import kotlinx.coroutines.flow.collectLatest

/**
 * MainActivity: The main entry point for the 'Sakoon' (FLUTTERISTA) application.
 * Sets up a modern Compose UI with a welcoming dashboard screen and dedicated
 * sections for Health, Islamic Routine, and Study Support.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SakoonApp()
            }
        }
    }
}

/**
 * SakoonApp: Coordinates the top-level navigation, App Check, floating bottom bar,
 * and handles the welcoming dashboard screen as well as specific tabs.
 */
@Composable
fun SakoonApp(
    viewModel: SakoonViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isAiCompanionOpen by viewModel.isAiCompanionOpen.collectAsState()
    var showSettingsDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // If AI companion is open, back gesture closes it and returns to the current tab
    if (isAiCompanionOpen) {
        BackHandler {
            viewModel.closeAiCompanion()
        }
    } else if (currentTab != SakoonTab.TODAY) {
        // Handle system back gesture/button: return to welcoming dashboard if in another tab
        BackHandler {
            viewModel.selectTab(SakoonTab.TODAY)
        }
    }

    // Collect transient UI events
    LaunchedEffect(viewModel) {
        viewModel.uiEvents.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    if (isAiCompanionOpen) {
        AiChatScreen(
            viewModel = viewModel,
            onBackClick = { viewModel.closeAiCompanion() },
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        )
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(SakoonBackground)
                .windowInsetsPadding(WindowInsets.statusBars),
            containerColor = SakoonBackground,
            topBar = {
                TopHeader(
                    dateString = viewModel.todayBengali,
                    onInfoClick = { showSettingsDialog = true },
                    onAiClick = { viewModel.openAiCompanion() }
                )
            },
            bottomBar = {
                BottomNavBar(
                    selectedTab = currentTab,
                    onTabSelected = { tab -> viewModel.selectTab(tab) }
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = SakoonPrimaryDark,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Crossfade(
                    targetState = currentTab,
                    animationSpec = tween(durationMillis = 220),
                    label = "tab_crossfade"
                ) { tab ->
                    when (tab) {
                        SakoonTab.TODAY -> SakoonWelcomingDashboardScreen(viewModel = viewModel)
                        SakoonTab.HEALTH -> HealthScreen(viewModel = viewModel)
                        SakoonTab.FAITH -> FaithScreen(viewModel = viewModel)
                        SakoonTab.STUDY -> StudyScreen(viewModel = viewModel)
                        SakoonTab.HEART -> HeartScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    if (showSettingsDialog) {
        SettingsDialog(
            onDismiss = { showSettingsDialog = false },
            onClearAllData = { viewModel.clearAllData() }
        )
    }
}

/**
 * Welcoming Dashboard Screen for the 'Sakoon' application.
 * Features a personalized welcome banner, AI companion launcher, and explicit
 * placeholder and overview sections for Health, Islamic Routine, and Study Support.
 */
@Composable
fun SakoonWelcomingDashboardScreen(
    viewModel: SakoonViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val completedHabits by viewModel.completedHabits.collectAsState()
    val waterCount by viewModel.waterCount.collectAsState()
    val activePrayerInfo by viewModel.activePrayerInfo.collectAsState()
    val dhikrTodayCount by viewModel.dhikrTodayCount.collectAsState()
    val dhikrGoal by viewModel.dhikrGoal.collectAsState()
    val studyTasks by viewModel.studyTasks.collectAsState()
    val totalHabits = SakoonRepository.DEFAULT_DAILY_HABITS.size
    val completedCount = completedHabits.size
    val progressRatio = if (totalHabits > 0) completedCount.toFloat() / totalHabits else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progressRatio,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "dashboard_progress"
    )

    var editDisplayName by remember(userProfile.displayName) { mutableStateOf(userProfile.displayName) }
    var editPartnerName by remember(userProfile.partnerName) { mutableStateOf(userProfile.partnerName) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 96.dp)
            .testTag("welcoming_dashboard_screen")
    ) {
        // --- 1. Welcoming Hero Banner ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .shadow(
                    elevation = 14.dp,
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
                .testTag("welcoming_banner")
        ) {
            // Subtle glowing backdrop shape
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            )

            Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.14f),
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.BottomEnd)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.22f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Welcome to FLUTTERISTA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    Text(
                        text = "✨ প্রশান্তি ও ভারসাম্য",
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
                    text = "দৈনন্দিন স্বাস্থ্যরীতি, ইসলামিক বিশ্বাস এবং পড়াশোনার সুন্দর ভারসাম্যে নিজেকে প্রতিদিন একটু একটু করে গড়ে তুলুন।",
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
                            text = "স্বাস্থ্য • ইবাদত • পড়াশোনা",
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
                            .background(Color.White.copy(alpha = 0.20f))
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

        // --- 2. AI Companion Quick Access Card ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = SakoonPrimary.copy(alpha = 0.12f),
                    spotColor = SakoonPrimary.copy(alpha = 0.20f)
                )
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(SakoonSurface, Color(0xFFF7F3FF), Color(0xFFFAF5FF))
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.horizontalGradient(
                        listOf(SakoonPrimary.copy(alpha = 0.5f), Color(0xFFCE93D8).copy(alpha = 0.4f))
                    ),
                    shape = RoundedCornerShape(24.dp)
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
                                        fontSize = 16.5.sp
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
                                        text = "Gemini 3.8 Flash",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF2E7D32),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                            Text(
                                text = "স্বাস্থ্য, দু'আ ও পড়াশোনার যেকোনো সহায়তায় কথা বলুন",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SakoonTextSecondary,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SakoonPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "কথা বলুন",
                            tint = SakoonPrimaryDark,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Section Title: Core Pillars of Sakoon
        Text(
            text = "FLUTTERISTA মূল বিভাগসমূহ",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = SakoonTextPrimary,
                fontSize = 18.sp
            )
        )
        Text(
            text = "স্বাস্থ্য, ইসলামিক রুটিন এবং পড়াশোনা সহায়তার জন্য নির্ধারিত ড্যাশবোর্ড",
            style = MaterialTheme.typography.bodySmall.copy(
                color = SakoonTextSecondary,
                fontSize = 12.sp
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- 3. Health Section Placeholder / Preview Card ---
        HealthSectionCard(
            waterCount = waterCount,
            onIncrementWater = { viewModel.addWater() },
            onOpenHealth = { viewModel.selectTab(SakoonTab.HEALTH) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // --- 4. Islamic Routine Section Placeholder / Preview Card ---
        IslamicRoutineSectionCard(
            nextPrayerName = activePrayerInfo.nextPrayerName,
            remainingTime = activePrayerInfo.remainingFormatted,
            dhikrTodayCount = dhikrTodayCount,
            dhikrGoal = dhikrGoal,
            onIncrementDhikr = { viewModel.incrementDhikr() },
            onOpenFaith = { viewModel.selectTab(SakoonTab.FAITH) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // --- 5. Study Support Section Placeholder / Preview Card ---
        StudySupportSectionCard(
            taskCount = studyTasks.size,
            onOpenStudy = { viewModel.selectTab(SakoonTab.STUDY) }
        )

        Spacer(modifier = Modifier.height(22.dp))

        // --- 6. Daily Habits Checklist Section ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = Color.Black.copy(alpha = 0.03f),
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
                .testTag("habits_checklist_section")
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
                                fontSize = 17.sp
                            )
                        )
                        Text(
                            text = "প্রতিদিনের ছোট অভ্যাস আপনাকে প্রশান্তি দেয়",
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

                // Progress Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(SakoonPrimaryContainer.copy(alpha = 0.6f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = animatedProgress.coerceIn(0f, 1f))
                                .height(8.dp)
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

                Spacer(modifier = Modifier.height(14.dp))

                SakoonRepository.DEFAULT_DAILY_HABITS.forEachIndexed { index, habit ->
                    val isChecked = completedHabits.contains(habit.id)
                    val itemBg by animateColorAsState(
                        targetValue = if (isChecked) SakoonPrimaryLight else Color.Transparent,
                        label = "habit_bg_$index"
                    )
                    val checkScale by animateFloatAsState(
                        targetValue = if (isChecked) 1.05f else 1.0f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "habit_scale_$index"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(itemBg)
                            .clickable { viewModel.toggleHabit(habit.id) }
                            .padding(vertical = 10.dp, horizontal = 8.dp)
                            .testTag("habit_item_${habit.id}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
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
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = habit.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isChecked) SakoonPrimary else SakoonTextPrimary
                                )
                            )
                            Text(
                                text = habit.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SakoonTextSecondary,
                                    fontSize = 11.5.sp
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

        Spacer(modifier = Modifier.height(18.dp))

        // --- 7. Mind & Emotional Wellbeing Shortcut ---
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
                .clickable { viewModel.selectTab(SakoonTab.HEART) }
                .padding(18.dp)
                .testTag("heart_shortcut_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SakoonRose),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "আজ নিজের মনের যত্ন নিয়েছেন?",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SakoonTextPrimary,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = "শ্বাসপ্রশ্বাসের ব্যায়াম, মুড ট্র্যাকার ও কৃতজ্ঞতা ডায়েরি",
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
                    tint = SakoonRose,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --- 8. Profile Personal Settings Card ---
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
                .testTag("dashboard_settings_card")
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
                    text = "নাম দুটি শুধু এই ডিভাইসেই সুরক্ষিতভাবে থাকবে।",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SakoonTextSecondary,
                        fontSize = 12.sp
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "আপনাকে কী নামে সম্বোধন করবে?",
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
                        .testTag("input_display_name")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "প্রিয় মানুষের নাম (ঐচ্ছিক)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = SakoonTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = editPartnerName,
                    onValueChange = { editPartnerName = it },
                    placeholder = { Text("Shohan / শোহান") },
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
                        .testTag("input_partner_name")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { viewModel.updateProfile(editDisplayName, editPartnerName) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SakoonPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("save_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "নাম সেভ করুন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                }
            }
        }
    }
}

/**
 * Health Section Card: Placeholder & overview for Health & Physical Wellness.
 */
@Composable
fun HealthSectionCard(
    waterCount: Int,
    onIncrementWater: () -> Unit,
    onOpenHealth: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("health_section_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SakoonGreenBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(SakoonGreen.copy(alpha = 0.35f), SakoonGreen.copy(alpha = 0.15f))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SakoonGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalDrink,
                            contentDescription = "Health",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "স্বাস্থ্য ও সুস্থতা (Health)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SakoonGreen,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "পানি পান, পুষ্টিকর খাবার ও স্বস্তিময় ঘুম",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SakoonTextSecondary,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "পানির যত্ন: $waterCount/৮ গ্লাস",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SakoonGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "শরীরের চাহিদাকে অবহেলা করবেন না। প্রতিদিন পর্যাপ্ত পানি পান এবং পরিমিত ঘুম সুস্থতার প্রথম ধাপ।",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SakoonTextPrimary.copy(alpha = 0.85f),
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onIncrementWater,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("health_increment_water_btn")
                ) {
                    Text(
                        text = "+ ১ গ্লাস পানি",
                        color = SakoonGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    )
                }

                Button(
                    onClick = onOpenHealth,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SakoonGreen),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("open_health_button")
                ) {
                    Text(
                        text = "স্বাস্থ্য বিভাগ খুলুন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    )
                }
            }
        }
    }
}

/**
 * Islamic Routine Section Card: Placeholder & overview for Islamic Routine & Faith.
 */
@Composable
fun IslamicRoutineSectionCard(
    nextPrayerName: String,
    remainingTime: String,
    dhikrTodayCount: Int,
    dhikrGoal: Int,
    onIncrementDhikr: () -> Unit,
    onOpenFaith: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("islamic_routine_section_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SakoonBlueBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(SakoonBlue.copy(alpha = 0.35f), SakoonBlue.copy(alpha = 0.15f))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SakoonBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mosque,
                            contentDescription = "Islamic Routine",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ইসলামিক রুটিন (Islamic Routine)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SakoonBlue,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "নামাজের সময়সূচি, ডিজিটাল তাসবীহ ও দু'আ",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SakoonTextSecondary,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "পরবর্তী: $nextPrayerName",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SakoonBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ওয়াক্ত বাকি: $remainingTime • যিকির: $dhikrTodayCount/$dhikrGoal",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SakoonTextPrimary.copy(alpha = 0.85f),
                        fontSize = 12.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onIncrementDhikr,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("islamic_increment_dhikr_btn")
                ) {
                    Text(
                        text = "+ ১টি যিকির",
                        color = SakoonBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    )
                }

                Button(
                    onClick = onOpenFaith,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SakoonBlue),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("open_islamic_routine_button")
                ) {
                    Text(
                        text = "ইবাদত রুটিন খুলুন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    )
                }
            }
        }
    }
}

/**
 * Study Support Section Card: Placeholder & overview for Study Support, Pomodoro, and Focus.
 */
@Composable
fun StudySupportSectionCard(
    taskCount: Int,
    onOpenStudy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("study_support_section_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SakoonAmberBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(SakoonAmber.copy(alpha = 0.35f), SakoonAmber.copy(alpha = 0.15f))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SakoonAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Study Support",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "পড়াশোনা ও সহায়তা (Study Support)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SakoonAmber,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "পোমোডোরো টাইমার, লক্ষ্য ও পারিপার্শ্বিক শব্দ",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SakoonTextSecondary,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "২৫ মি. ফোকাস",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SakoonAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "বড় কাজকে ছোট ছোট ২৫ মিনিটের পর্বে ভাগ করে পড়ুন। $taskCount টি পড়ার টাস্ক এবং শান্ত পরিবেশের সহায়তা প্রস্তুত।",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SakoonTextPrimary.copy(alpha = 0.85f),
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onOpenStudy,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SakoonAmber),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("open_study_support_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "পড়াশোনা ও ফোকাস শুরু করুন",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp
                )
            }
        }
    }
}
