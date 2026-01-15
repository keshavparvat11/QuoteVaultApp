package com.example.quotevault.Screens

import androidx.compose.ui.draw.rotate
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ThumbUp

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import com.example.quotevault.data.model.AccentColor
import com.example.quotevault.data.model.FontSize
import com.example.quotevault.data.model.Theme

import com.example.quotevault.data.model.UserPreferences
import com.example.quotevault.quotes.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val theme by viewModel.theme.collectAsState()
    val notificationTime by viewModel.notificationTime.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val accentColor by viewModel.accentColor.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Theme Settings
            SettingsCategory(title = "Appearance") {
                SettingsItem(
                    icon = Icons.Default.ThumbUp,
                    title = "Theme",
                    subtitle = when (theme) {
                        Theme.LIGHT -> "Light"
                        Theme.DARK -> "Dark"
                        Theme.SYSTEM -> "System"
                    },
                    onClick = { /* Open theme picker */ }
                )

                SettingsItem(
                    icon = Icons.Default.ThumbUp,
                    title = "Accent Color",
                    subtitle = when (accentColor) {
                        AccentColor.PURPLE -> "Purple"
                        AccentColor.BLUE -> "Blue"
                        AccentColor.GREEN -> "Green"
                        AccentColor.ORANGE -> "Orange"
                        AccentColor.RED -> "Red"
                    },
                    onClick = { /* Open color picker */ }
                )

                SettingsItem(
                    icon = Icons.Default.KeyboardArrowUp,
                    title = "Font Size",
                    subtitle = when (fontSize) {
                        FontSize.SMALL -> "Small"
                        FontSize.MEDIUM -> "Medium"
                        FontSize.LARGE -> "Large"
                        FontSize.XLARGE -> "Extra Large"
                    },
                    onClick = { /* Open font size picker */ }
                )
            }

            // Notifications
            SettingsCategory(title = "Notifications") {
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Daily Quote",
                    subtitle = "Get notified at $notificationTime",
                    onClick = { /* Open time picker */ }
                )
            }

            // Account
            SettingsCategory(title = "Account") {
                SettingsItem(
                    icon = null,
                    title = "Sign Out",
                    subtitle = null,
                    onClick = {
                        viewModel.signOut {
                            navController.navigate("auth") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    },
                    isDestructive = true
                )
            }

            // About
            SettingsCategory(title = "About") {
                SettingsItem(
                    icon = null,
                    title = "Version",
                    subtitle = "1.0.0",
                    onClick = {}
                )

                SettingsItem(
                    icon = null,
                    title = "Privacy Policy",
                    subtitle = null,
                    onClick = { /* Open privacy policy */ }
                )

                SettingsItem(
                    icon = null,
                    title = "Terms of Service",
                    subtitle = null,
                    onClick = { /* Open terms */ }
                )
            }
        }
    }
}

@Composable
fun SettingsCategory(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            content()
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (isDestructive) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDestructive) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface
                    )

                    subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            if (!isDestructive) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.rotate(180f),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: QuoteRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // Collect flows using stateIn
    private val _theme = MutableStateFlow<Theme>(Theme.SYSTEM)
    val theme: StateFlow<Theme> = _theme.asStateFlow()

    private val _notificationTime = MutableStateFlow("09:00")
    val notificationTime: StateFlow<String> = _notificationTime.asStateFlow()

    private val _fontSize = MutableStateFlow(FontSize.MEDIUM)
    val fontSize: StateFlow<FontSize> = _fontSize.asStateFlow()

    private val _accentColor = MutableStateFlow(AccentColor.PURPLE)
    val accentColor: StateFlow<AccentColor> = _accentColor.asStateFlow()

    init {
        // Initialize flows
        initFlows()
    }

    private fun initFlows() {
        // Collect theme
        viewModelScope.launch {
            userPreferences.getTheme().collect { theme ->
                _theme.value = theme
            }
        }

        // Collect notification time
        viewModelScope.launch {
            userPreferences.getNotificationTime().collect { time ->
                _notificationTime.value = time
            }
        }

        // Collect font size
        viewModelScope.launch {
            userPreferences.getFontSize().collect { size ->
                _fontSize.value = size
            }
        }

        // Collect accent color
        viewModelScope.launch {
            userPreferences.getAccentColor().collect { color ->
                _accentColor.value = color
            }
        }
    }

    fun updateTheme(theme: Theme) {
        viewModelScope.launch {
            userPreferences.setTheme(theme)
        }
    }

    fun updateNotificationTime(time: String) {
        viewModelScope.launch {
            userPreferences.setNotificationTime(time)
        }
    }

    fun updateFontSize(fontSize: FontSize) {
        viewModelScope.launch {
            userPreferences.setFontSize(fontSize)
        }
    }

    fun updateAccentColor(accentColor: AccentColor) {
        viewModelScope.launch {
            userPreferences.setAccentColor(accentColor)
        }
    }

    fun signOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.signOut()
            if (result.isSuccess) {
                onSuccess()
            }
        }
    }
}