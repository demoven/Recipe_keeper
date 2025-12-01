package com.example.recipekeeper.di.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recipekeeper.data.repository.IRecipeRepository
import com.example.recipekeeper.ui.screens.create_recipe.CreateRecipeViewModel

class CreateRecipeViewModelFactory(
    private val recipeRepository: IRecipeRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateRecipeViewModel::class.java)) {
            return CreateRecipeViewModel(
                recipeRepository = recipeRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
