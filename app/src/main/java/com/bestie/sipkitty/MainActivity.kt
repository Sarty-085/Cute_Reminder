package com.bestie.sipkitty

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.bestie.sipkitty.ui.components.UpdateDialog
import com.bestie.sipkitty.ui.screens.HistoryScreen
import com.bestie.sipkitty.ui.screens.HomeScreen
import com.bestie.sipkitty.ui.screens.SettingsScreen
import com.bestie.sipkitty.ui.theme.CardSurface
import com.bestie.sipkitty.ui.theme.SakuraPink
import com.bestie.sipkitty.ui.theme.SipKittyTheme
import com.bestie.sipkitty.ui.theme.SoftPink
import com.bestie.sipkitty.ui.theme.TextPrimary
import com.bestie.sipkitty.ui.theme.TextSecondary
import com.bestie.sipkitty.ui.viewmodel.WaterViewModel
import com.bestie.sipkitty.updater.UpdateInfo

sealed class Screen(val title: String, val icon: ImageVector) {
    object Home : Screen("Home", Icons.Default.Home)
    object Journey : Screen("Journey", Icons.Default.EmojiEvents)
    object Settings : Screen("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: WaterViewModel by viewModels()

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // Notification permission result handled
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkNotificationPermission()

        // Check for updates on startup (silent check)
        viewModel.checkForUpdates(silent = true)

        setContent {
            SipKittyTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: WaterViewModel) {
    val context = LocalContext.current
    var selectedScreenIndex by remember { mutableIntStateOf(0) }
    val screens = listOf(Screen.Home, Screen.Journey, Screen.Settings)

    val updateInfo by viewModel.updateInfo.collectAsState()
    val isDownloading by viewModel.isDownloadingUpdate.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = CardSurface,
                tonalElevation = 8.dp
            ) {
                screens.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text(screen.title) },
                        selected = selectedScreenIndex == index,
                        onClick = { selectedScreenIndex = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SakuraPink,
                            selectedTextColor = TextPrimary,
                            indicatorColor = SoftPink.copy(alpha = 0.5f),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedScreenIndex) {
            0 -> HomeScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            1 -> HistoryScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            2 -> SettingsScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
        }

        // Update Dialog Popup
        if (updateInfo != null) {
            UpdateDialog(
                updateInfo = updateInfo!!,
                isDownloading = isDownloading,
                downloadProgress = downloadProgress,
                onStartUpdate = { viewModel.downloadAndInstallUpdate(context) },
                onDismiss = { viewModel.dismissUpdateDialog() }
            )
        }
    }
}
