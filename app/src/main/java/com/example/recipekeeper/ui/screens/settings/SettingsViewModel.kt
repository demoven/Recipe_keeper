package com.example.recipekeeper.ui.screens.settings
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

    fun updateEmail(updatedEmail: String) {
        _uiState.value = _uiState.value.copy(email = updatedEmail, emailError = false)
    }

    fun updateCurrentPassword(updatedPassword: String) {
        _uiState.value = _uiState.value.copy(currentPassword = updatedPassword)
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

    fun updateEmail(onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            authRepository.updateEmail(uiState.value.email, uiState.value.currentPassword, onSuccess, onFailure)
        }
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