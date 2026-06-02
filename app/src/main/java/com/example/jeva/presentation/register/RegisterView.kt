package com.example.jeva.presentation.register

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.jeva.domain.usecase.RegisterUseCase
import com.example.jeva.presentation.components.DocumentScannerView
import kotlinx.coroutines.launch

@Composable
fun RegisterView(
    registerUseCase: RegisterUseCase,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Control del flujo por pasos (KYC)
    var scanDocumentStep by remember { mutableStateOf(true) }
    var frontDocumentUri by remember { mutableStateOf<Uri?>(null) }
    var backDocumentUri by remember { mutableStateOf<Uri?>(null) }

    // PASO 1 Y 2: Escaneo secuencial del documento de identidad
    if (scanDocumentStep) {
        DocumentScannerView(
            onFlowCompleted = { front, back ->
                frontDocumentUri = front
                backDocumentUri = back
                scanDocumentStep = false // Avanza al formulario de datos adicionales
                Log.d("JevaRegister", "Fotos capturadas con éxito - Frontal: $front | Reverso: $back")
            },
            onBack = onBackToLogin
        )
    } else {
        // PASO 3: Formulario de Registro con los datos adicionales
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Jeva - Registro", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            errorMessage = ""

                            val frontPath = frontDocumentUri?.toString() ?: "vacio"
                            val backPath = backDocumentUri?.toString() ?: "vacio"

                            // Se envían los 5 parámetros obligatorios al caso de uso actualizado
                            val result = registerUseCase(name, email, password, frontPath, backPath)

                            isLoading = false
                            if (result.isSuccess) {
                                onRegisterSuccess()
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Error al registrar"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar")
                }
            }

            TextButton(onClick = onBackToLogin, enabled = !isLoading) {
                Text("Ya tengo cuenta")
            }
        }
    }
}