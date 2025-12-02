package com.example.recipekeeper.di.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipekeeper.data.repository.IRecipeRepository
import com.example.recipekeeper.ui.screens.recipe_details.RecipeDetailViewModel

class RecipeDetailViewModelFactory(
    private val recipeRepository: IRecipeRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeDetailViewModel(
                recipeRepository = recipeRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
