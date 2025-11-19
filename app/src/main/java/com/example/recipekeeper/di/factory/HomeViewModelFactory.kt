package com.example.recipekeeper.di.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipekeeper.data.repository.IAuthRepository
import com.example.recipekeeper.data.repository.IFolderRepository
import com.example.recipekeeper.data.repository.IRecipeRepository
import com.example.recipekeeper.ui.screens.home.HomeViewModel

class HomeViewModelFactory(
    private val folderRepository: IFolderRepository,
    private val recipeRepository: IRecipeRepository,
    private val authRepository: IAuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                folderRepository = folderRepository,
                recipeRepository = recipeRepository,
                authRepository = authRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}