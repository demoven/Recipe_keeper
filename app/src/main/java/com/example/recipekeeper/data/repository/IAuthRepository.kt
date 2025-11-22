package com.example.recipekeeper.data.repository

import com.example.recipekeeper.data.models.AuthUser
import kotlinx.coroutines.flow.StateFlow

interface IAuthRepository : AutoCloseable {
    val isUserLoggedIn: StateFlow<Boolean>

    suspend fun register(email: String, password: String): AuthUser
    suspend fun login(email: String, password: String): AuthUser
    suspend fun sendEmailVerification()
    fun isEmailVerified(): Boolean
    suspend fun reloadUser()
    fun getCurrentUser(): AuthUser?
    fun getCurrentUserId(): String?
    fun logout()

    suspend fun updatePassword(currentPassword: String, newPassword: String)
    suspend fun updateEmail(currentPassword: String, newEmail: String)

    override fun close()
}