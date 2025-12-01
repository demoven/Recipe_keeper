package com.example.recipekeeper.tools

import android.util.Patterns

private const val MIN_PASSWORD_LENGTH = 12

class AuthValidator {
    fun isEmailValid(email: String?): Boolean = !email.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isPasswordValid(password: String): Boolean {
        val lengthOk = password.length >= MIN_PASSWORD_LENGTH
        val upperOk = password.any { it.isUpperCase() }
        val lowerOk = password.any { it.isLowerCase() }
        val specialOk = password.any { !it.isLetterOrDigit() && !it.isWhitespace() }
        return lengthOk && upperOk && lowerOk && specialOk
    }

    fun isConfirmPasswordValid(
        password: String,
        confirmPassword: String,
    ): Boolean = confirmPassword.isNotBlank() && confirmPassword == password
}
