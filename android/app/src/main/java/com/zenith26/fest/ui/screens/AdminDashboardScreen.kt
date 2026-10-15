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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.zenith26.fest.ui.components.CategoryBreakdown
import com.zenith26.fest.ui.components.CreateEventDialog
import com.zenith26.fest.ui.components.ErrorStateView
import com.zenith26.fest.ui.components.EventUtilizationTable
import com.zenith26.fest.ui.components.LoadingStateView
import com.zenith26.fest.ui.components.RegistrationsTable
import com.zenith26.fest.ui.components.StatCard
import com.zenith26.fest.ui.components.TopNavBar
import com.zenith26.fest.ui.theme.Amber400
import com.zenith26.fest.ui.theme.Cyan400
import com.zenith26.fest.ui.theme.Emerald400
import com.zenith26.fest.ui.theme.Pink600
import com.zenith26.fest.ui.theme.PureWhite
import com.zenith26.fest.ui.theme.Purple300
import com.zenith26.fest.ui.theme.Purple400
import com.zenith26.fest.ui.theme.Purple500
import com.zenith26.fest.ui.theme.Purple600
import com.zenith26.fest.ui.theme.Slate100
import com.zenith26.fest.ui.theme.Slate300
import com.zenith26.fest.ui.theme.Slate400
import com.zenith26.fest.ui.theme.Slate500
import com.zenith26.fest.ui.theme.Slate700
import com.zenith26.fest.ui.theme.Slate800
import com.zenith26.fest.ui.theme.Slate900
import com.zenith26.fest.ui.theme.Slate950
import com.zenith26.fest.ui.viewmodel.AdminViewModel

enum class AdminTab {
    EVENTS_UTILIZATION,
    REGISTRATIONS_LIST
}

@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    onNavigateToPortal: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.stats.collectAsState()
    val events by viewModel.events.collectAsState()
    val registrations by viewModel.registrations.collectAsState()
    val selectedFilter by viewModel.selectedEventFilter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val createEventStatus by viewModel.createEventStatus.collectAsState()

    var activeTab by remember { mutableStateOf(AdminTab.EVENTS_UTILIZATION) }
    var createEventDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopNavBar(
                isAdminMode = true,
                onNavigateEvents = onNavigateToPortal,
                onNavigateAdmin = { /* already here */ },
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Admin Action Buttons Bar (Refresh, Add Event)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Refresh Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Slate900)
                            .border(1.dp, Slate700, RoundedCornerShape(8.dp))
                            .clickable { viewModel.loadDashboard() }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔄 ", fontSize = 11.sp)
                            Text(text = "Refresh Data", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate300)
                        }
                    }

                    // Add Event Button (Stretch Goal 1)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.horizontalGradient(listOf(Purple600, Pink600)))
                            .clickable { createEventDialogOpen = true }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "+ ", fontSize = 13.sp, fontWeight = FontWeight.Black, color = PureWhite)
                            Text(text = "Add New Event", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                        }
                    }
                }
            }

            // Overview Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FESTIVAL OVERVIEW METRICS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Live DB Metrics",
                        fontSize = 10.sp,
                        color = Slate500
                    )
                }
            }

            // 4 Stat Cards
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Total Events",
                            value = "${stats?.totalEvents ?: events.size}",
                            subtitle = "Configured in catalog",
                            icon = "📅",
                            iconColor = Cyan400,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Total Registrations",
                            value = "${stats?.totalRegistrations ?: registrations.size}",
                            subtitle = "Enrolled students",
                            icon = "✍️",
                            iconColor = Purple400,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val utilPct = stats?.overallUtilizationPct ?: 0.0
                        val totalCap = stats?.totalCapacity ?: 0
                        StatCard(
                            title = "Capacity Utilization",
                            value = "${String.format("%.1f", utilPct)}%",
                            subtitle = "of $totalCap total seats",
                            icon = "📈",
                            iconColor = Emerald400,
                            utilizationBarPct = utilPct.toFloat(),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Upcoming Events",
                            value = "${stats?.upcomingEventsCount ?: events.size}",
                            subtitle = "Today or in future",
                            icon = "⏳",
                            iconColor = Amber400,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Category Breakdown Ribbon
            item {
                CategoryBreakdown(
                    eventsByCategory = stats?.eventsByCategory ?: emptyMap()
                )
            }

            // Tab Navigation: Events Utilization vs Student Registrations
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = Slate800, shape = RoundedCornerShape(0.dp))
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tab 1: Event Capacity & Utilization
                    val isTab1Active = activeTab == AdminTab.EVENTS_UTILIZATION
                    Box(
                        modifier = Modifier
                            .clickable { activeTab = AdminTab.EVENTS_UTILIZATION }
                            .padding(vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🎯 ", fontSize = 12.sp)
                            Text(
                                text = "Event Utilization",
                                fontSize = 13.sp,
                                fontWeight = if (isTab1Active) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isTab1Active) PureWhite else Slate400
                            )
                            Box(
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isTab1Active) Purple900.copy(alpha = 0.6f) else Slate800)
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "${events.size}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isTab1Active) Purple300 else Slate400
                                )
                            }
                        }
                    }

                    // Tab 2: Student Registrations List
                    val isTab2Active = activeTab == AdminTab.REGISTRATIONS_LIST
                    Box(
                        modifier = Modifier
                            .clickable { activeTab = AdminTab.REGISTRATIONS_LIST }
                            .padding(vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📋 ", fontSize = 12.sp)
                            Text(
                                text = "Registrations List",
                                fontSize = 13.sp,
                                fontWeight = if (isTab2Active) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isTab2Active) PureWhite else Slate400
                            )
                            Box(
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isTab2Active) Purple900.copy(alpha = 0.6f) else Slate800)
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "${registrations.size}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isTab2Active) Purple300 else Slate400
                                )
                            }
                        }
                    }
                }
            }

            // Tab Content
            when {
                isLoading -> {
                    item {
                        LoadingStateView(message = "Loading live dashboard metrics from database...")
                    }
                }
                errorMessage != null -> {
                    item {
                        ErrorStateView(
                            errorMessage = errorMessage!!,
                            onRetry = { viewModel.loadDashboard() }
                        )
                    }
                }
                activeTab == AdminTab.EVENTS_UTILIZATION -> {
                    item {
                        EventUtilizationTable(events = events)
                    }
                }
                activeTab == AdminTab.REGISTRATIONS_LIST -> {
                    item {
                        RegistrationsTable(
                            registrations = registrations,
                            events = events,
                            selectedEventId = selectedFilter,
                            onFilterEventChanged = { viewModel.filterRegistrationsByEvent(it) },
                            onCancelRegistration = { viewModel.cancelRegistration(it) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Create Event Dialog
    if (createEventDialogOpen) {
        CreateEventDialog(
            status = createEventStatus,
            onDismiss = {
                createEventDialogOpen = false
                viewModel.resetCreateEventStatus()
            },
            onSubmit = { payload ->
                viewModel.createEvent(payload) {
                    createEventDialogOpen = false
                }
            }
        )
    }
}
