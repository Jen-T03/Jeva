package com.example.jeva.domain.usecase

import com.example.jeva.domain.model.User
import com.example.jeva.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, javaPass: String, frontPath: String, backPath: String): Result<User> {
        return repository.register(name, email, javaPass, frontPath, backPath)
    }
}