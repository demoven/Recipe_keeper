package com.example.recipekeeper.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipekeeper.data.repository.IAuthRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authRepository: IAuthRepository
) : ViewModel() {

    fun logout() {
        authRepository.logout()
    }

    fun deleteAccount(currentPassword: String) {
        viewModelScope.launch {
            authRepository.deleteAccount(currentPassword)
        }
    }

}