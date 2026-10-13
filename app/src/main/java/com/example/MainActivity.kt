package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.FilterBottomSheet
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PremiumScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SavedScreen
import com.example.ui.screens.TenderDetailScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.TenderViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TenderViewModel by viewModels {
        val app = application as TenderRadarApplication
        TenderViewModel.Factory(app.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TenderRadarApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenderRadarApp(viewModel: TenderViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    val unreadNotifCount = uiState.notifications.count { !it.isRead }

    if (currentScreen != Screen.Dashboard) {
        BackHandler {
            viewModel.navigateBack()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen != Screen.TenderDetail) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NavyDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Radar,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "TenderRadar BiH",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "ZENICA DIGITAL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    actions = {
                        // Premium CTA Button
                        Surface(
                            color = CyanAccent.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .clickable { viewModel.navigateTo(Screen.Premium) }
                                .padding(end = 8.dp)
                                .testTag("premium_topbar_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "PREMIUM",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            if (currentScreen != Screen.TenderDetail) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreen == Screen.Dashboard,
                        onClick = { viewModel.navigateTo(Screen.Dashboard) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == Screen.Dashboard) Icons.Default.Home else Icons.Outlined.Home,
                                contentDescription = "Početna"
                            )
                        },
                        label = { Text("Početna", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_item_dashboard")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.Feed,
                        onClick = { viewModel.navigateTo(Screen.Feed) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == Screen.Feed) Icons.Default.Search else Icons.Outlined.Search,
                                contentDescription = "Tenderi"
                            )
                        },
                        label = { Text("Tenderi", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_item_feed")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.Saved,
                        onClick = { viewModel.navigateTo(Screen.Saved) },
                        icon = {
                            BadgedBox(badge = {
                                if (uiState.kpis.savedCount > 0) {
                                    Badge { Text("${uiState.kpis.savedCount}") }
                                }
                            }) {
                                Icon(
                                    imageVector = if (currentScreen == Screen.Saved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Moji"
                                )
                            }
                        },
                        label = { Text("Moji", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_item_saved")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.Notifications,
                        onClick = { viewModel.navigateTo(Screen.Notifications) },
                        icon = {
                            BadgedBox(badge = {
                                if (unreadNotifCount > 0) {
                                    Badge { Text("$unreadNotifCount") }
                                }
                            }) {
                                Icon(
                                    imageVector = if (currentScreen == Screen.Notifications) Icons.Default.Notifications else Icons.Outlined.Notifications,
                                    contentDescription = "Obavijesti"
                                )
                            }
                        },
                        label = { Text("Obavijesti", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_item_notifications")
                    )

                    NavigationBarItem(
                        selected = currentScreen == Screen.Profile,
                        onClick = { viewModel.navigateTo(Screen.Profile) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == Screen.Profile) Icons.Default.Business else Icons.Outlined.Person,
                                contentDescription = "Profil"
                            )
                        },
                        label = { Text("Profil", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_item_profile")
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                Screen.Dashboard -> {
                    DashboardScreen(
                        uiState = uiState,
                        onNavigate = { viewModel.navigateTo(it) },
                        onSelectTender = { viewModel.selectTender(it) },
                        onToggleSave = { viewModel.toggleSave(it) },
                        onOpenFilter = { viewModel.setFilterSheetVisible(true) }
                    )
                }

                Screen.Feed -> {
                    FeedScreen(
                        uiState = uiState,
                        onSearchChange = { viewModel.updateSearchQuery(it) },
                        onQuickFilter = { viewModel.setQuickFilter(it) },
                        onOpenFilterSheet = { viewModel.setFilterSheetVisible(true) },
                        onSelectTender = { viewModel.selectTender(it) },
                        onToggleSave = { viewModel.toggleSave(it) },
                        onResetFilters = { viewModel.resetFilters() }
                    )
                }

                Screen.Saved -> {
                    SavedScreen(
                        uiState = uiState,
                        onSelectTender = { viewModel.selectTender(it) },
                        onToggleSave = { viewModel.toggleSave(it) },
                        onNavigateToFeed = { viewModel.navigateTo(Screen.Feed) }
                    )
                }

                Screen.Notifications -> {
                    NotificationsScreen(
                        notifications = uiState.notifications,
                        onNotificationClick = { notif ->
                            viewModel.markNotificationRead(notif.id)
                            notif.tenderId?.let { tId ->
                                val found = uiState.tendersWithMatch.find { it.tender.id == tId }
                                if (found != null) {
                                    viewModel.selectTender(found)
                                }
                            }
                        },
                        onMarkAllRead = { viewModel.markAllNotificationsRead() }
                    )
                }

                Screen.Profile -> {
                    ProfileScreen(
                        currentProfile = uiState.companyProfile,
                        onSaveProfile = { viewModel.updateCompanyProfile(it) }
                    )
                }

                Screen.Premium -> {
                    PremiumScreen()
                }

                Screen.TenderDetail -> {
                    val activeDetail = uiState.activeTenderDetail
                    if (activeDetail != null) {
                        TenderDetailScreen(
                            item = activeDetail,
                            aiSummary = uiState.activeAiSummary,
                            isAiLoading = uiState.isAiLoading,
                            onBack = { viewModel.navigateBack() },
                            onToggleSave = { viewModel.toggleSave(activeDetail.tender.id) },
                            onRequestAiSummary = { viewModel.generateAiSummary(activeDetail.tender) }
                        )
                    } else {
                        viewModel.navigateBack()
                    }
                }
            }

            // Filter Bottom Sheet modal
            if (uiState.showFilterSheet) {
                FilterBottomSheet(
                    currentFilter = uiState.filter,
                    onApplyFilter = { viewModel.applyDetailedFilter(it) },
                    onReset = { viewModel.resetFilters() },
                    onDismiss = { viewModel.setFilterSheetVisible(false) }
                )
            }
        }
    }
}
