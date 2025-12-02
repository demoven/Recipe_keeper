package com.example.recipekeeper.ui.screens.recipe_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipekeeper.data.repository.IRecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    private val recipeRepository: IRecipeRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            recipeRepository.getRecipeById(recipeId) { recipe ->
                if (recipe != null) {
                    _uiState.value = RecipeDetailUiState(recipe = recipe, isLoading = false, error = false)
                } else {
                    _uiState.value = RecipeDetailUiState(error = true, isLoading = false)
                }
            }
        }
    }
}
