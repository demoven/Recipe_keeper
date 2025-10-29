package com.example.recipekeeper.ui.auth

data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val email: String = "",
    // Fields errors
    val loginError: Boolean = false,
    val registerError: Boolean = false,
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
    val confirmPasswordError: Boolean = false
)
