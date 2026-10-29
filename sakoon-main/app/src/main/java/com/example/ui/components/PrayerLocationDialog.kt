package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.prayer.CityLocation
import com.example.prayer.LocationManagerHelper
import com.example.ui.theme.SakoonBlue
import com.example.ui.theme.SakoonBlueBg
import com.example.ui.theme.SakoonOutline
import com.example.ui.theme.SakoonSurface
import com.example.ui.theme.SakoonTextPrimary
import com.example.ui.theme.SakoonTextSecondary

@Composable
fun PrayerLocationDialog(
    currentLocation: CityLocation,
    onSelectCity: (CityLocation) -> Unit,
    onRequestGps: () -> Unit,
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
                            text = "নামাজের অবস্থান নির্বাচন",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SakoonTextPrimary
                            )
                        )
                        Text(
                            text = "সঠিক ওয়াক্তের জন্য এলাকা বা শহর বেছে নিন",
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

                // GPS Auto Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SakoonBlueBg)
                        .border(1.dp, SakoonBlue.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .clickable {
                            onRequestGps()
                            onDismiss()
                        }
                        .padding(14.dp)
                        .testTag("gps_detect_button"),
                    contentAlignment = Alignment.CenterStart
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
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "জিপিএস দিয়ে স্বয়ংক্রিয় সনাক্ত করুন",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SakoonBlue
                                )
                            )
                            Text(
                                text = "ডিভাইসের বর্তমান অক্ষাংশ ও দ্রাঘিমাংশ ব্যবহার করবে",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SakoonTextSecondary,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "বাংলাদেশের বিভাগ ও প্রধান শহরসমূহ:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SakoonTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                LocationManagerHelper.DEFAULT_CITIES.forEach { city ->
                    val isSelected = city.nameBn == currentLocation.nameBn

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) SakoonBlueBg else Color.Transparent)
                            .clickable {
                                onSelectCity(city)
                                onDismiss()
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .testTag("city_select_${city.nameEn.lowercase()}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (isSelected) SakoonBlue else SakoonTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = city.nameBn,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) SakoonBlue else SakoonTextPrimary
                                    )
                                )
                                Text(
                                    text = city.nameEn,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SakoonTextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "নির্বাচিত",
                                tint = SakoonBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
