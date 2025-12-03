package com.example.recipekeeper.ui.screens.cooking

import com.example.recipekeeper.data.models.Recipe

data class CookingUiState(
    val recipe: Recipe? = null,
    val currentStep: Int = 0,
    val isLoading: Boolean = true,
    val showIngredientsDialog: Boolean = false,
    val showVoiceInstructions: Boolean = false,
    val error: Boolean = false,
)
