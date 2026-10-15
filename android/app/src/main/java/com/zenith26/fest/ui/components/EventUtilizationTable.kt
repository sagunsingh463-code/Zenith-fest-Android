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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.data.model.EventOut
import com.zenith26.fest.ui.theme.Amber300
import com.zenith26.fest.ui.theme.Amber500
import com.zenith26.fest.ui.theme.Emerald300
import com.zenith26.fest.ui.theme.Emerald500
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Rose300
import com.zenith26.fest.ui.theme.Rose400
import com.zenith26.fest.ui.theme.Rose500
import com.zenith26.fest.ui.theme.Rose950
import com.zenith26.fest.ui.theme.Slate100
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate500
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.getCategoryTheme

@Composable
fun EventUtilizationTable(
    events: List<EventOut>,
    modifier: Modifier = Modifier
) {
    // Sort by utilization percentage descending by default
    val sortedEvents = events.sortedByDescending { it.utilizationPct }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Table Notice & Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sorted by Utilization % descending",
                fontSize = 11.sp,
                color = Slate400
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Rose500)
                )
                Text(
                    text = "≥90% Warning",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Rose400
                )
            }
        }

        // Event List Cards (Responsive mobile representation of utilization table rows)
        sortedEvents.forEach { ev ->
            EventUtilizationRowCard(event = ev)
        }
    }
}

@Composable
private fun EventUtilizationRowCard(
    event: EventOut,
    modifier: Modifier = Modifier
) {
    val isHighDemand = event.isHighDemand
    val isFull = event.isFull
    val categoryTheme = getCategoryTheme(event.category)
    val utilPct = event.utilizationPct

    val backgroundColor = if (isHighDemand) Rose950.copy(alpha = 0.25f) else Slate900.copy(alpha = 0.95f)
    val borderColor = if (isHighDemand) Rose500.copy(alpha = 0.45f) else Slate800

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header: Icon + Title + Category + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = event.icon ?: "⚡", fontSize = 18.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = event.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate100,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "ID: #${event.id} • ${event.feeDisplay} • ${event.venue}",
                            fontSize = 10.sp,
                            color = Slate400,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Status Badge
                if (isFull) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Rose500.copy(alpha = 0.2f))
                            .border(1.dp, Rose500.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = "FULL", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Rose300)
                    }
                } else if (isHighDemand) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Amber500.copy(alpha = 0.2f))
                            .border(1.dp, Amber500.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = "⚠ ALMOST FULL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Amber300)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Emerald500.copy(alpha = 0.15f))
                            .border(1.dp, Emerald500.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = "OPEN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Emerald300)
                    }
                }
            }

            // Middle: Category Badge + Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(categoryTheme.backgroundColor)
                        .border(1.dp, categoryTheme.borderColor, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = event.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = categoryTheme.textColor
                    )
                }

                Text(
                    text = "${event.eventDate} • ${event.eventTime}",
                    fontSize = 11.sp,
                    color = Slate400
                )
            }

            // Bottom: Registered / Capacity and Utilization Progress Bar
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${event.registeredCount}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isHighDemand) Rose400 else Slate100
                        )
                        Text(
                            text = " / ${event.capacity} registered",
                            fontSize = 11.sp,
                            color = Slate400
                        )
                    }

                    Text(
                        text = "${String.format("%.1f", utilPct)}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isHighDemand) Rose400 else Slate300
                    )
                }

                val barColor = if (isHighDemand) Rose500 else if (utilPct > 50) Amber500 else Purple500
                val progressFraction = (utilPct / 100.0).toFloat().coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Slate800)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressFraction)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(barColor)
                    )
                }
            }
        }
    }
}
