package com.example.jeva.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jeva.ui.login.LoginScreen
import com.example.jeva.ui.login.LoginViewModel
import com.example.jeva.ui.home.HomeScreen
import com.example.jeva.ui.home.HomeViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    // Instanciamos los ViewModels para que gestionen sus pantallas
    val loginViewModel: LoginViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            // Si el usuario se loguea con éxito, navegamos al Home
            if (loginViewModel.isLogged) {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
            LoginScreen(viewModel = loginViewModel)
        }

        composable("home") {
            HomeScreen(viewModel = homeViewModel)
        }
    }
}