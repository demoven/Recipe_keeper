package com.example.recipekeeper.ui.auth

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,
    val email: String = "",
    // Fields errors
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)
