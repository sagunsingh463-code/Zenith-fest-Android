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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zenith26.fest.data.api.ApiClient
import com.zenith26.fest.ui.theme.Emerald300
import com.zenith26.fest.ui.theme.Pink600
import com.zenith26.fest.ui.theme.PureWhite
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Purple900
import com.zenith26.fest.ui.theme.Slate100
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.Slate950

@Composable
fun ApiConfigDialog(
    currentUrl: String,
    onDismiss: () -> Unit,
    onSaveUrl: (String) -> Unit
) {
    var urlInput by remember { mutableStateOf(currentUrl) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(18.dp))
                .background(Slate900)
                .border(1.dp, Slate800, RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚙️ Backend API Configuration",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Slate800)
                            .clickable { onDismiss() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "✕", fontSize = 11.sp, color = Slate400, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = "Configure the FastAPI base URL. For Android Emulator, use 10.0.2.2:8000 to reach your computer's localhost.",
                    fontSize = 11.sp,
                    color = Slate400,
                    lineHeight = 15.sp
                )

                // Quick Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresetButton(
                        label = "Emulator (10.0.2.2)",
                        onClick = { urlInput = ApiClient.DEFAULT_EMULATOR_BASE_URL }
                    )
                    PresetButton(
                        label = "Localhost (127.0.0.1)",
                        onClick = { urlInput = ApiClient.LOCALHOST_BASE_URL }
                    )
                }

                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    label = { Text("Base URL", fontSize = 11.sp, color = Slate400) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Slate950,
                        unfocusedContainerColor = Slate950,
                        focusedBorderColor = Purple500,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate100
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onDismiss() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(text = "Cancel", fontSize = 12.sp, color = Slate400)
                    }

                    Spacer(modifier = Modifier.size(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.horizontalGradient(listOf(Purple600, Pink600)))
                            .clickable {
                                if (urlInput.isNotBlank()) {
                                    onSaveUrl(urlInput.trim())
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(text = "Save & Reconnect", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetButton(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Purple900.copy(alpha = 0.4f))
            .border(1.dp, Purple500.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Emerald300)
    }
}
