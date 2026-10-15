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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import com.zenith26.fest.data.model.EventOut
import com.zenith26.fest.data.model.RegistrationIn
import com.zenith26.fest.ui.theme.Emerald300
import com.zenith26.fest.ui.theme.Emerald500
import com.zenith26.fest.ui.theme.Pink600
import com.zenith26.fest.ui.theme.PureWhite
import com.zenith26.fest.ui.theme.Purple300
import com.zenith26.fest.ui.theme.Purple400
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Purple900
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
import com.zenith26.fest.ui.theme.Slate950
import com.zenith26.fest.ui.theme.getCategoryTheme

sealed class RegistrationFormStatus {
    object Idle : RegistrationFormStatus()
    object Submitting : RegistrationFormStatus()
    data class Success(val message: String) : RegistrationFormStatus()
    data class Error(val message: String) : RegistrationFormStatus()
}

@Composable
fun EventDetailDialog(
    event: EventOut,
    formStatus: RegistrationFormStatus,
    onDismiss: () -> Unit,
    onSubmitRegistration: (Int, RegistrationIn) -> Unit
) {
    var studentName by remember { mutableStateOf("") }
    var rollNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var rollError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    val categoryTheme = getCategoryTheme(event.category)
    val isFull = event.isFull
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
                                listOf(
                                    Purple900.copy(alpha = 0.5f),
                                    Slate900
                                )
                            )
                        )
                        .border(width = 1.dp, color = Slate800, shape = RoundedCornerShape(0.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Slate800)
                                    .border(1.dp, Slate700, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = event.icon ?: "⚡", fontSize = 24.sp)
                            }

                            Column(modifier = Modifier.padding(start = 12.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(categoryTheme.backgroundColor)
                                            .border(1.dp, categoryTheme.borderColor, RoundedCornerShape(12.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = event.category,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = categoryTheme.textColor
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Emerald500.copy(alpha = 0.2f))
                                            .border(1.dp, Emerald500.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (event.fee > 0) "Fee: ₹${event.fee}" else "Free Entry",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Emerald300
                                        )
                                    }
                                }

                                Text(
                                    text = event.title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        // Close Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Slate800)
                                .clickable { onDismiss() }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✕", fontSize = 12.sp, color = Slate400, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Modal Body Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Quick Info Grid
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate950)
                            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Date & Time", fontSize = 11.sp, color = Slate400)
                                    Text(
                                        text = "${event.eventDate} at ${event.eventTime}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate100
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Venue", fontSize = 11.sp, color = Slate400)
                                    Text(
                                        text = event.venue,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate100
                                    )
                                }
                            }
                            Column {
                                Text(text = "Availability", fontSize = 11.sp, color = Slate400)
                                if (isFull) {
                                    Text(
                                        text = "FULL (0 / ${event.capacity} seats left)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Rose400
                                    )
                                } else {
                                    Text(
                                        text = "${event.availableSeats} / ${event.capacity} seats available",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald300
                                    )
                                }
                            }
                        }
                    }

                    // About this Event
                    Column {
                        Text(
                            text = "ABOUT THIS EVENT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate400,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = event.description ?: "No description provided.",
                            fontSize = 13.sp,
                            color = Slate300,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Slate800)
                    )

                    // Registration Form Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✍️ ", fontSize = 14.sp)
                            Text(
                                text = "Register for this Event",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }

                        Text(
                            text = if (isFull) "Registrations Closed" else "Instant Confirmation",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFull) Rose400 else Purple400
                        )
                    }

                    // Feedback Alert Banner
                    when (formStatus) {
                        is RegistrationFormStatus.Success -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Emerald500.copy(alpha = 0.2f))
                                    .border(1.dp, Emerald500.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "✅ ${formStatus.message}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Emerald300,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                        is RegistrationFormStatus.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Rose950.copy(alpha = 0.6f))
                                    .border(1.dp, Rose500.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "⚠️ Registration Error: ${formStatus.message}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Rose300,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                        else -> Unit
                    }

                    // Form Fields
                    val isSubmitting = formStatus is RegistrationFormStatus.Submitting

                    // Student Name
                    Column {
                        Text(
                            text = "Full Name *",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate300
                        )
                        OutlinedTextField(
                            value = studentName,
                            onValueChange = {
                                studentName = it
                                nameError = null
                            },
                            placeholder = { Text(text = "e.g. Alex Morgan", fontSize = 12.sp, color = Slate500) },
                            isError = nameError != null,
                            enabled = !isFull && !isSubmitting,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950,
                                disabledContainerColor = Slate900,
                                focusedBorderColor = Purple500,
                                unfocusedBorderColor = Slate700,
                                errorBorderColor = Rose500,
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate100
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        )
                        if (nameError != null) {
                            Text(
                                text = nameError!!,
                                fontSize = 10.sp,
                                color = Rose400,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Roll Number
                    Column {
                        Text(
                            text = "Roll Number / Student ID *",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate300
                        )
                        OutlinedTextField(
                            value = rollNumber,
                            onValueChange = {
                                rollNumber = it
                                rollError = null
                            },
                            placeholder = { Text(text = "e.g. 2026CS108", fontSize = 12.sp, color = Slate500) },
                            isError = rollError != null,
                            enabled = !isFull && !isSubmitting,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950,
                                disabledContainerColor = Slate900,
                                focusedBorderColor = Purple500,
                                unfocusedBorderColor = Slate700,
                                errorBorderColor = Rose500,
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate100
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        )
                        if (rollError != null) {
                            Text(
                                text = rollError!!,
                                fontSize = 10.sp,
                                color = Rose400,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Email Address
                    Column {
                        Text(
                            text = "Email Address *",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate300
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                emailError = null
                            },
                            placeholder = { Text(text = "e.g. alex@university.edu", fontSize = 12.sp, color = Slate500) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            isError = emailError != null,
                            enabled = !isFull && !isSubmitting,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950,
                                disabledContainerColor = Slate900,
                                focusedBorderColor = Purple500,
                                unfocusedBorderColor = Slate700,
                                errorBorderColor = Rose500,
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate100
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        )
                        if (emailError != null) {
                            Text(
                                text = emailError!!,
                                fontSize = 10.sp,
                                color = Rose400,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Phone Number (Optional)
                    Column {
                        Row {
                            Text(
                                text = "Phone Number ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Slate300
                            )
                            Text(
                                text = "(Optional)",
                                fontSize = 11.sp,
                                color = Slate500
                            )
                        }
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            placeholder = { Text(text = "e.g. +91 9876543210", fontSize = 12.sp, color = Slate500) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            enabled = !isFull && !isSubmitting,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950,
                                disabledContainerColor = Slate900,
                                focusedBorderColor = Purple500,
                                unfocusedBorderColor = Slate700,
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate100
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        )
                    }

                    // Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onDismiss() }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 12.sp,
                                color = Slate400,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.size(8.dp))

                        val canSubmit = !isFull && !isSubmitting
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (canSubmit) {
                                        Brush.horizontalGradient(listOf(Purple600, Pink600))
                                    } else {
                                        Slate800
                                    }
                                )
                                .clickable(enabled = canSubmit) {
                                    // Validation
                                    var valid = true
                                    val trimmedName = studentName.trim()
                                    val trimmedRoll = rollNumber.trim()
                                    val trimmedEmail = email.trim()

                                    if (trimmedName.isEmpty()) {
                                        nameError = "Please enter your full name"
                                        valid = false
                                    }
                                    if (trimmedRoll.isEmpty()) {
                                        rollError = "Please enter your college roll number"
                                        valid = false
                                    }
                                    if (trimmedEmail.isEmpty()) {
                                        emailError = "Please enter your email address"
                                        valid = false
                                    } else if (!trimmedEmail.contains("@") || trimmedEmail.startsWith("@") || trimmedEmail.endsWith("@")) {
                                        emailError = "Please enter a valid email address containing '@'"
                                        valid = false
                                    }

                                    if (valid) {
                                        onSubmitRegistration(
                                            event.id,
                                            RegistrationIn(
                                                studentName = trimmedName,
                                                rollNumber = trimmedRoll,
                                                email = trimmedEmail,
                                                phone = phone.trim().ifEmpty { null }
                                            )
                                        )
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSubmitting) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        color = PureWhite,
                                        strokeWidth = 2.dp
                                    )
                                    Text(
                                        text = "Registering...",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PureWhite
                                    )
                                }
                            } else {
                                Text(
                                    text = if (isFull) "Event Full" else "Confirm Registration",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (canSubmit) PureWhite else Slate500
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
