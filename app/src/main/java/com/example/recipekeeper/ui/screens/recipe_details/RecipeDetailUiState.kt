package com.example.recipekeeper.ui.screens.recipe_details

import com.example.recipekeeper.data.models.Recipe

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = true,
    val error: Boolean = false,
)
