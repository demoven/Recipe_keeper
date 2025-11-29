package com.example.recipekeeper.di.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipekeeper.data.repository.IAuthRepository
import com.example.recipekeeper.data.repository.IFolderRepository
import com.example.recipekeeper.data.repository.IRecipeRepository
import com.example.recipekeeper.ui.screens.settings.SettingsViewModel

class SettingsViewModelFactory(
    private val authRepository: IAuthRepository,
    private val recipeRepository: IRecipeRepository,
    private val folderRepository: IFolderRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(
                authRepository = authRepository,
                recipeRepository = recipeRepository,
                folderRepository = folderRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}