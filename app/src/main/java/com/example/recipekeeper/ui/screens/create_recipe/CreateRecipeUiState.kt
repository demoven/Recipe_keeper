package com.example.recipekeeper.ui.screens.create_recipe

data class CreateRecipeUiState (
    val title: String = "",
    val description: String = "",
    val ingredients: List<String> = listOf(""),
    val instructions: List<String> = listOf(""),
    val prepTime: Int = 0, // in minutes
    val cookTime: Int = 0, // in minutes
    val servings: Int = 0,
    // Error fields
)