package com.example.recipekeeper.ui.auth

import android.util.Log
import android.util.Patterns
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

    override fun onCleared() {
        firebaseAuth.removeAuthStateListener(authStateListener)
        super.onCleared()
    }

    fun updateEmail(updatedEmail: String) {
        _uiState.value = _uiState.value.copy(emailError = false, email = updatedEmail)
    }

    fun updatePassword(updatedPassword: String) {
        _uiState.value = _uiState.value.copy(passwordError = false)
        password = updatedPassword
    }
    fun updateConfirmPassword(updatedConfirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPasswordError = false)
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

    fun resetErrors() {
        _uiState.value = _uiState.value.copy(
            emailError = false,
            passwordError = false,
            confirmPasswordError = false,
            loginError = false,
            registerError = false,
        )
    }

    fun setPasswordError(isError: Boolean) {
        _uiState.value = _uiState.value.copy(passwordError = isError)
    }

    fun setRegisterError(isError: Boolean) {
        _uiState.value = _uiState.value.copy(registerError = isError)
    }

    fun setLoginError(isError: Boolean) {
        _uiState.value = _uiState.value.copy(loginError = isError)
    }

    fun setEmailError(message: Boolean) {
        _uiState.value = _uiState.value.copy(emailError = message)
    }

    fun setConfirmPasswordError(message: Boolean) {
        _uiState.value = _uiState.value.copy(confirmPasswordError = message)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        _uiState.value = _uiState.value.copy(isLoggedIn = isLoggedIn)
    }

    private fun validateEmail(email: String): Boolean {
        return if (email.isBlank()) {
            true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            true
        } else false
    }

    private fun isPasswordValid(password: String): Boolean {
        val lengthOk = password.length >= 12
        val upperOk = password.any { it.isUpperCase() }
        val lowerOk = password.any { it.isLowerCase() }
        val specialOk = password.any { !it.isLetterOrDigit() && !it.isWhitespace() }
        return lengthOk && upperOk && lowerOk && specialOk
    }

    private fun validateConfirmPassword(confirmPassword: String): Boolean {
        return if (confirmPassword.isBlank()) {
            true
        } else if (confirmPassword != password) {
            true
        } else false
    }

    fun register() {
        val emailErr = validateEmail(_uiState.value.email)
        setEmailError(emailErr)
        val confirmPasswordErr = validateConfirmPassword(confirmPassword)
        setConfirmPasswordError(confirmPasswordErr)
        val passwordErr = isPasswordValid(password)
        setPasswordError(!passwordErr)

        Log.d("AuthViewModel", "Registering with email: ${_uiState.value.email}")
        if (emailErr || confirmPasswordErr || !passwordErr) {
            return
        }
        firebaseAuth.createUserWithEmailAndPassword(_uiState.value.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    setLoggedIn(true)
                    resetPassword()
                } else {
                    Log.d("AuthViewModel", "Registration failed: ${task.exception?.message}" )
                    setRegisterError(true)
                }
            }
    }

    fun login() {
        val emailErr = validateEmail(_uiState.value.email)
        setEmailError(emailErr)
        val passwordErr = password.isBlank()
        setPasswordError(passwordErr)
        if (emailErr || passwordErr) {
            return
        }
        firebaseAuth.signInWithEmailAndPassword(_uiState.value.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    setLoggedIn(true)
                    resetPassword()
                } else {
                    Log.d("AuthViewModel", "Login failed: ${task.exception?.message}" )
                    setLoginError(true)
                }
            }
    }
    fun logout() {
        firebaseAuth.signOut()
        setLoggedIn(false)
        resetFields()
    }
}