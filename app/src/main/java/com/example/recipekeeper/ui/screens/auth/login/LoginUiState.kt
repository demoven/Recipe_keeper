package com.example.recipekeeper.ui.screens.auth.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    // Error fields
    val loginError: Boolean = false,
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
    val emailVerificationError: Boolean = false,
    val isResetPasswordDialogVisible: Boolean = false,
    val isResetPasswordEmailSent: Boolean = false,
    val resetPasswordError: Boolean = false,
)
