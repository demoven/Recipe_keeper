package com.example.recipekeeper.ui.screens.home

import androidx.lifecycle.ViewModel
import com.example.recipekeeper.data.repository.RecipeRepository

class HomeViewModel(private val repository: RecipeRepository) : ViewModel() {

    fun getRecipes(folderId: String?) {
        return repository.getRecipesInFolder(folderId)
    }
}