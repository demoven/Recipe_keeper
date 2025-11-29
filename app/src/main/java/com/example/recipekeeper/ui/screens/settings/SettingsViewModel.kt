package com.example.recipekeeper.ui.screens.settings
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipekeeper.data.repository.IAuthRepository
import com.example.recipekeeper.tools.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val validator = AuthValidator()
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val currentUserEmail = authRepository.getCurrentUser()?.email ?: ""
            _uiState.value = _uiState.value.copy(email = currentUserEmail)
        }
    }

    fun updateEmail(updatedEmail: String) {
        _uiState.value = _uiState.value.copy(email = updatedEmail, emailError = false)
    }

    fun updateCurrentPassword(updatedPassword: String) {
        _uiState.value = _uiState.value.copy(currentPassword = updatedPassword, currentPasswordError = false)
    }

    fun updateNewPassword(updatedPassword: String) {
        _uiState.value = _uiState.value.copy(newPassword = updatedPassword, newPasswordError = false)
    }

    fun updateShowPasswordDialogEmail(show: Boolean) {
        _uiState.value = _uiState.value.copy(showPasswordDialogEmail = show)
    }

    fun updateShowPasswordDialogDeletion(show: Boolean) {
        _uiState.value = _uiState.value.copy(showPasswordDialogDeletion = show)
    }

    fun updateShowPasswordDialogSecurity(show: Boolean) {
        _uiState.value = _uiState.value.copy(showPasswordDialogSecurity = show)
    }

    fun updateEmailAlreadyExists(exists: Boolean) {
        _uiState.value = _uiState.value.copy(emailAlreadyExists = exists)
    }

    fun updateEmailUpdateSuccess(isSuccess: Boolean) {
        _uiState.value = _uiState.value.copy(emailUpdateSuccess = isSuccess)
    }

    fun isEmailValid(): Boolean  {
        if(validator.isEmailValid(uiState.value.email)) {
            _uiState.value = _uiState.value.copy(emailError = false)
            return true
        } else {
            _uiState.value = _uiState.value.copy(emailError = true)
            return false
        }
    }

    fun isNewPasswordValid(): Boolean {
        if(validator.isPasswordValid(uiState.value.newPassword)) {
            _uiState.value = _uiState.value.copy(newPasswordError = false)
            return true
        } else {
            _uiState.value = _uiState.value.copy(newPasswordError = true)
            return false
        }
    }

    fun isCurrentPasswordValid(): Boolean {
        if(uiState.value.currentPassword.isNotBlank()) {
            _uiState.value = _uiState.value.copy(currentPasswordError = false)
            return true
        } else {
            _uiState.value = _uiState.value.copy(currentPasswordError = true)
            return false
        }
    }

    fun updateUserEmail() {
        val userEmail = authRepository.getCurrentUser()?.email
        if (uiState.value.email == userEmail) {
            Log.d("SettingsViewModel", "L'email ne s'est pas modifié.")
            updateEmailAlreadyExists(true)
            return
        } else {
            viewModelScope.launch {
                try {
                    authRepository.updateEmail(
                        currentPassword = uiState.value.currentPassword,
                        newEmail = uiState.value.email,
                        onSuccess = {
                            updateEmailUpdateSuccess(true)
                            logout()
                        },
                        onFailure = {
                            updateEmailUpdateError(true)
                        }
                    )
                } catch (e: Exception) {
                    updateEmailUpdateError(true)
                }
            }
        }
    }

    fun updateEmailUpdateError(hasError: Boolean) {
        _uiState.value = _uiState.value.copy(emailUpdateError = hasError)
    }

    fun updatePasswordUpdateError(hasError: Boolean) {
        _uiState.value = _uiState.value.copy(passwordUpdateError = hasError)
    }

    fun logout() {
        authRepository.logout()
    }

    fun deleteAccount(currentPassword: String) {
        viewModelScope.launch {
            authRepository.deleteAccount(currentPassword)
        }
    }

}