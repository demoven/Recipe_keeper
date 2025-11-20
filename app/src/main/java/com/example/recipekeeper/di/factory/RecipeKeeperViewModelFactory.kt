package com.example.recipekeeper.di.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipekeeper.RecipeKeeperViewModel
import com.example.recipekeeper.data.repository.IAuthRepository

class RecipeKeeperViewModelFactory(
    private val authRepository: IAuthRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeKeeperViewModel::class.java)) {
            return RecipeKeeperViewModel(
                authRepository = authRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}