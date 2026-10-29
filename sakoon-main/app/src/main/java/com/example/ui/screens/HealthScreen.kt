package com.example.ui.screens

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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.SakoonGreen
import com.example.ui.theme.SakoonGreenBg
import com.example.ui.theme.SakoonGreenBorder
import com.example.ui.theme.SakoonGreenLight
import com.example.ui.theme.SakoonOutline
import com.example.ui.theme.SakoonPrimary
import com.example.ui.theme.SakoonPrimaryContainer
import com.example.ui.theme.SakoonPrimaryLight
import com.example.ui.theme.SakoonSurface
import com.example.ui.theme.SakoonTextPrimary
import com.example.ui.theme.SakoonTextSecondary
import com.example.ui.viewmodel.SakoonViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HealthScreen(
    viewModel: SakoonViewModel,
    modifier: Modifier = Modifier
) {
    val waterCount by viewModel.waterCount.collectAsState()
    val movementActivity by viewModel.movementActivity.collectAsState()
    val movementMinutes by viewModel.movementMinutes.collectAsState()
    val movementDone by viewModel.movementDone.collectAsState()

    val activities = listOf(
        "হাঁটা" to "🚶‍♀️",
        "স্ট্রেচিং" to "🧘‍♀️",
        "নাচ" to "💃",
        "সাইক্লিং" to "🚴‍♀️",
        "ঘরের কাজ" to "🧹",
        "বিশ্রাম/রিকভারি" to "🛋️"
    )

    val durations = listOf(5, 10, 15, 30, 45)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 90.dp)
    ) {
        // Section Header
        Column(modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)) {
            Text(
                text = "স্বাস্থ্য ও আরামদায়ক যত্ন",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = SakoonTextPrimary
                )
            )
            Text(
                text = "কোনো কঠোর ডায়েট বা বাধ্যবাধকতা নয়, শরীরের স্বাভাবিক প্রয়োজনকে সম্মান করুন",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SakoonTextSecondary
                )
            )
        }

        // Medical Safety Disclaimer Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(SakoonGreenBg)
                .border(
                    width = 1.dp,
                    color = SakoonGreen.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(SakoonGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "শরীরের নিজস্ব ছন্দ ও সুস্থতা",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SakoonGreen,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "শরীরভেদে ক্ষুধা ও গঠন ভিন্ন হয়। পেটের চর্বি কমানোর আলাদা কোনো জাদুকরী ব্যায়াম নেই। পরিমিত সুষম খাবার ও নিয়মিত স্বস্তিদায়ক চলাচলই দীর্ঘমেয়াদে সুস্থ রাখে।",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SakoonTextSecondary,
                            lineHeight = 18.sp,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Digital Water Tracker Card (FR-04)
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
                            text = "পানি পানের হিসাব",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SakoonTextPrimary
                            )
                        )
                        Text(
                            text = "পিপাসা অনুযায়ী পান করুন, জোর করে অতিরিক্ত নয়",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SakoonTextSecondary,
                                fontSize = 11.5.sp
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
                            text = "দৈনিক হাইড্রেশন",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SakoonPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Digital Glowing Circular Counter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Minus button
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .shadow(
                                elevation = 3.dp,
                                shape = CircleShape,
                                ambientColor = Color.Black.copy(alpha = 0.04f)
                            )
                            .clip(CircleShape)
                            .background(SakoonSurface)
                            .border(1.dp, SakoonOutline, CircleShape)
                            .clickable { viewModel.removeWater() }
                            .testTag("water_minus_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "১ গ্লাস কমান",
                            tint = SakoonTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(26.dp))

                    // Digital water orb
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = CircleShape,
                                ambientColor = SakoonPrimary.copy(alpha = 0.15f),
                                spotColor = SakoonPrimary.copy(alpha = 0.25f)
                            )
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        SakoonPrimaryLight,
                                        SakoonPrimaryContainer
                                    )
                                )
                            )
                            .border(2.dp, SakoonPrimary.copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.LocalDrink,
                                contentDescription = null,
                                tint = SakoonPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                            Text(
                                text = viewModel.repository.toBanglaNumber(waterCount),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 32.sp,
                                    color = SakoonPrimary
                                )
                            )
                            Text(
                                text = "গ্লাস",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SakoonPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(26.dp))

                    // Plus button
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = CircleShape,
                                ambientColor = SakoonPrimary.copy(alpha = 0.25f),
                                spotColor = SakoonPrimary.copy(alpha = 0.4f)
                            )
                            .clip(CircleShape)
                            .background(SakoonPrimary)
                            .clickable { viewModel.addWater() }
                            .testTag("water_plus_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "১ গ্লাস বাড়ান",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(SakoonSurface)
                            .border(1.dp, SakoonOutline, RoundedCornerShape(50))
                            .clickable { viewModel.resetWater() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("water_reset_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = null,
                                tint = SakoonTextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "রিসেট",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SakoonTextSecondary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Digital Movement Tracker Card (FR-05)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = Color.Black.copy(alpha = 0.04f),
                    spotColor = SakoonGreen.copy(alpha = 0.08f)
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
                    text = "আরামদায়ক শরীরচর্চা ও নড়াচড়া",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = SakoonTextPrimary
                    )
                )
                Text(
                    text = "ব্যথা বা ক্লান্তি হলে যেকোনো সময় থামুন। নিজের পছন্দই সেরা।",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SakoonTextSecondary,
                        fontSize = 11.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "পছন্দের নড়াচড়া বেছে নিন:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SakoonTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    activities.forEach { (name, emoji) ->
                        val isSelected = movementActivity == name
                        val chipBg by animateColorAsState(
                            targetValue = if (isSelected) SakoonGreenBg else SakoonSurface,
                            label = "act_bg"
                        )
                        val chipBorder by animateColorAsState(
                            targetValue = if (isSelected) SakoonGreen else SakoonOutline,
                            label = "act_border"
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(chipBg)
                                .border(1.2.dp, chipBorder, RoundedCornerShape(14.dp))
                                .clickable {
                                    viewModel.setMovement(name, movementMinutes, movementDone)
                                }
                                .padding(horizontal = 13.dp, vertical = 8.dp)
                                .testTag("activity_chip_$name")
                        ) {
                            Text(
                                text = "$emoji $name",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) SakoonGreen else SakoonTextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "সময়কাল বেছে নিন:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SakoonTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    durations.forEach { mins ->
                        val isSelected = movementMinutes == mins
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) SakoonPrimaryContainer else SakoonSurface)
                                .border(
                                    width = 1.2.dp,
                                    color = if (isSelected) SakoonPrimary else SakoonOutline,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    viewModel.setMovement(movementActivity, mins, movementDone)
                                }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${viewModel.repository.toBanglaNumber(mins)}মি",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) SakoonPrimary else SakoonTextPrimary,
                                    fontSize = 12.5.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.setMovement(movementActivity, movementMinutes, !movementDone)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (movementDone) SakoonGreen else SakoonPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("movement_done_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (movementDone) "আজকের নড়াচড়া সম্পন্ন! 🌸" else "সম্পন্ন হিসেবে চিহ্নিত করুন",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.resetMovement() },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("movement_reset_button")
                    ) {
                        Text("রিসেট", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Flexible Meal Guidance
        Text(
            text = "সহজ ও পরিচিত খাবারের আইডিয়া",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = SakoonTextPrimary
            )
        )
        Text(
            text = "কোনো খাবার বাদ দিয়ে নিজেকে কষ্ট দেবেন না। ঘরোয়া খাবারে পুষ্টি নিশ্চিত করুন।",
            style = MaterialTheme.typography.bodySmall.copy(
                color = SakoonTextSecondary,
                fontSize = 11.5.sp
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        val mealIdeas = listOf(
            Triple(
                "সকালের নাস্তা",
                "🍳 ভাত বা আটার রুটি, ডিম, ডাল বা সবজি",
                "সকালে পর্যাপ্ত শক্তি দিনভর সতেজ রাখবে। মিষ্টি নাশতার চেয়ে প্রোটিন ও জটিল কার্বস দীর্ঘ সময় তৃপ্ত রাখে।"
            ),
            Triple(
                "দুপুরের পুষ্টিকর খাবার",
                "🐟 পরিমাণমতো ভাত, দেশি মাছ বা মাংস, শাকসবজি ও ডাল",
                "সবজির পরিমাণ ভাতের সমান বা কাছাকাছি রাখার চেষ্টা করুন। শান্ত মনে চিবিয়ে খাওয়ার অভ্যাস গড়ে তুলুন।"
            ),
            Triple(
                "বিকেলের হালকা নাশতা",
                "🍎 মৌসুমি ফল, সেদ্ধ বা ভুনা ছোলা, বাদাম অথবা টক দই",
                "ভাজাপোড়ার পরিবর্তে ঘরে তৈরি হালকা নাশতা শরীরের ক্লান্তি কমায় এবং মিষ্টির অহেতুক লোভ প্রতিরোধ করে।"
            ),
            Triple(
                "রাতের আরামদায়ক খাবার",
                "🥣 হালকা রুটি-সবজি, ডাল বা গরম পুষ্টিকর স্যুপ",
                "ঘুমানোর অন্তত ২ ঘণ্টা আগে খাওয়া ভালো। বেশি ভারী না রেখে সহজে হজম হয় এমন পুষ্টিকর খাবার বেছে নিন।"
            )
        )

        mealIdeas.forEach { (title, subtitle, tip) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(SakoonSurface)
                    .border(1.dp, SakoonOutline.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SakoonGreen,
                            fontSize = 14.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = SakoonTextPrimary,
                            fontSize = 13.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tip,
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
}
