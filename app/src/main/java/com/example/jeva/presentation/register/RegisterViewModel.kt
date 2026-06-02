package com.example.jeva.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeva.domain.usecase.RegisterUseCase
import kotlinx.coroutines.launch

class RegisterViewModel(private val registerUseCase: RegisterUseCase) : ViewModel() {
    // ... otros estados que tenga el ViewModel de tu compañera ...

    fun registerUser(name: String, email: String, javaPass: String, frontPath: String, backPath: String, onResult: (Result<Any>) -> Unit) {
        viewModelScope.launch {
            // Se invoca el caso de uso con los 5 parámetros requeridos
            val result = registerUseCase(name, email, javaPass, frontPath, backPath)
            // Manejo del resultado según la estructura de tu compañera...
        }
    }
}