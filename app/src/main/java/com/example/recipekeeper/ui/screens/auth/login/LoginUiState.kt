package com.example.recipekeeper.ui.screens.auth.login

data class LoginUiState (
    val email: String = "",
    val password: String = "",
    // Error fields
    val loginError: Boolean = false,
    val emailError: Boolean = false,
    val passwordError: Boolean = false
)