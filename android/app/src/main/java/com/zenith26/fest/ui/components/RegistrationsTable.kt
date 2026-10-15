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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.data.model.EventOut
import com.zenith26.fest.data.model.RegistrationItemOut
import com.zenith26.fest.ui.theme.PureWhite
import com.zenith26.fest.ui.theme.Purple300
import com.zenith26.fest.ui.theme.Purple400
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Rose300
import com.zenith26.fest.ui.theme.Rose500
import com.zenith26.fest.ui.theme.Rose950
import com.zenith26.fest.ui.theme.Slate100
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate500
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.Slate950

@Composable
fun RegistrationsTable(
    registrations: List<RegistrationItemOut>,
    events: List<EventOut>,
    selectedEventId: Int?,
    onFilterEventChanged: (Int?) -> Unit,
    onCancelRegistration: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    var itemToCancel by remember { mutableStateOf<RegistrationItemOut?>(null) }

    val selectedEventTitle = remember(selectedEventId, events) {
        if (selectedEventId == null) "All Events (${events.size})"
        else events.find { it.id == selectedEventId }?.let { "#${it.id} - ${it.title}" } ?: "Event #$selectedEventId"
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Event Filter Dropdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Slate900)
                        .border(1.dp, Slate700, RoundedCornerShape(10.dp))
                        .clickable { dropdownExpanded = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "Filter:", fontSize = 11.sp, color = Slate400, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = selectedEventTitle,
                            fontSize = 11.sp,
                            color = Slate100,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(text = "▼", fontSize = 9.sp, color = Slate400)
                    }
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.background(Slate900).border(1.dp, Slate700, RoundedCornerShape(8.dp))
                ) {
                    DropdownMenuItem(
                        text = { Text("All Events", color = PureWhite, fontSize = 12.sp) },
                        onClick = {
                            dropdownExpanded = false
                            onFilterEventChanged(null)
                        }
                    )
                    events.forEach { ev ->
                        DropdownMenuItem(
                            text = { Text("#${ev.id} - ${ev.title} (${ev.category})", color = Slate300, fontSize = 12.sp) },
                            onClick = {
                                dropdownExpanded = false
                                onFilterEventChanged(ev.id)
                            }
                        )
                    }
                }
            }

            Text(
                text = "Showing ${registrations.size} registrations",
                fontSize = 11.sp,
                color = Slate400
            )
        }

        if (registrations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedEventId != null) "No registrations found for this event." else "No registrations found yet.",
                    fontSize = 13.sp,
                    color = Slate400
                )
            }
        } else {
            registrations.forEach { reg ->
                RegistrationCard(
                    reg = reg,
                    onCancelClick = { itemToCancel = reg }
                )
            }
        }
    }

    // Confirmation Dialog for Cancelling Registration (Stretch Goal 2)
    if (itemToCancel != null) {
        val reg = itemToCancel!!
        AlertDialog(
            onDismissRequest = { itemToCancel = null },
            containerColor = Slate900,
            title = {
                Text(
                    text = "Cancel Registration?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to cancel registration for:\n\nStudent: ${reg.studentName}\nRoll No: ${reg.rollNumber}\nEvent: ${reg.eventTitle}\n\nThis will immediately restore one available seat in the fest database.",
                    fontSize = 13.sp,
                    color = Slate300,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = reg.id
                        itemToCancel = null
                        onCancelRegistration(id)
                    }
                ) {
                    Text(text = "Confirm Cancel", color = Rose400, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToCancel = null }) {
                    Text(text = "Keep Registration", color = Slate400)
                }
            }
        )
    }
}

@Composable
private fun RegistrationCard(
    reg: RegistrationItemOut,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Slate900.copy(alpha = 0.95f))
            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "#${reg.id}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Purple400
                    )
                    Text(
                        text = reg.studentName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Rose950.copy(alpha = 0.6f))
                        .border(1.dp, Rose500.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .clickable { onCancelClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Rose300
                    )
                }
            }

            Text(
                text = "Event: ${reg.eventTitle}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Purple300,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Roll: ${reg.rollNumber}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Slate300
                )
                Text(
                    text = reg.email,
                    fontSize = 11.sp,
                    color = Slate300
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Phone: ${reg.phone ?: "—"}",
                    fontSize = 10.sp,
                    color = Slate500
                )
                Text(
                    text = reg.registeredAt,
                    fontSize = 10.sp,
                    color = Slate500
                )
            }
        }
    }
}
