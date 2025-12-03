package com.example.recipekeeper.ui.screens.home

import com.example.recipekeeper.data.models.Folder
import com.example.recipekeeper.data.models.Recipe

data class HomeUiState(
    val recipes: List<Recipe> = emptyList(),
    val folders: List<Folder> = emptyList(),
    val selectedFolder: String? = null,
    val isLoading: Boolean = true,
)
