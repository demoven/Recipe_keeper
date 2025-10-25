package com.example.recipekeeper.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var confirmPassword by mutableStateOf("")
        private set

    fun updateEmail(updatedEmail: String) {
        email = updatedEmail
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
        email = ""
        password = ""
        confirmPassword = ""
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


}