package com.example.jeva.domain.repository

import com.example.jeva.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, javaPass: String): Result<User>
    suspend fun register(name: String, email: String, javaPass: String): Result<User>
}