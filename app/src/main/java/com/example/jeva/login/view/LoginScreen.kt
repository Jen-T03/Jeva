package com.example.jeva.login.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import com.example.jeva.login.viewmodel.LoginViewModel
import com.example.jeva.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BlackPrimary)
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("JEVA", color = RedPrimary)

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = viewModel.user,
            onValueChange = { viewModel.onUserChange(it) },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (viewModel.errorMessage.isNotEmpty()) {
            Text(text = viewModel.errorMessage, color = RedPrimary)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (viewModel.isRegister) viewModel.onRegisterClick()
                else viewModel.onLoginClick()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
        ) {
            Text(if (viewModel.isRegister) "Registrarse" else "Ingresar", color = WhiteText)
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = { viewModel.toggleMode() }) {
            Text(
                if (viewModel.isRegister) "¿Ya tienes cuenta? Iniciar sesión"
                else "¿No tienes cuenta? Registrarse",
                color = WhiteText
            )
        }
    }
}