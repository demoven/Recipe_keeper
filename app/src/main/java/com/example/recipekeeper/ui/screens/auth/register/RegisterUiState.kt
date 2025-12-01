package com.example.recipekeeper.ui.screens.auth.register

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    // Error fields
    val registerError: Boolean = false,
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
    val confirmPasswordError: Boolean = false,
    val verificationEmailSent: Boolean = false,
)
