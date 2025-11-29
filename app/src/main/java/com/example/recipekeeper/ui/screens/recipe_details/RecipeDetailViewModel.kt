package com.example.recipekeeper.ui.screens.recipe_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipekeeper.data.models.Recipe
import com.example.recipekeeper.data.repository.IRecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class RecipeDetailViewModel(private val recipeRepository: IRecipeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            recipeRepository.getRecipeById(recipeId) { recipe ->
                if (recipe != null) {
                    _uiState.value = RecipeDetailUiState(recipe = recipe, isLoading = false)
                } else {
                    _uiState.value = RecipeDetailUiState(error = "Recette non trouvée", isLoading = false)
                }
            }
        }
    }
}