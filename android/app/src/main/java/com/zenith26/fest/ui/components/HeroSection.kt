package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.theme.Amber300
import com.zenith26.fest.ui.theme.Pink400
import com.zenith26.fest.ui.theme.Purple300
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Purple900
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.Slate950

@Composable
fun HeroSection(modifier: Modifier = Modifier) {
    val textGradient = Brush.linearGradient(
        listOf(Purple300, Pink400, Amber300)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Purple900.copy(alpha = 0.35f),
                        Slate950
                    )
                )
            )
            .border(width = 1.dp, color = Slate800.copy(alpha = 0.6f), shape = RoundedCornerShape(0.dp))
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Fest Date Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Purple900.copy(alpha = 0.4f))
                    .border(1.dp, Purple500.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "📅", fontSize = 12.sp)
                    Text(
                        text = "Nov 14 – 15, 2026",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Purple300
                    )
                    Text(text = "•", fontSize = 12.sp, color = Purple500)
                    Text(
                        text = "Campus Grounds",
                        fontSize = 12.sp,
                        color = Purple300
                    )
                }
            }

            // Headline
            Text(
                text = "Ignite the Stage.\nBuild the Future.",
                style = TextStyle(
                    brush = textGradient,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 34.sp,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-0.5).sp
                ),
                modifier = Modifier.padding(top = 12.dp)
            )

            // Tagline
            Text(
                text = "Explore 14 flagship competitions, workshops, performances, and tournaments. Register with your college roll number and secure your spot!",
                fontSize = 13.sp,
                color = Slate400,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
            )

            // Quick Metrics Ribbon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                MetricPill(icon = "🔥", label = "14 Events")
                MetricPill(icon = "⚡", label = "5 Tracks", modifier = Modifier.padding(horizontal = 6.dp))
                MetricPill(icon = "🎓", label = "Open to All")
            }
        }
    }
}

@Composable
private fun MetricPill(
    icon: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Slate900)
            .border(1.dp, Slate800, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 11.sp)
            Text(
                text = " $label",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Slate300
            )
        }
    }
}
