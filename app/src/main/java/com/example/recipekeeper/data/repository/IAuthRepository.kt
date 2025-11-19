package com.example.recipekeeper.data.repository

import com.example.recipekeeper.data.models.AuthUser
import kotlinx.coroutines.flow.StateFlow

interface IAuthRepository: AutoCloseable {
    val isUserLoggedIn: StateFlow<Boolean>

    suspend fun register(email: String, password: String): AuthUser
    suspend fun login(email: String, password: String): AuthUser
    fun getCurrentUser(): AuthUser?
    fun getCurrentUserId(): String?
    fun logout()

    override fun close()
}