package com.example.recipekeeper.ui.screens.settings

data class SettingsUiState(
    val email: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val showPasswordDialogEmail: Boolean = false,
    val showPasswordDialogSecurity: Boolean = false,
    val showPasswordDialogDeletion: Boolean = false,

    // Error fields
    val emailError: Boolean = false,
    val newPasswordError: Boolean = false,
    val currentPasswordError: Boolean = false
)
