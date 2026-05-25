package com.example.jeva.domain.usecase

import com.example.jeva.domain.model.User
import com.example.jeva.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, javaPass: String): Result<User> {
        if (email.isBlank() || javaPass.isBlank()) {
            return Result.failure(Exception("El correo y la contraseña no pueden estar vacíos"))
        }
        return repository.login(email, javaPass)
    }
}