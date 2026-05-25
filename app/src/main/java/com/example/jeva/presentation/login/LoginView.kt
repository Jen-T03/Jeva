package com.example.jeva.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jeva.domain.usecase.LoginUseCase
import com.example.jeva.presentation.components.JevaTextField
import kotlinx.coroutines.launch

@Composable
fun LoginView(
    loginUseCase: LoginUseCase,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // Estado de carga local
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Jeva - Login", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))

        // Uso del componente reutilizable
        JevaTextField(value = email, onValueChange = { email = it }, label = "Correo electrónico")
        Spacer(modifier = Modifier.height(12.dp))
        JevaTextField(value = password, onValueChange = { password = it }, label = "Contraseña", isPassword = true)

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator() // Spinner de carga activo
        } else {
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = ""
                        val result = loginUseCase(email, password)
                        isLoading = false
                        if (result.isSuccess) {
                            onLoginSuccess()
                        } else {
                            errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar")
            }
        }

        TextButton(onClick = onNavigateToRegister, enabled = !isLoading) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}