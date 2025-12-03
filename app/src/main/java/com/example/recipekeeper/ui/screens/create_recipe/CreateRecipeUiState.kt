package com.example.recipekeeper.ui.screens.create_recipe

data class CreateRecipeUiState(
    val title: String = "",
    val description: String = "",
    val ingredients: List<String> = listOf(""),
    val instructions: List<String> = listOf(""),
    val prepTime: String = "", // in minutes
    val cookTime: String = "", // in minutes
    val servings: String = "",
)
