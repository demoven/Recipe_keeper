package com.example.recipekeeper.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.example.recipekeeper.data.repository.IAuthRepository

class SettingsViewModel(
    private val authRepository: IAuthRepository
) : ViewModel() {

    fun logout() {
        authRepository.logout()
    }

}