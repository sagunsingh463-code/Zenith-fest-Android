package com.zenith26.fest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900

data class CategoryFilterItem(
    val id: String,
    val label: String
)

val FestCategories = listOf(
    CategoryFilterItem("All", "All Events"),
    CategoryFilterItem("Technical", "💻 Technical"),
    CategoryFilterItem("Cultural", "🎭 Cultural"),
    CategoryFilterItem("Sports", "⚽ Sports"),
    CategoryFilterItem("Gaming", "🎮 Gaming"),
    CategoryFilterItem("Workshop", "🛠️ Workshop")
)

@Composable
fun CategoryFilterBar(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FestCategories.forEach { cat ->
            val isSelected = selectedCategory.equals(cat.id, ignoreCase = true)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) Purple600 else Slate900
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Purple500 else Slate800,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onCategorySelected(cat.id) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = cat.label,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isSelected) PureWhite else Slate300
                )
            }
        }
    }
}
