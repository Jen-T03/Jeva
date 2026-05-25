package com.example.jeva.domain.usecase

import com.example.jeva.domain.model.User
import com.example.jeva.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, javaPass: String): Result<User> {
        if (name.isBlank() || email.isBlank() || javaPass.isBlank()) {
            return Result.failure(Exception("Todos los campos son obligatorios"))
        }
        return repository.register(name, email, javaPass)
    }
}