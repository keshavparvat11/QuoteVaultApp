package com.example.quotevault.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quotevault.Screens.HomeScreen
import com.example.quotevault.auth.AuthViewModel
import com.example.quotevault.favorites.FavoritesScreen
import com.quotevault.presentation.auth.AuthScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.quotevault.Screens.QuoteDetailScreen
import com.example.quotevault.Screens.SearchScreen
import com.example.quotevault.Screens.SettingsScreen
import com.example.quotevault.Screens.SplashScreen
import com.example.quotevault.auth.AuthUiState

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val uiState by authViewModel.uiState.collectAsState()

    /** Check auth once when app starts */
    LaunchedEffect(Unit) {
        authViewModel.checkAuthState()
    }

    /** Redirect automatically */
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            navController.navigate("home") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (uiState.isLoggedIn) "home" else "auth"
    ) {
        composable("auth") {
            AuthScreen(navController)
        }
        composable("home") {
            HomeScreen(navController)
        }
        composable("favorites") {
            FavoritesScreen(navController)
        }
        composable("search") {
            SearchScreen(navController)
        }
        composable("settings") {
            SettingsScreen(navController)
        }
    }
}
