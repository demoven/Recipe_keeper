package com.example.recipekeeper.ui.screens.settings
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipekeeper.R
import com.example.recipekeeper.data.repository.IAuthRepository
import com.example.recipekeeper.data.repository.IFolderRepository
import com.example.recipekeeper.data.repository.IRecipeRepository
import com.example.recipekeeper.tools.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authRepository: IAuthRepository,
    private val recipeRepository: IRecipeRepository,
    private val folderRepository: IFolderRepository,
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
        _uiState.value =
            _uiState.value.copy(
                currentPassword = updatedPassword,
                currentPasswordError = false,
                passwordDialogMessageResId = null,
            )
    }

    fun updateNewPassword(updatedPassword: String) {
        _uiState.value = _uiState.value.copy(newPassword = updatedPassword, newPasswordError = false)
    }

    fun updateShowPasswordDialogEmail(show: Boolean) {
        _uiState.value = _uiState.value.copy(showPasswordDialogEmail = show, passwordDialogMessageResId = null)
    }

    fun updateShowPasswordDialogDeletion(show: Boolean) {
        _uiState.value = _uiState.value.copy(showPasswordDialogDeletion = show, passwordDialogMessageResId = null)
    }

    fun updateShowPasswordDialogSecurity(show: Boolean) {
        _uiState.value = _uiState.value.copy(showPasswordDialogSecurity = show, passwordDialogMessageResId = null)
    }

    fun updatePasswordDialogMessage(
        @StringRes messageResId: Int?,
    ) {
        _uiState.value = _uiState.value.copy(passwordDialogMessageResId = messageResId)
    }

    fun clearSuccessFlags() {
        _uiState.value =
            _uiState.value.copy(
                emailUpdateSuccess = false,
                passwordUpdateSuccess = false,
            )
    }

    fun updateIsLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    fun onDismissDialog() {
        _uiState.value =
            _uiState.value.copy(
                showPasswordDialogEmail = false,
                showPasswordDialogDeletion = false,
                showPasswordDialogSecurity = false,
                currentPassword = "",
                newPassword = "",
                passwordDialogMessageResId = null,
            )
    }

    fun isEmailValid(): Boolean {
        if (validator.isEmailValid(uiState.value.email)) {
            _uiState.value = _uiState.value.copy(emailError = false)
            return true
        } else {
            _uiState.value = _uiState.value.copy(emailError = true)
            return false
        }
    }

    fun isNewPasswordValid(): Boolean {
        if (validator.isPasswordValid(uiState.value.newPassword)) {
            _uiState.value = _uiState.value.copy(newPasswordError = false)
            return true
        } else {
            _uiState.value = _uiState.value.copy(newPasswordError = true)
            return false
        }
    }

    fun isCurrentPasswordValid(): Boolean {
        if (uiState.value.currentPassword.isNotBlank()) {
            _uiState.value = _uiState.value.copy(currentPasswordError = false)
            return true
        } else {
            _uiState.value = _uiState.value.copy(currentPasswordError = true)
            return false
        }
    }

    fun updateUserEmail() {
        if (!isCurrentPasswordValid()) {
            updatePasswordDialogMessage(R.string.required_field)
            return
        }
        val userEmail = authRepository.getCurrentUser()?.email
        if (uiState.value.email == userEmail) {
            updatePasswordDialogMessage(R.string.current_email_error)
            return
        } else {
            updateIsLoading(true)
            viewModelScope.launch {
                try {
                    authRepository.updateEmail(
                        currentPassword = uiState.value.currentPassword,
                        newEmail = uiState.value.email,
                        onSuccess = {
                            updateIsLoading(false)
                            _uiState.value = _uiState.value.copy(emailUpdateSuccess = true)
                            onDismissDialog()
                        },
                        onFailure = {
                            updateIsLoading(false)
                            updatePasswordDialogMessage(R.string.email_update_error)
                        },
                    )
                } catch (_: Exception) {
                    updateIsLoading(false)
                    updatePasswordDialogMessage(R.string.confirmation_error)
                }
            }
        }
    }

    fun updateUserPassword() {
        if (!isCurrentPasswordValid()) {
            updatePasswordDialogMessage(R.string.required_field)
            return
        }
        updateIsLoading(true)
        viewModelScope.launch {
            try {
                authRepository.updatePassword(
                    currentPassword = uiState.value.currentPassword,
                    newPassword = uiState.value.newPassword,
                    onSuccess = {
                        updateIsLoading(false)
                        _uiState.value = _uiState.value.copy(passwordUpdateSuccess = true)
                        onDismissDialog()
                    },
                    onFailure = {
                        updateIsLoading(false)
                        updatePasswordDialogMessage(R.string.invalid_password)
                    },
                )
            } catch (_: Exception) {
                updateIsLoading(false)
                updatePasswordDialogMessage(R.string.invalid_password)
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }

    fun deleteAccount() {
        if (!isCurrentPasswordValid()) {
            updatePasswordDialogMessage(R.string.required_field)
            return
        }
        updateIsLoading(true)
        viewModelScope.launch {
            try {
                recipeRepository.deleteAllRecipes()
                folderRepository.deleteAllFolders()
                authRepository.deleteAccount(uiState.value.currentPassword)
                updateIsLoading(false)
            } catch (_: Exception) {
                updateIsLoading(false)
                updatePasswordDialogMessage(R.string.account_suppression_error)
            }
        }
    }
}
