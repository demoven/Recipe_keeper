package com.example.recipekeeper.data.models

data class UserRecipes (
    val userId: String = "",
    val recipes: List<Recipe> = emptyList(),
    val folders: List<Folder> = emptyList()
)