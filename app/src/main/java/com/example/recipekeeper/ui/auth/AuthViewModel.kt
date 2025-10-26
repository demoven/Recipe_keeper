package com.example.recipekeeper.ui.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

//    var email by mutableStateOf("")
//        private set
    var password by mutableStateOf("")
        private set
    var confirmPassword by mutableStateOf("")
        private set

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        _uiState.value = _uiState.value.copy(
            isLoggedIn = user != null,
            email = user?.email ?: ""
        )
    }
    init {
        val user = firebaseAuth.currentUser
        _uiState.value = _uiState.value.copy(
            isLoggedIn = user != null,
            email = user?.email ?: ""
        )
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    fun updateEmail(updatedEmail: String) {
        _uiState.value = _uiState.value.copy(email = updatedEmail)
    }

    fun updatePassword(updatedPassword: String) {
        password = updatedPassword
    }
    fun updateConfirmPassword(updatedConfirmPassword: String) {
        confirmPassword = updatedConfirmPassword
    }
    fun resetPassword() {
        password = ""
        confirmPassword = ""
    }
    fun resetFields() {
        _uiState.value = _uiState.value.copy(email = "")
        password = ""
        confirmPassword = ""
    }

    private fun checkPasswordsMatch(): Boolean {
        return password == confirmPassword
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    fun setErrorMessage(message: String?) {
        _uiState.value = _uiState.value.copy(errorMessage = message)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        _uiState.value = _uiState.value.copy(isLoggedIn = isLoggedIn)
    }

    fun register(onSuccess: () -> Unit) {
        if (_uiState.value.email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            setErrorMessage("Please fill in all fields.")
            return
        }
        if (!checkPasswordsMatch()) {
            setErrorMessage("Passwords do not match.")
            return
        }
        firebaseAuth.createUserWithEmailAndPassword(_uiState.value.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    setLoggedIn(true)
                    resetFields()
                    onSuccess()
                } else {
                    Log.d("AuthViewModel", "Registration failed: ${task.exception?.message}" )
                }
            }
    }

    fun login(onSuccess: () -> Unit) {
        if (_uiState.value.email.isBlank() || password.isBlank()) {
            setErrorMessage("Please fill in all fields.")
            return
        }
        firebaseAuth.signInWithEmailAndPassword(_uiState.value.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    setLoggedIn(true)
                    resetFields()
                    onSuccess()
                } else {
                    Log.d("AuthViewModel", "Login failed: ${task.exception?.message}" )
                }
            }
    }

    fun logout() {
        firebaseAuth.signOut()
        setLoggedIn(false)
        resetFields()
    }


}