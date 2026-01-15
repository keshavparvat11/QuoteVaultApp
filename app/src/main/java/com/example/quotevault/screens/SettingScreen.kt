package com.example.quotevault.Screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.rotate
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ThumbUp

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import com.example.quotevault.data.model.AccentColor
import com.example.quotevault.data.model.FontSize
import com.example.quotevault.data.model.Theme

import com.example.quotevault.data.model.UserPreferences
import com.example.quotevault.notification.NotificationScheduler
import com.example.quotevault.quotes.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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
    val context = LocalContext.current
    var showThemeDialog by remember { mutableStateOf(false) }
    var showFontDialog by remember { mutableStateOf(false) }
    var showAccentDialog by remember { mutableStateOf(false) }
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()

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
                    icon = Icons.Default.DarkMode,
                    title = "Theme",
                    subtitle = when (theme) {
                        Theme.LIGHT -> "Light"
                        Theme.DARK -> "Dark"
                        Theme.SYSTEM -> "System"
                    },
                    onClick = { showThemeDialog = true }
                )

                SettingsItem(
                    icon = Icons.Default.Colorize,
                    title = "Accent Color",
                    subtitle = when (accentColor) {
                        AccentColor.PURPLE -> "Purple"
                        AccentColor.BLUE -> "Blue"
                        AccentColor.GREEN -> "Green"
                        AccentColor.ORANGE -> "Orange"
                        AccentColor.RED -> "Red"
                    },
                    onClick = { showAccentDialog = true }
                )

                SettingsItem(
                    icon = Icons.Default.FormatSize,
                    title = "Font Size",
                    subtitle = when (fontSize) {
                        FontSize.SMALL -> "Small"
                        FontSize.MEDIUM -> "Medium"
                        FontSize.LARGE -> "Large"
                        FontSize.XLARGE -> "Extra Large"
                    },
                    onClick = { showFontDialog = true }
                )
            }

            // Notifications
            SettingsCategory(title = "Notifications") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Daily Quote")
                        Text(
                            if (notificationsEnabled)
                                "Enabled • $notificationTime"
                            else
                                "Disabled",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = {
                            viewModel.toggleNotifications(context, it)
                        }
                    )
                }
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
                                popUpTo(0)
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
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose Theme") },
            text = {
                Column {
                    Theme.values().forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clickable {
                                    viewModel.updateTheme(option)
                                    showThemeDialog = false
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = theme == option,
                                onClick = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(option.name.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
    if (showFontDialog) {
        AlertDialog(
            onDismissRequest = { showFontDialog = false },
            title = { Text("Font Size") },
            text = {
                Column {
                    FontSize.values().forEach { size ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clickable {
                                    viewModel.updateFontSize(size)
                                    showFontDialog = false
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = fontSize == size,
                                onClick = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(size.name)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
    if (showAccentDialog) {
        AlertDialog(
            onDismissRequest = { showAccentDialog = false },
            title = { Text("Accent Color") },
            text = {
                Column {
                    AccentColor.values().forEach { color ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clickable {
                                    viewModel.updateAccentColor(color)
                                    showAccentDialog = false
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = accentColor == color,
                                onClick = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(color.name)
                        }
                    }
                }
            },
            confirmButton = {}
        )
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

    private val _notificationsEnabled = MutableStateFlow(true)


    val notificationsEnabled = userPreferences.isNotificationsEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val notificationTime = userPreferences.getNotificationTime()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "09:00")

    fun toggleNotifications(context: Context, enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotificationsEnabled(enabled)

            if (enabled) {
                NotificationScheduler.scheduleDailyQuote(context, 9, 0)
            } else {
                NotificationScheduler.cancelDailyQuote(context)
            }
        }
    }
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
            userPreferences.isNotificationEnabled().collect {
                _notificationsEnabled.value = it
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