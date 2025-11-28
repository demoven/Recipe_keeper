package com.example.recipekeeper.di.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipekeeper.data.repository.IRecipeRepository
import com.example.recipekeeper.ui.screens.recipe.CookingViewModel

class CookingViewModelFactory(
    private val recipeRepository: IRecipeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CookingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CookingViewModel(recipeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}