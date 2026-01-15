package com.example.quotevault

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.quotevault.Screens.SettingsViewModel
import com.example.quotevault.data.model.AccentColor
import com.example.quotevault.data.model.FontSize
import com.example.quotevault.data.model.Theme
import com.example.quotevault.data.model.UserPreferences
import com.example.quotevault.navigation.AppNavigation
import com.example.quotevault.ui.theme.QuoteVaultTheme
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class QuoteVaultApp : Application(){
    override fun onCreate() {
        super.onCreate()

    }
}