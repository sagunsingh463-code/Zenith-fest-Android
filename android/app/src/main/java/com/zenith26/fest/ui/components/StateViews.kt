package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.theme.PureWhite
import com.zenith26.fest.ui.theme.Purple300
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Rose300
import com.zenith26.fest.ui.theme.Rose500
import com.zenith26.fest.ui.theme.Rose950
import com.zenith26.fest.ui.theme.Slate200
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate800

@Composable
fun LoadingStateView(
    message: String = "Loading festival events...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(36.dp),
            color = Purple500,
            strokeWidth = 3.dp
        )
        Text(
            text = message,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Slate400,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun ErrorStateView(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Rose950.copy(alpha = 0.4f))
            .border(1.dp, Rose500.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "⚠️", fontSize = 32.sp)
            Text(
                text = "Could Not Connect to Server",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Rose300,
                textAlign = TextAlign.Center
            )
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                color = Slate300,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Rose500)
                    .clickable { onRetry() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Try Again",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
            }
        }
    }
}

@Composable
fun EmptyStateView(
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "🔎", fontSize = 36.sp)
        Text(
            text = "No events match your search",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Slate200,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Try adjusting your search keywords or switching category filters.",
            fontSize = 12.sp,
            color = Slate400,
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Slate800)
                .border(1.dp, Slate700, RoundedCornerShape(8.dp))
                .clickable { onReset() }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Reset All Filters",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Purple300
            )
        }
    }
}
