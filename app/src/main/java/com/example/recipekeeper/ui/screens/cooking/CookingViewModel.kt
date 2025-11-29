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

data class CookingUiState(
    val recipe: Recipe? = null,
    val currentStep: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

class CookingViewModel(private val recipeRepository: IRecipeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CookingUiState())
    val uiState: StateFlow<CookingUiState> = _uiState.asStateFlow()

    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            recipeRepository.getRecipeById(recipeId) { recipe ->
                if (recipe != null && recipe.instructions.isNotEmpty()) {
                    _uiState.update {
                        it.copy(recipe = recipe, isLoading = false)
                    }
                } else {
                    _uiState.update {
                        it.copy(error = "Recette non trouvée ou sans instructions.", isLoading = false)
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
}