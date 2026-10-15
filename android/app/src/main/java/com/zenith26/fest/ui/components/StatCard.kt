package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.theme.Amber400
import com.zenith26.fest.ui.theme.Cyan400
import com.zenith26.fest.ui.theme.Emerald400
import com.zenith26.fest.ui.theme.Emerald500
import com.zenith26.fest.ui.theme.Purple400
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Slate100
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate500
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: String,
    iconColor: Color,
    utilizationBarPct: Float? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Slate900.copy(alpha = 0.95f))
            .border(1.dp, Slate800, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate400
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.15f))
                        .border(1.dp, iconColor.copy(alpha = 0.25f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 14.sp)
                }
            }

            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = if (utilizationBarPct != null) Emerald400 else Slate100,
                modifier = Modifier.padding(top = 8.dp)
            )

            if (utilizationBarPct != null) {
                // Progress Bar
                val clamped = (utilizationBarPct / 100f).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Slate800)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(clamped)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(listOf(Emerald500, Purple500))
                            )
                    )
                }
            }

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Slate500,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
