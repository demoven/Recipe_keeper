package com.example.recipekeeper.ui.screens.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipekeeper.data.repository.IAuthRepository
import com.example.recipekeeper.tools.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: IAuthRepository,
) : ViewModel() {
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

    fun updateIsLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
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
        updateIsLoading(true)

        viewModelScope.launch {
            try {
                authRepository.login(state.email, state.password)
                if (authRepository.isEmailVerified()) {
                    resetAllFields()
                    updateEmailVerificationError(false)
                    updateIsLoading(false)
                } else {
                    try {
                        authRepository.sendEmailVerification()
                    } catch (e: Exception) {
                        // TODO handle exception properly
                    }
                    updateIsLoading(false)
                    authRepository.logout()
                    updateEmailVerificationError(true)
                }
            } catch (e: Exception) {
                // TODO handle exception properly
                updateLoginError(true)
            }
        }
    }

    fun showResetPasswordDialog() {
        _uiState.value = _uiState.value.copy(isResetPasswordDialogVisible = true)
    }

    fun hideResetPasswordDialog() {
        _uiState.value =
            _uiState.value.copy(
                isResetPasswordDialogVisible = false,
                resetPasswordError = false,
                isResetPasswordEmailSent = false,
            )
    }

    fun sendPasswordResetEmail(emailForReset: String) {
        if (!validator.isEmailValid(emailForReset)) {
            _uiState.value = _uiState.value.copy(resetPasswordError = true)
            return
        }

        viewModelScope.launch {
            try {
                authRepository.sendPasswordResetEmail(emailForReset)
                _uiState.value =
                    _uiState.value.copy(
                        isResetPasswordEmailSent = true,
                        resetPasswordError = false,
                    )
            } catch (e: Exception) {
                // TODO handle exception properly
            }
        }
    }
}
