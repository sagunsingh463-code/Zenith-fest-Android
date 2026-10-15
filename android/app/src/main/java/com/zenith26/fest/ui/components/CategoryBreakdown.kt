package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.theme.PureWhite
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate500
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.Slate950
import com.zenith26.fest.ui.theme.getCategoryTheme

@Composable
fun CategoryBreakdown(
    eventsByCategory: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Slate900.copy(alpha = 0.85f))
            .border(1.dp, Slate800, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "EVENTS BY CATEGORY",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400,
                letterSpacing = 0.5.sp
            )

            if (eventsByCategory.isEmpty()) {
                Text(
                    text = "No category data available",
                    fontSize = 11.sp,
                    color = Slate500
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    eventsByCategory.forEach { (cat, count) ->
                        val theme = getCategoryTheme(cat)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(theme.backgroundColor)
                                .border(1.dp, theme.borderColor, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = theme.textColor
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Slate950.copy(alpha = 0.8f))
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "$count",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
