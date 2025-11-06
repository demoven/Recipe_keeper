package com.example.recipekeeper.tools

import android.util.Patterns

class AuthValidator {

    fun isEmailValid(email: String?): Boolean {
        return !email.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        val lengthOk = password.length >= 12
        val upperOk = password.any { it.isUpperCase() }
        val lowerOk = password.any { it.isLowerCase() }
        val specialOk = password.any { !it.isLetterOrDigit() && !it.isWhitespace() }
        return lengthOk && upperOk && lowerOk && specialOk
    }

    fun isConfirmPasswordValid(password: String, confirmPassword: String): Boolean {
        return confirmPassword.isNotBlank() && confirmPassword == password
    }
}