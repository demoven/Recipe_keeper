package com.example.recipekeeper.ui.screens.settings

import androidx.annotation.StringRes

data class SettingsUiState(
    val email: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val showPasswordDialogEmail: Boolean = false,
    val showPasswordDialogSecurity: Boolean = false,
    val showPasswordDialogDeletion: Boolean = false,
    val isLoading: Boolean = false,
    // Error fields
    val emailError: Boolean = false,
    val newPasswordError: Boolean = false,
    val currentPasswordError: Boolean = false,
    @StringRes val passwordDialogMessageResId: Int? = null,
    val emailUpdateError: Boolean = false,
    val emailAlreadyExists: Boolean = false,
    val passwordUpdateError: Boolean = false,
    val deletionError: Boolean = false,
    // Success fields
    val emailUpdateSuccess: Boolean = false,
    val passwordUpdateSuccess: Boolean = false,
)
