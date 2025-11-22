package com.example.recipekeeper.ui.screens.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipekeeper.data.repository.IAuthRepository
import com.example.recipekeeper.tools.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: IAuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    private val validator = AuthValidator()

    fun updateEmail(updatedEmail: String) {
        _uiState.value = _uiState.value.copy(email = updatedEmail, emailError = false)
    }

    fun updatePassword(updatedPassword: String) {
        _uiState.value = _uiState.value.copy(password = updatedPassword, passwordError = false)
    }

    fun updateLoginError(hasError: Boolean) {
        _uiState.value = _uiState.value.copy(loginError = hasError)
    }

    fun updateEmailError(hasError: Boolean) {
        _uiState.value = _uiState.value.copy(emailError = hasError)
    }

    fun updatePasswordError(hasError: Boolean) {
        _uiState.value = _uiState.value.copy(passwordError = hasError)
    }

    fun updateEmailVerificationError(hasError: Boolean) {
        _uiState.value = _uiState.value.copy(emailVerificationError = hasError)
    }

    fun resetAllFields() {
        _uiState.value = LoginUiState()
    }

    fun login() {
        val state = _uiState.value
        val emailError = !validator.isEmailValid(state.email)
        val passwordError = !validator.isPasswordValid(state.password)
        updateEmailError(emailError)
        updatePasswordError(passwordError)
        if (emailError || passwordError) {
            return
        }

        viewModelScope.launch {
            try {
                authRepository.login(state.email, state.password)
                if (authRepository.isEmailVerified()) {
                    resetAllFields()
                    updateEmailVerificationError(false)
                } else {
                    try {
                        authRepository.sendEmailVerification()
                        Log.d("LoginViewModel", "Email de vérification renvoyé automatiquement.")
                    } catch (e: Exception) {
                        Log.w("LoginViewModel", "Impossible de renvoyer l'email (peut-être trop tôt ?) : ${e.message}")
                    }
                    authRepository.logout()
                    updateEmailVerificationError(true)
                    Log.e("LoginViewModel", "Email non vérifié")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login failed: ${e.message}")
                updateLoginError(true)
            }
        }
    }
}