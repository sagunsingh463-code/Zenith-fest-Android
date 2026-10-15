package com.zenith26.fest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.zenith26.fest.data.api.ApiClient
import com.zenith26.fest.ui.components.ApiConfigDialog
import com.zenith26.fest.ui.screens.AdminDashboardScreen
import com.zenith26.fest.ui.screens.StudentPortalScreen
import com.zenith26.fest.ui.theme.Slate950
import com.zenith26.fest.ui.theme.ZenithTheme
import com.zenith26.fest.ui.viewmodel.AdminViewModel
import com.zenith26.fest.ui.viewmodel.StudentViewModel

enum class ScreenState {
    STUDENT_PORTAL,
    ADMIN_DASHBOARD
}

class MainActivity : ComponentActivity() {

    private val studentViewModel: StudentViewModel by viewModels()
    private val adminViewModel: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZenithTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Slate950
                ) {
                    ZenithAppContainer(
                        studentViewModel = studentViewModel,
                        adminViewModel = adminViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun ZenithAppContainer(
    studentViewModel: StudentViewModel,
    adminViewModel: AdminViewModel
) {
    var currentScreen by remember { mutableStateOf(ScreenState.STUDENT_PORTAL) }
    var showConfigDialog by remember { mutableStateOf(false) }

    when (currentScreen) {
        ScreenState.STUDENT_PORTAL -> {
            StudentPortalScreen(
                viewModel = studentViewModel,
                onNavigateToAdmin = {
                    currentScreen = ScreenState.ADMIN_DASHBOARD
                    adminViewModel.loadDashboard()
                },
                onOpenSettings = { showConfigDialog = true }
            )
        }
        ScreenState.ADMIN_DASHBOARD -> {
            AdminDashboardScreen(
                viewModel = adminViewModel,
                onNavigateToPortal = {
                    currentScreen = ScreenState.STUDENT_PORTAL
                    studentViewModel.loadEvents()
                },
                onOpenSettings = { showConfigDialog = true }
            )
        }
    }

    if (showConfigDialog) {
        ApiConfigDialog(
            currentUrl = ApiClient.getBaseUrl(),
            onDismiss = { showConfigDialog = false },
            onSaveUrl = { newUrl ->
                ApiClient.updateBaseUrl(newUrl)
                showConfigDialog = false
                studentViewModel.loadEvents()
                adminViewModel.loadDashboard()
            }
        )
    }
}
