package com.example.recipekeeper.di.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipekeeper.data.repository.IAuthRepository
import com.example.recipekeeper.ui.screens.auth.login.LoginViewModel
import com.example.recipekeeper.ui.screens.auth.register.RegisterViewModel

class AuthViewModelFactory(
    private val authRepository: IAuthRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                authRepository = authRepository,
            ) as T
        }
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(
                authRepository = authRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
