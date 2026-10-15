package com.zenith26.fest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zenith26.fest.ui.components.CategoryFilterBar
import com.zenith26.fest.ui.components.EmptyStateView
import com.zenith26.fest.ui.components.ErrorStateView
import com.zenith26.fest.ui.components.EventCard
import com.zenith26.fest.ui.components.EventDetailDialog
import com.zenith26.fest.ui.components.HeroSection
import com.zenith26.fest.ui.components.LoadingStateView
import com.zenith26.fest.ui.components.TopNavBar
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Slate100
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate500
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.Slate950
import com.zenith26.fest.ui.viewmodel.StudentViewModel

@Composable
fun StudentPortalScreen(
    viewModel: StudentViewModel,
    onNavigateToAdmin: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedEvent by viewModel.selectedEvent.collectAsState()
    val registrationStatus by viewModel.registrationStatus.collectAsState()

    Scaffold(
        topBar = {
            TopNavBar(
                isAdminMode = false,
                onNavigateEvents = { /* already here */ },
                onNavigateAdmin = onNavigateToAdmin,
                onOpenSettings = onOpenSettings
            )
        },
        containerColor = Slate950,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Slate950)
        ) {
            // Hero Section
            item {
                HeroSection()
            }

            // Search Bar & Result Count
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Search textfield
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            placeholder = {
                                Text(
                                    text = "Search events by title...",
                                    fontSize = 12.sp,
                                    color = Slate400
                                )
                            },
                            leadingIcon = {
                                Text(text = "🔍", fontSize = 14.sp)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { viewModel.clearSearch() }
                                            .padding(4.dp)
                                    ) {
                                        Text(text = "✕", fontSize = 12.sp, color = Slate400)
                                    }
                                }
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate900,
                                unfocusedContainerColor = Slate900,
                                focusedBorderColor = Purple500,
                                unfocusedBorderColor = Slate700,
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate100
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Result count indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isLoading) "Fetching events..."
                            else if (events.isEmpty()) "0 events found"
                            else "Showing ${events.size} event${if (events.size == 1) "" else "s"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate400
                        )
                    }

                    // Category Filter Pills Bar
                    CategoryFilterBar(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { viewModel.selectCategory(it) }
                    )
                }
            }

            // Main Content Area: Loading, Error, Empty, or Event Cards List
            when {
                isLoading -> {
                    item {
                        LoadingStateView()
                    }
                }
                errorMessage != null -> {
                    item {
                        ErrorStateView(
                            errorMessage = errorMessage!!,
                            onRetry = { viewModel.loadEvents() }
                        )
                    }
                }
                events.isEmpty() -> {
                    item {
                        EmptyStateView(
                            onReset = { viewModel.resetFilters() }
                        )
                    }
                }
                else -> {
                    items(events, key = { it.id }) { event ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            EventCard(
                                event = event,
                                onViewDetailsClick = { viewModel.openEventDetail(it) }
                            )
                        }
                    }
                }
            }

            // Footer
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate900)
                        .border(width = 1.dp, color = Slate800, shape = RoundedCornerShape(0.dp))
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Zenith '26 College Fest Management System",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate400
                        )
                        Text(
                            text = "PM2-01 & PM2-02 Capstone • Native Kotlin Android Application",
                            fontSize = 10.sp,
                            color = Slate500
                        )
                    }
                }
            }
        }
    }

    // Event Detail & Registration Dialog
    if (selectedEvent != null) {
        EventDetailDialog(
            event = selectedEvent!!,
            formStatus = registrationStatus,
            onDismiss = { viewModel.closeEventDetail() },
            onSubmitRegistration = { eventId, form ->
                viewModel.submitRegistration(eventId, form)
            }
        )
    }
}
