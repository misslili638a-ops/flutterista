package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.SakoonBlue
import com.example.ui.theme.SakoonBlueBg
import com.example.ui.theme.SakoonGreen
import com.example.ui.theme.SakoonGreenBg
import com.example.ui.theme.SakoonOutline
import com.example.ui.theme.SakoonPrimary
import com.example.ui.theme.SakoonPrimaryContainer
import com.example.ui.theme.SakoonRose
import com.example.ui.theme.SakoonRoseBg
import com.example.ui.theme.SakoonSurface
import com.example.ui.theme.SakoonTextPrimary
import com.example.ui.theme.SakoonTextSecondary

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    onClearAllData: () -> Unit
) {
    var showConfirmDelete by remember { mutableStateOf(false) }

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
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "গোপনীয়তা ও অ্যাপ পরিচিতি",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SakoonTextPrimary
                        )
                    )
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

                // Privacy Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SakoonPrimaryContainer.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(SakoonPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "সম্পূর্ণ অফলাইন ও গোপনীয়",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SakoonTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "আপনার নাম, ডায়রির লেখা, পানির হিসাব ও মনস্তাত্ত্বিক তথ্য শুধুমাত্র এই ফোনেই সংরক্ষিত থাকে। কোনো সার্ভারে পাঠানো হয় না এবং কোনো ট্র্যাকার বা বিজ্ঞাপন নেই।",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SakoonTextSecondary,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Medical Disclaimer
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SakoonGreenBg)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
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
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "স্বাস্থ্য ও পুষ্টি বিষয়ক সতর্কতা",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SakoonTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "FLUTTERISTA কোনো প্রেসক্রিপশন বা ওজন কমানোর কঠোর বাধ্যবাধকতা দেয় না। পেটের চর্বি শুধু ব্যায়াম দিয়ে একা কমানোর কোনো অবাস্তব দাবি আমরা করি না। অসুস্থতা বা বিশেষ শারীরিক অবস্থায় চিকিৎসকের পরামর্শ নিন।",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SakoonTextSecondary,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Faith Disclaimer
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SakoonBlueBg)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
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
                            Text(
                                text = "ইবাদত সহায়িকা বার্তা",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SakoonTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "নামাজের তালিকাটি আপনার ব্যক্তিগত নিয়মিত চর্চার সহায়িকা। নামাজের নির্দিষ্ট ওয়াক্তের জন্য আপনার এলাকার স্থানীয় মসজিদ বা ক্যালেন্ডার অনুসরণ করুন।",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SakoonTextSecondary,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Clear All Data Action (PRD FR-15)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SakoonRoseBg)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = null,
                                tint = SakoonRose,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "সব ডেটা মুছে ফেলুন",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SakoonRose
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "আপনার ডায়রির লেখা, অভ্যাসের তালিকা ও সেটিংস স্থায়ীভাবে মুছে যাবে।",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SakoonTextSecondary,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { showConfirmDelete = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SakoonRose),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("clear_all_data_button")
                        ) {
                            Text("ডেটা মুছে ফেলুন", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Footer version
                Text(
                    text = "FLUTTERISTA • সংস্করণ ১.০ • Bangla Daily Companion",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SakoonTextSecondary.copy(alpha = 0.7f),
                        fontSize = 11.5.sp
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }

    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = {
                Text(
                    text = "সব তথ্য নিশ্চিতভাবে মুছবেন?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "এই ফোনের সংরক্ষিত সকল ডায়রি, পানির হিসাব, টাস্ক ও সেটিংস মুছে যাবে। এই কাজ পূর্বাবস্থায় ফিরিয়ে আনা সম্ভব নয়।",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDelete = false
                        onClearAllData()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SakoonRose),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("হ্যাঁ, সব মুছুন", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showConfirmDelete = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("বাতিল")
                }
            }
        )
    }
}
