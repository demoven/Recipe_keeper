package com.example.recipekeeper.di.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipekeeper.RecipeKeeperViewModel
import com.example.recipekeeper.data.repository.IAuthRepository
import com.example.recipekeeper.data.repository.IFolderRepository

class RecipeKeeperViewModelFactory(
    private val authRepository: IAuthRepository,
    private val folderRepository: IFolderRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeKeeperViewModel::class.java)) {
            return RecipeKeeperViewModel(
                authRepository = authRepository,
                folderRepository = folderRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}