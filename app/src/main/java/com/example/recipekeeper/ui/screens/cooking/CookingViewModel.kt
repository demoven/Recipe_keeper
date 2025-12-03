package com.example.recipekeeper.ui.screens.cooking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipekeeper.data.models.Recipe
import com.example.recipekeeper.data.repository.IRecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CookingViewModel(
    private val recipeRepository: IRecipeRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CookingUiState())
    val uiState: StateFlow<CookingUiState> = _uiState.asStateFlow()

    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            recipeRepository.getRecipeById(recipeId) { recipe ->
                if (recipe != null && recipe.instructions.isNotEmpty()) {
                    _uiState.update {
                        it.copy(recipe = recipe, isLoading = false, error = false)
                    }
                } else {
                    _uiState.update {
                        it.copy(error = true, isLoading = false)
                    }
                }
            }
        }
    }

    fun nextStep() {
        _uiState.value.recipe?.let { recipe ->
            if (_uiState.value.currentStep < recipe.instructions.lastIndex) {
                _uiState.update { it.copy(currentStep = it.currentStep + 1) }
            }
        }
    }

    fun previousStep() {
        if (_uiState.value.currentStep > 0) {
            _uiState.update { it.copy(currentStep = it.currentStep - 1) }
        }
    }

    fun openIngredientsDialog() {
        _uiState.update { it.copy(showIngredientsDialog = true) }
    }

    fun closeIngredientsDialog() {
        _uiState.update { it.copy(showIngredientsDialog = false) }
    }

    fun openVoiceInstructions() {
        _uiState.update { it.copy(showVoiceInstructions = true) }
    }

    fun closeVoiceInstructions() {
        _uiState.update { it.copy(showVoiceInstructions = false) }
    }
}
