package com.example.jeva.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.jeva.data.model.LoginUser
import com.example.jeva.data.repository.AuthRepository

class LoginViewModel : ViewModel() {
    private val repository = AuthRepository()

    // Variables
    var nombre by mutableStateOf("")
    var correo by mutableStateOf("")
    var celular by mutableStateOf("")
    var password by mutableStateOf("")

    // Variables de estado de la interfaz
    var errorMessage by mutableStateOf("")
    var isRegister by mutableStateOf(false) // Alterna entre Login y Registro
    var isLogged by mutableStateOf(false)   // Indica si entramos a la app

    fun onLoginOrRegisterClick() {
        if (correo.isEmpty() || password.isEmpty()) {
            errorMessage = "Por favor, completar los campos obligatorios"
            return
        }

        if (isRegister) {
            //Registro de usuario
            val nuevosDatos = LoginUser(
                nombre = nombre,
                correo = correo,
                celular = celular,
                saldo = 0.0
            )
            repository.registrarUsuario(correo, password, nuevosDatos) { exito, error ->
                if (exito) isLogged = true else errorMessage = error ?: "Error al registrar"
            }
        } else {
            // Inicio de sesión
            repository.iniciarSesion(correo, password) { exito, error ->
                if (exito) isLogged = true else errorMessage = "Correo o contraseña incorrectos"
            }
        }
    }

    fun toggleMode() {
        isRegister = !isRegister
        errorMessage = ""
    }
}