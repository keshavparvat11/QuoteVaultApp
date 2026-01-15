package com.example.quotevault.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotevault.data.model.AccentColor
import com.example.quotevault.data.model.FontSize
import com.example.quotevault.data.model.Theme
import com.example.quotevault.data.model.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppThemeViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val theme = userPreferences.getTheme()
        .stateIn(viewModelScope, SharingStarted.Eagerly, Theme.SYSTEM)

    val fontSize = userPreferences.getFontSize()
        .stateIn(viewModelScope, SharingStarted.Eagerly, FontSize.MEDIUM)

    val accentColor = userPreferences.getAccentColor()
        .stateIn(viewModelScope, SharingStarted.Eagerly, AccentColor.PURPLE)
}
