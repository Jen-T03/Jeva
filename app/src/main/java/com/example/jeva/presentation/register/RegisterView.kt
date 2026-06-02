package com.example.jeva.presentation.register

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jeva.domain.usecase.RegisterUseCase
import com.example.jeva.presentation.components.DocumentScannerView
import com.example.jeva.presentation.components.JevaTextField
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

    var scanDocumentStep by remember { mutableStateOf(true) }

    // Almacenar las dos rutas de los archivos capturados
    var frontDocumentUri by remember { mutableStateOf<Uri?>(null) }
    var backDocumentUri by remember { mutableStateOf<Uri?>(null) }

    if (scanDocumentStep) {
        DocumentScannerView(
            onFlowCompleted = { front, back ->
                frontDocumentUri = front
                backDocumentUri = back
                scanDocumentStep = false // AL FIN DESBLOQUEA Y PASA AL FORMULARIO
                Log.d("JevaKYC", "Frontal: $front | Reverso: $back")
            },
            onBack = onBackToLogin
        )
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Jeva - Datos Adicionales", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            JevaTextField(value = name, onValueChange = { name = it }, label = "Nombre Completo")
            Spacer(modifier = Modifier.height(12.dp))
            JevaTextField(value = email, onValueChange = { email = it }, label = "Correo Electrónico")
            Spacer(modifier = Modifier.height(12.dp))
            JevaTextField(value = password, onValueChange = { password = it }, label = "Contraseña de la Cuenta", isPassword = true)

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

                            // Enviamos los 5 parámetros limpios
                            val result = registerUseCase(name, email, password, frontPath, backPath)

                            isLoading = false
                            if (result.isSuccess) {
                                onRegisterSuccess()
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Error en el registro"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Completar Registro")
                }
            }
        }
    }
}