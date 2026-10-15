package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.theme.Amber400
import com.zenith26.fest.ui.theme.Pink500
import com.zenith26.fest.ui.theme.PureWhite
import com.zenith26.fest.ui.theme.Purple300
import com.zenith26.fest.ui.theme.Purple400
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Purple900
import com.zenith26.fest.ui.theme.Slate100
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900

@Composable
fun TopNavBar(
    isAdminMode: Boolean,
    onNavigateEvents: () -> Unit,
    onNavigateAdmin: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val logoGradient = Brush.linearGradient(
        listOf(Purple600, Pink500, Amber400)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate900.copy(alpha = 0.95f))
            .border(width = 1.dp, color = Slate800, shape = RoundedCornerShape(0.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onNavigateEvents() }
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(logoGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚡",
                        fontSize = 18.sp,
                        color = PureWhite
                    )
                }

                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ZENITH '26",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp,
                            color = PureWhite
                        )
                        if (isAdminMode) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Purple500.copy(alpha = 0.2f))
                                    .border(1.dp, Purple500.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "ADMIN",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Purple300
                                )
                            }
                        }
                    }
                    Text(
                        text = if (isAdminMode) "Fest Operations & Analytics" else "ANNUAL TECH-CULTURAL FEST",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isAdminMode) Slate400 else Purple400,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Right Action Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isAdminMode) {
                    // Switch to Admin Dashboard
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Slate800)
                            .border(1.dp, Slate700, RoundedCornerShape(8.dp))
                            .clickable { onNavigateAdmin() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📊 ", fontSize = 12.sp)
                            Text(
                                text = "Admin",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate300
                            )
                        }
                    }
                } else {
                    // Switch to Student Portal
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Purple900.copy(alpha = 0.6f))
                            .border(1.dp, Purple500.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { onNavigateEvents() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Portal ↗",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Purple300
                        )
                    }
                }

                // Settings icon for API URL config
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate800.copy(alpha = 0.7f))
                        .border(1.dp, Slate700, RoundedCornerShape(8.dp))
                        .clickable { onOpenSettings() }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⚙️", fontSize = 12.sp)
                }
            }
        }
    }
}
