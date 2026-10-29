package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.NightlightRound
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.ui.theme.SakoonPrimaryVariant
import com.example.ui.theme.SakoonSurface
import com.example.ui.theme.SakoonTextSecondary
import com.example.ui.viewmodel.SakoonTab

@Composable
fun BottomNavBar(
    selectedTab: SakoonTab,
    onTabSelected: (SakoonTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Floating glass dock
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 14.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = SakoonPrimary.copy(alpha = 0.16f)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(SakoonSurface)
                .border(
                    width = 1.dp,
                    color = SakoonOutline.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 6.dp, vertical = 7.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SakoonTab.values().forEach { tab ->
                    val isSelected = tab == selectedTab

                    val (iconSelected, iconUnselected) = when (tab) {
                        SakoonTab.TODAY -> Icons.Filled.Home to Icons.Outlined.Home
                        SakoonTab.HEALTH -> Icons.Filled.Eco to Icons.Outlined.Eco
                        SakoonTab.FAITH -> Icons.Filled.NightlightRound to Icons.Outlined.NightlightRound
                        SakoonTab.STUDY -> Icons.Filled.MenuBook to Icons.Outlined.MenuBook
                        SakoonTab.HEART -> Icons.Filled.Favorite to Icons.Outlined.FavoriteBorder
                    }

                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.05f else 1.0f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "tab_scale"
                    )

                    val pillBgColor by animateColorAsState(
                        targetValue = if (isSelected) SakoonPrimaryContainer else Color.Transparent,
                        label = "pill_color"
                    )

                    Box(
                        modifier = Modifier
                            .scale(scale)
                            .clip(RoundedCornerShape(20.dp))
                            .background(pillBgColor)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onTabSelected(tab)
                            }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                            .testTag("tab_${tab.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) iconSelected else iconUnselected,
                                contentDescription = tab.title,
                                tint = if (isSelected) SakoonPrimary else SakoonTextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color = if (isSelected) SakoonPrimary else SakoonTextSecondary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
