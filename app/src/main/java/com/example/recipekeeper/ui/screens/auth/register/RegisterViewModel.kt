package com.example.recipekeeper.ui.screens.auth.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipekeeper.data.repository.IAuthRepository
import com.example.recipekeeper.tools.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: IAuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()
    private val validator = AuthValidator()

    fun updateEmail(updatedEmail: String) {
        _uiState.value = _uiState.value.copy(emailError = false, email = updatedEmail)
    }

    fun updatePassword(updatedPassword: String) {
        _uiState.value = _uiState.value.copy(passwordError = false, password = updatedPassword)
    }

    fun updateConfirmPassword(updatedConfirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPasswordError = false, confirmPassword = updatedConfirmPassword)
    }

    fun updateRegisterError(hasError: Boolean) {
        _uiState.value = _uiState.value.copy(registerError = hasError)
    }

    fun updateEmailError(hasError: Boolean) {
        _uiState.value = _uiState.value.copy(emailError = hasError)
    }

    fun updatePasswordError(hasError: Boolean) {
        _uiState.value = _uiState.value.copy(passwordError = hasError)
    }

    fun updateConfirmPasswordError(hasError: Boolean) {
        _uiState.value = _uiState.value.copy(confirmPasswordError = hasError)
    }

    fun resetAllFields() {
        _uiState.value = RegisterUiState()
    }

    fun register() {
        val state = _uiState.value
        val emailError = !validator.isEmailValid(state.email)
        val passwordError = !validator.isPasswordValid(state.password)
        val confirmPasswordError = !validator.isConfirmPasswordValid(state.password, state.confirmPassword)
        updateEmailError(emailError)
        updatePasswordError(passwordError)
        updateConfirmPasswordError(confirmPasswordError)
        if (emailError || passwordError || confirmPasswordError) {
            return
        }

        viewModelScope.launch {
            try {
                authRepository.register(state.email, state.password)
                authRepository.sendEmailVerification()
                resetAllFields()
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Registration failed", e)
                updateRegisterError(true)
            }
        }
    }
}