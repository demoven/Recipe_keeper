package com.example.recipekeeper.data.repository.impl

import com.example.recipekeeper.data.models.AuthUser
import com.example.recipekeeper.data.repository.IAuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : IAuthRepository {
    private val authInstance: FirebaseAuth = FirebaseAuth.getInstance()

    override val isUserLoggedIn = MutableStateFlow(authInstance.currentUser != null)
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        isUserLoggedIn.value = auth.currentUser != null
    }

    init {
        authInstance.addAuthStateListener(authStateListener)
    }

    override suspend fun register(
        email: String,
        password: String
    ): AuthUser {
        val result = authInstance.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user ?: throw Exception("Registration failed: User is null")
        return mapFirebaseUser(firebaseUser)
    }

    override suspend fun login(
        email: String,
        password: String
    ): AuthUser {
        val result = authInstance.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user ?: throw Exception("Login failed: User is null")
        return mapFirebaseUser(firebaseUser)
    }

    override suspend fun sendEmailVerification() {
        val user = authInstance.currentUser
        if (user != null && !user.isEmailVerified) {
            user.sendEmailVerification().await()
        }
    }

    override fun isEmailVerified(): Boolean {
        return authInstance.currentUser?.isEmailVerified == true
    }

    override suspend fun reloadUser() {
        authInstance.currentUser?.reload()?.await()
    }

    override fun getCurrentUserId(): String? {
        return authInstance.currentUser?.uid
    }

    override fun getCurrentUser(): AuthUser? {
        val firebaseUser = authInstance.currentUser
        return firebaseUser?.let { mapFirebaseUser(it) }
    }

    private fun mapFirebaseUser(user: FirebaseUser): AuthUser {
        return AuthUser(uid = user.uid, email = user.email)
    }

    override fun logout() {
        authInstance.signOut()
    }

    override fun close() {
        authInstance.removeAuthStateListener(authStateListener)
    }

    override suspend fun updatePassword(currentPassword: String, newPassword: String) {
        val user = authInstance.currentUser
        val email = user?.email

        if (user != null && email != null) {
            val credential = EmailAuthProvider.getCredential(email, currentPassword)

            try {
                user.reauthenticate(credential).await()
                user.updatePassword(newPassword).await()
            } catch (e: Exception) {
                throw Exception("Re-authentication failed: ${e.message}")
            }
        } else {
            throw Exception("No authenticated user found.")
        }
    }

    override suspend fun updateEmail(currentPassword: String, newEmail: String) {
        val user = authInstance.currentUser
        val email = user?.email

        if (user != null && email != null) {
            val credential = EmailAuthProvider.getCredential(email, currentPassword)

            try {
                user.reauthenticate(credential).await()
                user.updateEmail(newEmail).await()
                //user.verifyBeforeUpdateEmail(newEmail).await()
            } catch (e: Exception) {
                throw Exception("Re-authentication failed: ${e.message}")
            }
        } else {
            throw Exception("No authenticated user found.")
        }
    }

    override suspend fun deleteAccount(currentPassword: String) {
        val user = authInstance.currentUser
        val email = user?.email

        if (user != null && email != null) {
            val credential = EmailAuthProvider.getCredential(email, currentPassword)

            try {
                user.reauthenticate(credential).await()
                user.delete().await()
            } catch (e: Exception) {
                throw Exception("Re-authentication failed: ${e.message}")
            }
        } else {
            throw Exception("No authenticated user found.")
        }
    }
}