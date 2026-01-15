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
import com.example.quotevault.auth.AuthUiState



@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.uiState.collectAsState()

    // Listen for auth state changes
    LaunchedEffect(isLoggedIn.isLoggedIn) {
        if (isLoggedIn.isLoggedIn && navController.currentDestination?.route == "auth") {
            navController.navigate("home") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn.isLoggedIn) "home" else "auth",
        modifier = modifier
    ) {
        composable("auth") {
            AuthScreen(navController = navController)
        }

        composable("home") {
            HomeScreen(navController = navController)
        }

        composable("quote/{quoteId}") { backStackEntry ->
            val quoteId = backStackEntry.arguments?.getString("quoteId") ?: ""
            QuoteDetailScreen(
                quoteId = quoteId,
                navController = navController
            )
        }

        composable("favorites") {
            FavoritesScreen(navController = navController)
        }

        composable("search") {
            SearchScreen(navController = navController)
        }

        composable("settings") {
            SettingsScreen(navController = navController)
        }
    }
}