package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.MtuHeader
import com.example.ui.model.AdminTab
import com.example.ui.model.CashierTab
import com.example.ui.model.StudentTab
import com.example.ui.model.UserActor
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.CashierScannerScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.StudentMealCardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MealCardViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppRoot()
            }
        }
    }
}

@Composable
fun MainAppRoot(viewModel: MealCardViewModel = viewModel()) {
    val authenticatedUser by viewModel.authenticatedUser.collectAsState()
    val activeSession by viewModel.activeSession.collectAsState()
    val isOfflineMode by viewModel.isOfflineMode.collectAsState()
    val unsyncedCount by viewModel.unsyncedCount.collectAsState()

    // Sub-tab states for each actor
    val studentTab by viewModel.studentTab.collectAsState()
    val cashierTab by viewModel.cashierTab.collectAsState()
    val adminTab by viewModel.adminTab.collectAsState()

    // 1. If not authenticated, display Login Screen
    if (authenticatedUser == null) {
        LoginScreen(
            onLoginSuccess = { actor, username ->
                viewModel.login(actor, username)
            }
        )
        return
    }

    val user = authenticatedUser!!

    // 2. Authenticated Actor Screen
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            MtuHeader(
                currentUser = user,
                activeSession = activeSession,
                isOfflineMode = isOfflineMode,
                onToggleOfflineMode = { viewModel.toggleOfflineMode() },
                unsyncedCount = unsyncedCount,
                onTriggerSync = { viewModel.syncOfflineRecords() },
                onLogout = { viewModel.logout() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                when (user.actor) {
                    // STUDENT ONLY NAVIGATION
                    UserActor.STUDENT -> {
                        NavigationBarItem(
                            selected = studentTab == StudentTab.MY_CARD,
                            onClick = { viewModel.setStudentTab(StudentTab.MY_CARD) },
                            icon = {
                                Icon(imageVector = Icons.Default.School, contentDescription = "My Meal Card")
                            },
                            label = {
                                Text(
                                    text = "My Meal Card",
                                    fontSize = 11.sp,
                                    fontWeight = if (studentTab == StudentTab.MY_CARD) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF004D40),
                                selectedTextColor = Color(0xFF004D40),
                                indicatorColor = Color(0xFFB2DFDB)
                            ),
                            modifier = Modifier.testTag("nav_student_card")
                        )

                        NavigationBarItem(
                            selected = studentTab == StudentTab.MEAL_STATUS,
                            onClick = { viewModel.setStudentTab(StudentTab.MEAL_STATUS) },
                            icon = {
                                Icon(imageVector = Icons.Default.Restaurant, contentDescription = "Meal Dashboard")
                            },
                            label = {
                                Text(
                                    text = "Meal Dashboard",
                                    fontSize = 11.sp,
                                    fontWeight = if (studentTab == StudentTab.MEAL_STATUS) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF004D40),
                                selectedTextColor = Color(0xFF004D40),
                                indicatorColor = Color(0xFFB2DFDB)
                            ),
                            modifier = Modifier.testTag("nav_student_status")
                        )
                    }

                    // CASHIER ONLY NAVIGATION
                    UserActor.CASHIER -> {
                        NavigationBarItem(
                            selected = cashierTab == CashierTab.SCANNER,
                            onClick = { viewModel.setCashierTab(CashierTab.SCANNER) },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (unsyncedCount > 0) {
                                            Badge(containerColor = Color(0xFFD84315)) {
                                                Text("$unsyncedCount", color = Color.White)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "Turnstile Scanner")
                                }
                            },
                            label = {
                                Text(
                                    text = "Turnstile Scanner",
                                    fontSize = 11.sp,
                                    fontWeight = if (cashierTab == CashierTab.SCANNER) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF004D40),
                                selectedTextColor = Color(0xFF004D40),
                                indicatorColor = Color(0xFFB2DFDB)
                            ),
                            modifier = Modifier.testTag("nav_cashier_scanner")
                        )

                        NavigationBarItem(
                            selected = cashierTab == CashierTab.SHIFT_LOGS,
                            onClick = { viewModel.setCashierTab(CashierTab.SHIFT_LOGS) },
                            icon = {
                                Icon(imageVector = Icons.Default.FormatListBulleted, contentDescription = "Shift Logs")
                            },
                            label = {
                                Text(
                                    text = "Shift Logs",
                                    fontSize = 11.sp,
                                    fontWeight = if (cashierTab == CashierTab.SHIFT_LOGS) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF004D40),
                                selectedTextColor = Color(0xFF004D40),
                                indicatorColor = Color(0xFFB2DFDB)
                            ),
                            modifier = Modifier.testTag("nav_cashier_logs")
                        )
                    }

                    // ADMIN ONLY NAVIGATION
                    UserActor.ADMIN -> {
                        NavigationBarItem(
                            selected = adminTab == AdminTab.HEADCOUNT,
                            onClick = { viewModel.setAdminTab(AdminTab.HEADCOUNT) },
                            icon = {
                                Icon(imageVector = Icons.Default.Assignment, contentDescription = "Campus Headcounts")
                            },
                            label = {
                                Text(
                                    text = "Headcounts",
                                    fontSize = 11.sp,
                                    fontWeight = if (adminTab == AdminTab.HEADCOUNT) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF004D40),
                                selectedTextColor = Color(0xFF004D40),
                                indicatorColor = Color(0xFFB2DFDB)
                            ),
                            modifier = Modifier.testTag("nav_admin_headcount")
                        )

                        NavigationBarItem(
                            selected = adminTab == AdminTab.ROSTER,
                            onClick = { viewModel.setAdminTab(AdminTab.ROSTER) },
                            icon = {
                                Icon(imageVector = Icons.Default.People, contentDescription = "Student Roster")
                            },
                            label = {
                                Text(
                                    text = "Student Roster",
                                    fontSize = 11.sp,
                                    fontWeight = if (adminTab == AdminTab.ROSTER) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF004D40),
                                selectedTextColor = Color(0xFF004D40),
                                indicatorColor = Color(0xFFB2DFDB)
                            ),
                            modifier = Modifier.testTag("nav_admin_roster")
                        )

                        NavigationBarItem(
                            selected = adminTab == AdminTab.WEB_PORTAL,
                            onClick = { viewModel.setAdminTab(AdminTab.WEB_PORTAL) },
                            icon = {
                                Icon(imageVector = Icons.Default.Language, contentDescription = "Web Portal")
                            },
                            label = {
                                Text(
                                    text = "Web Portal",
                                    fontSize = 11.sp,
                                    fontWeight = if (adminTab == AdminTab.WEB_PORTAL) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF004D40),
                                selectedTextColor = Color(0xFF004D40),
                                indicatorColor = Color(0xFFB2DFDB)
                            ),
                            modifier = Modifier.testTag("nav_admin_web")
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (user.actor) {
                UserActor.STUDENT -> StudentMealCardScreen(viewModel = viewModel)
                UserActor.CASHIER -> CashierScannerScreen(viewModel = viewModel)
                UserActor.ADMIN -> AdminDashboardScreen(viewModel = viewModel)
            }
        }
    }
}
