package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.SakoonOutline
import com.example.ui.theme.SakoonPrimary
import com.example.ui.theme.SakoonPrimaryContainer
import com.example.ui.theme.SakoonPrimaryDark
import com.example.ui.theme.SakoonPrimaryVariant
import com.example.ui.theme.SakoonSurface
import com.example.ui.theme.SakoonTextPrimary
import com.example.ui.theme.SakoonTextSecondary
import com.example.ui.theme.SakoonTextTertiary

@Composable
fun TopHeader(
    dateString: String,
    onInfoClick: () -> Unit,
    onAiClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Branding
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = SakoonPrimary.copy(alpha = 0.25f),
                        spotColor = SakoonPrimary.copy(alpha = 0.35f)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                SakoonPrimary,
                                SakoonPrimaryVariant
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.flutterista_logo),
                    contentDescription = "FLUTTERISTA Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "FLUTTERISTA",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 21.sp,
                            letterSpacing = (-0.3).sp,
                            color = SakoonTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(SakoonPrimary)
                    )
                }
                Text(
                    text = "নিজের যত্ন • বিশ্বাস • দৈনিক অগ্রগতি",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SakoonTextSecondary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        // Right Digital Pills (AI Companion, Date Capsule & Settings)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Sparkling AI Companion Pill
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 3.dp,
                        shape = RoundedCornerShape(50),
                        ambientColor = SakoonPrimary.copy(alpha = 0.15f),
                        spotColor = SakoonPrimary.copy(alpha = 0.25f)
                    )
                    .clip(RoundedCornerShape(50))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(SakoonPrimaryContainer, Color(0xFFF3E8FF))
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = SakoonPrimary.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(50)
                    )
                    .clickable { onAiClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("top_header_ai_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "FLUTTERISTA এআই সঙ্গী",
                        tint = SakoonPrimaryDark,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "এআই সঙ্গী",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SakoonPrimaryDark,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }

            // iOS 18 style dynamic island pill
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 3.dp,
                        shape = RoundedCornerShape(50),
                        ambientColor = Color.Black.copy(alpha = 0.04f),
                        spotColor = Color.Black.copy(alpha = 0.06f)
                    )
                    .clip(RoundedCornerShape(50))
                    .background(SakoonSurface)
                    .border(
                        width = 1.dp,
                        color = SakoonOutline.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = SakoonPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SakoonPrimary,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }

            // Glass action button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .shadow(
                        elevation = 3.dp,
                        shape = CircleShape,
                        ambientColor = Color.Black.copy(alpha = 0.04f),
                        spotColor = Color.Black.copy(alpha = 0.06f)
                    )
                    .clip(CircleShape)
                    .background(SakoonSurface)
                    .border(
                        width = 1.dp,
                        color = SakoonOutline.copy(alpha = 0.8f),
                        shape = CircleShape
                    )
                    .clickable { onInfoClick() }
                    .testTag("settings_info_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "গোপনীয়তা ও তথ্য",
                    tint = SakoonTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
