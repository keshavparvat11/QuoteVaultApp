package com.example.quotevault

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat.enableEdgeToEdge
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.quotevault.data.remote.testSupabaseConnection
import com.example.quotevault.navigation.AppNavigation
import com.example.quotevault.notification.DailyQuoteNotification
import com.example.quotevault.ui.theme.AppThemeViewModel
import com.example.quotevault.ui.theme.QuoteVaultTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) {}

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= 33) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            DailyQuoteNotification.createChannel(this)
            val themeViewModel: AppThemeViewModel = hiltViewModel()

            val theme by themeViewModel.theme.collectAsState()
            val fontSize by themeViewModel.fontSize.collectAsState()
            val accentColor by themeViewModel.accentColor.collectAsState()

            QuoteVaultTheme(
                theme = theme,
                fontSize = fontSize,
                accentColor = accentColor
            ) {
                AppNavigation()
            }
        }
    }
}
