package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.data.model.EventOut
import com.zenith26.fest.ui.theme.Emerald300
import com.zenith26.fest.ui.theme.Emerald500
import com.zenith26.fest.ui.theme.Pink600
import com.zenith26.fest.ui.theme.PureWhite
import com.zenith26.fest.ui.theme.Purple400
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Rose300
import com.zenith26.fest.ui.theme.Rose400
import com.zenith26.fest.ui.theme.Rose500
import com.zenith26.fest.ui.theme.Slate100
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate500
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.getCategoryTheme

@Composable
fun EventCard(
    event: EventOut,
    onViewDetailsClick: (EventOut) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryTheme = getCategoryTheme(event.category)
    val isFull = event.isFull

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Slate900.copy(alpha = 0.95f))
            .border(1.dp, Slate800, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Top Row: Icon + Category Badge + Fee/FULL Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Box
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate800)
                        .border(1.dp, Slate700.copy(alpha = 0.8f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = event.icon ?: "⚡",
                        fontSize = 22.sp
                    )
                }

                // Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(categoryTheme.backgroundColor)
                            .border(1.dp, categoryTheme.borderColor, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = event.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryTheme.textColor
                        )
                    }

                    // Fee or FULL Badge
                    if (isFull) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Rose500.copy(alpha = 0.2f))
                                .border(1.dp, Rose500.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "FULL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Rose300
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Emerald500.copy(alpha = 0.2f))
                                .border(1.dp, Emerald500.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = event.feeDisplay,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald300
                            )
                        }
                    }
                }
            }

            // Title
            Text(
                text = event.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Slate100,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 12.dp)
            )

            // Description snippet
            Text(
                text = event.description ?: "No description provided.",
                fontSize = 12.sp,
                color = Slate400,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Metadata: Date/Time and Venue
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .background(Slate900)
                    .border(
                        width = 1.dp,
                        color = Slate800.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(0.dp)
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📅 ", fontSize = 12.sp)
                    Text(
                        text = "${event.eventDate} • ${event.eventTime}",
                        fontSize = 12.sp,
                        color = Slate300
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📍 ", fontSize = 12.sp)
                    Text(
                        text = event.venue,
                        fontSize = 12.sp,
                        color = Slate300,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Bottom Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 12.dp)
                    .border(0.5.dp, Slate800, RoundedCornerShape(0.dp))
            )

            // Footer: Available seats & View Details button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AVAILABLE SEATS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = Slate500
                    )
                    if (isFull) {
                        Text(
                            text = "0 / ${event.capacity} seats",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Rose400
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${event.availableSeats}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Purple400
                            )
                            Text(
                                text = " / ${event.capacity} seats",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate300
                            )
                        }
                    }
                }

                // Action Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isFull) {
                                Slate800
                            } else {
                                Brush.horizontalGradient(listOf(Purple600, Pink600))
                            }
                        )
                        .border(
                            1.dp,
                            if (isFull) Slate700 else Purple500.copy(alpha = 0.5f),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onViewDetailsClick(event) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isFull) "View Details" else "Register Now",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFull) Slate300 else PureWhite
                        )
                        Text(
                            text = "→",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFull) Slate400 else PureWhite
                        )
                    }
                }
            }
        }
    }
}
