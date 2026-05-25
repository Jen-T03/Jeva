package com.example.jeva.data.repository

import com.example.jeva.data.datasource.FirebaseUserDataSource
import com.example.jeva.domain.model.User
import com.example.jeva.domain.repository.AuthRepository

class FirebaseAuthRepositoryImpl(
    private val dataSource: FirebaseUserDataSource
) : AuthRepository {

    override suspend fun login(email: String, javaPass: String): Result<User> {
        return try {
            val user = dataSource.authenticateWithFirebase(email, javaPass)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(name: String, email: String, javaPass: String): Result<User> {
        return try {
            val user = dataSource.registerInFirebase(name, email, javaPass)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}