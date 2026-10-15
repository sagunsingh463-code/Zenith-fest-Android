package com.zenith26.fest.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zenith26.fest.data.model.EventCreateIn
import com.zenith26.fest.ui.theme.Emerald300
import com.zenith26.fest.ui.theme.Emerald500
import com.zenith26.fest.ui.theme.Pink600
import com.zenith26.fest.ui.theme.PureWhite
import com.zenith26.fest.ui.theme.Purple300
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Purple900
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

sealed class CreateEventStatus {
    object Idle : CreateEventStatus()
    object Submitting : CreateEventStatus()
    data class Success(val message: String) : CreateEventStatus()
    data class Error(val message: String) : CreateEventStatus()
}

@Composable
fun CreateEventDialog(
    status: CreateEventStatus,
    onDismiss: () -> Unit,
    onSubmit: (EventCreateIn) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Technical") }
    var icon by remember { mutableStateOf("⚡") }
    var eventDate by remember { mutableStateOf("2026-11-14") }
    var eventTime by remember { mutableStateOf("10:00 AM") }
    var venue by remember { mutableStateOf("") }
    var capacityText by remember { mutableStateOf("50") }
    var feeText by remember { mutableStateOf("0") }
    var description by remember { mutableStateOf("") }

    var categoryDropdownOpen by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(20.dp))
                .background(Slate900)
                .border(1.dp, Slate800, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                // Modal Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Purple900.copy(alpha = 0.6f), Slate900)
                            )
                        )
                        .border(1.dp, Slate800, RoundedCornerShape(0.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "✨ ", fontSize = 16.sp)
                                Text(
                                    text = "Create New Festival Event",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite
                                )
                            }
                            Text(
                                text = "Adds event to SQLite and updates portal immediately.",
                                fontSize = 10.sp,
                                color = Slate400,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Slate800)
                                .clickable { onDismiss() }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(text = "✕", fontSize = 12.sp, color = Slate400, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Modal Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Status Alert Banner
                    when (status) {
                        is CreateEventStatus.Success -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Emerald500.copy(alpha = 0.2f))
                                    .border(1.dp, Emerald500.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(text = "✅ ${status.message}", fontSize = 12.sp, color = Emerald300)
                            }
                        }
                        is CreateEventStatus.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Rose950.copy(alpha = 0.6f))
                                    .border(1.dp, Rose500.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(text = "⚠️ ${status.message}", fontSize = 12.sp, color = Rose300)
                            }
                        }
                        else -> Unit
                    }

                    if (localError != null) {
                        Text(text = localError!!, fontSize = 11.sp, color = Rose400)
                    }

                    // Title
                    Column {
                        Text(text = "Event Title *", fontSize = 11.sp, color = Slate300, fontWeight = FontWeight.Medium)
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("e.g. Hackathon Apex", fontSize = 12.sp, color = Slate500) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950,
                                focusedBorderColor = Purple500,
                                unfocusedBorderColor = Slate700,
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate100
                            ),
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                        )
                    }

                    // Category & Icon Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Category *", fontSize = 11.sp, color = Slate300, fontWeight = FontWeight.Medium)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Slate950)
                                    .border(1.dp, Slate700, RoundedCornerShape(8.dp))
                                    .clickable { categoryDropdownOpen = true }
                                    .padding(horizontal = 12.dp, vertical = 14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = category, fontSize = 12.sp, color = Slate100)
                                    Text(text = "▼", fontSize = 10.sp, color = Slate400)
                                }
                            }

                            DropdownMenu(
                                expanded = categoryDropdownOpen,
                                onDismissRequest = { categoryDropdownOpen = false },
                                modifier = Modifier.background(Slate900).border(1.dp, Slate700, RoundedCornerShape(8.dp))
                            ) {
                                listOf("Technical", "Cultural", "Sports", "Gaming", "Workshop").forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat, color = PureWhite, fontSize = 12.sp) },
                                        onClick = {
                                            category = cat
                                            categoryDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(0.5f)) {
                            Text(text = "Icon (Emoji)", fontSize = 11.sp, color = Slate300, fontWeight = FontWeight.Medium)
                            OutlinedTextField(
                                value = icon,
                                onValueChange = { icon = it },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Slate950,
                                    unfocusedContainerColor = Slate950,
                                    focusedBorderColor = Purple500,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = Slate100,
                                    unfocusedTextColor = Slate100
                                ),
                                modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                            )
                        }
                    }

                    // Date & Time Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Event Date *", fontSize = 11.sp, color = Slate300, fontWeight = FontWeight.Medium)
                            OutlinedTextField(
                                value = eventDate,
                                onValueChange = { eventDate = it },
                                placeholder = { Text("YYYY-MM-DD", fontSize = 11.sp, color = Slate500) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Slate950,
                                    unfocusedContainerColor = Slate950,
                                    focusedBorderColor = Purple500,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = Slate100,
                                    unfocusedTextColor = Slate100
                                ),
                                modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Event Time *", fontSize = 11.sp, color = Slate300, fontWeight = FontWeight.Medium)
                            OutlinedTextField(
                                value = eventTime,
                                onValueChange = { eventTime = it },
                                placeholder = { Text("10:00 AM", fontSize = 11.sp, color = Slate500) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Slate950,
                                    unfocusedContainerColor = Slate950,
                                    focusedBorderColor = Purple500,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = Slate100,
                                    unfocusedTextColor = Slate100
                                ),
                                modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                            )
                        }
                    }

                    // Venue
                    Column {
                        Text(text = "Venue *", fontSize = 11.sp, color = Slate300, fontWeight = FontWeight.Medium)
                        OutlinedTextField(
                            value = venue,
                            onValueChange = { venue = it },
                            placeholder = { Text("e.g. Main Auditorium Hall B", fontSize = 12.sp, color = Slate500) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950,
                                focusedBorderColor = Purple500,
                                unfocusedBorderColor = Slate700,
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate100
                            ),
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                        )
                    }

                    // Capacity & Fee Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Capacity *", fontSize = 11.sp, color = Slate300, fontWeight = FontWeight.Medium)
                            OutlinedTextField(
                                value = capacityText,
                                onValueChange = { capacityText = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Slate950,
                                    unfocusedContainerColor = Slate950,
                                    focusedBorderColor = Purple500,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = Slate100,
                                    unfocusedTextColor = Slate100
                                ),
                                modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Fee (₹) (0 for free)", fontSize = 11.sp, color = Slate300, fontWeight = FontWeight.Medium)
                            OutlinedTextField(
                                value = feeText,
                                onValueChange = { feeText = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Slate950,
                                    unfocusedContainerColor = Slate950,
                                    focusedBorderColor = Purple500,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = Slate100,
                                    unfocusedTextColor = Slate100
                                ),
                                modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                            )
                        }
                    }

                    // Description
                    Column {
                        Text(text = "Description", fontSize = 11.sp, color = Slate300, fontWeight = FontWeight.Medium)
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = { Text("Brief outline of rules, rounds, and requirements...", fontSize = 12.sp, color = Slate500) },
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950,
                                focusedBorderColor = Purple500,
                                unfocusedBorderColor = Slate700,
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate100
                            ),
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                        )
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onDismiss() }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(text = "Cancel", fontSize = 12.sp, color = Slate400)
                        }

                        Spacer(modifier = Modifier.size(8.dp))

                        val isSubmitting = status is CreateEventStatus.Submitting
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.horizontalGradient(listOf(Purple600, Pink600)))
                                .clickable(enabled = !isSubmitting) {
                                    val cap = capacityText.toIntOrNull() ?: 0
                                    val fee = feeText.toIntOrNull() ?: 0
                                    if (title.isBlank() || venue.isBlank() || eventDate.isBlank() || eventTime.isBlank()) {
                                        localError = "Please fill in all required fields."
                                    } else if (cap <= 0) {
                                        localError = "Capacity must be greater than 0."
                                    } else {
                                        localError = null
                                        onSubmit(
                                            EventCreateIn(
                                                title = title.trim(),
                                                category = category,
                                                icon = icon.trim().ifEmpty { "⚡" },
                                                eventDate = eventDate.trim(),
                                                eventTime = eventTime.trim(),
                                                venue = venue.trim(),
                                                capacity = cap,
                                                fee = fee,
                                                description = description.trim().ifEmpty { null }
                                            )
                                        )
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = PureWhite, strokeWidth = 2.dp)
                            } else {
                                Text(text = "Create Event", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                            }
                        }
                    }
                }
            }
        }
    }
}
