package com.example.jeva.presentation.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeva.domain.usecase.RegisterUseCase
import kotlinx.coroutines.launch

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {

    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var registerSuccess by mutableStateOf(false)
    var frontPath by mutableStateOf("")
    var backPath by mutableStateOf("")

    fun onNameChange(value: String) { name = value }
    fun onEmailChange(value: String) { email = value.trim() }
    fun onPasswordChange(value: String) { password = value }

    fun register() {
        errorMessage = ""

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            errorMessage = "Completa todos los campos."
            return
        }
        if (!email.contains("@") || !email.contains(".")) {
            errorMessage = "Ingresa un correo válido."
            return
        }
        if (password.length < 6) {
            errorMessage = "La contraseña debe tener al menos 6 caracteres."
            return
        }

        viewModelScope.launch {
            isLoading = true
            val result = registerUseCase(name, email.trim(), password, frontPath, backPath)
            isLoading = false
            if (result.isSuccess) {
                registerSuccess = true
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Error al registrar"
            }
        }
    }
}