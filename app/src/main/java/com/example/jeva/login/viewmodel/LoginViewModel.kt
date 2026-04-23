package com.example.jeva.login.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.jeva.login.model.LoginUser

class LoginViewModel : ViewModel() {

    var user by mutableStateOf("")
    var password by mutableStateOf("")
    var isLogged by mutableStateOf(false)
    var isRegister by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    private var registeredUser: LoginUser? = null

    fun onUserChange(value: String) {
        user = value
    }

    fun onPasswordChange(value: String) {
        password = value
    }

    fun toggleMode() {
        isRegister = !isRegister
        errorMessage = ""
        user = ""
        password = ""
    }

    fun onLoginClick() {
        if (user.isEmpty() || password.isEmpty()) {
            errorMessage = "Campos vacíos"
            return
        }

        if (registeredUser == null) {
            errorMessage = "Debes registrarte"
            return
        }

        if (user == registeredUser?.username &&
            password == registeredUser?.password
        ) {
            isLogged = true
            errorMessage = ""
        } else {
            errorMessage = "Datos incorrectos"
        }
    }

    fun onRegisterClick() {
        if (user.isEmpty() || password.isEmpty()) {
            errorMessage = "Campos vacíos"
            return
        }

        registeredUser = LoginUser(user, password)
        errorMessage = "Usuario registrado"
        isRegister = false
        user = ""
        password = ""
    }
}