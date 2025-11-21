package com.example.recipekeeper.data.repository.impl

import com.example.recipekeeper.data.models.AuthUser
import com.example.recipekeeper.data.repository.IAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl: IAuthRepository {
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
}