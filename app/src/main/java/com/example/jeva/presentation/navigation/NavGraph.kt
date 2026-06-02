package com.example.jeva.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jeva.data.datasource.FirebaseUserDataSource
import com.example.jeva.data.repository.FirebaseAuthRepositoryImpl
import com.example.jeva.domain.usecase.LoginUseCase
import com.example.jeva.domain.usecase.RegisterUseCase
import com.example.jeva.presentation.home.HomeView
import com.example.jeva.presentation.login.LoginView
import com.example.jeva.presentation.register.RegisterView
import com.example.jeva.presentation.register.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SetupNavGraph(navController: NavHostController) {
    val dataSource = FirebaseUserDataSource()
    val repository = FirebaseAuthRepositoryImpl(dataSource)
    val loginUseCase = LoginUseCase(repository)
    val registerUseCase = RegisterUseCase(repository)

    val registerViewModel = remember { RegisterViewModel(registerUseCase) }

    val currentUser = FirebaseAuth.getInstance().currentUser
    val startRoute = if (currentUser != null) Screen.Home.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startRoute
    ) {
        composable(Screen.Login.route) {
            LoginView(
                loginUseCase = loginUseCase,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterView(
                registerUseCase = registerUseCase, // Le pasamos el caso de uso que declaraste arriba
                onRegisterSuccess = { navController.popBackStack() },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeView(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}