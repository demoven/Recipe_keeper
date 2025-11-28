package com.example.recipekeeper.ui.screens.recipe

import androidx.lifecycle.ViewModel
import com.example.recipekeeper.data.models.Recipe
import com.example.recipekeeper.data.repository.IRecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CreateRecipeViewModel(
    private val recipeRepository: IRecipeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateRecipeUiState())
    val uiState: StateFlow<CreateRecipeUiState> = _uiState.asStateFlow()

    fun updateTitle(updatedTitle: String) {
        _uiState.value = _uiState.value.copy(title = updatedTitle)
    }

    fun updateDescription(updatedDescription: String) {
        _uiState.value = _uiState.value.copy(description = updatedDescription)
    }

    fun updatePreparationTime(updatedTime: Int) {
        _uiState.value = _uiState.value.copy(prepTime = updatedTime)
    }

    fun updateCookingTime(updatedTime: Int) {
        _uiState.value = _uiState.value.copy(cookTime = updatedTime)
    }

    fun updateServings(updatedServings: Int) {
        _uiState.value = _uiState.value.copy(servings = updatedServings)
    }

    // --- Gestion des Ingrédients ---
    fun addIngredient() {
        _uiState.update { current ->
            current.copy(ingredients = current.ingredients + "")
        }
    }

    fun removeIngredient(index: Int) {
        _uiState.update { current ->
            if (index in current.ingredients.indices) {
                val newList = current.ingredients.toMutableList()
                newList.removeAt(index)
                current.copy(ingredients = newList)
            } else current
        }
    }

    fun updatePrepTime(value: String) {
        val numericValue = value.toIntOrNull() ?: 0
        _uiState.update { it.copy(prepTime = numericValue) }
    }

    fun updateIngredient(index: Int, newValue: String) {
        _uiState.update { current ->
            if (index in current.ingredients.indices) {
                val newList = current.ingredients.toMutableList()
                newList[index] = newValue
                current.copy(ingredients = newList)
            } else current
        }
    }

    // --- Gestion des Étapes (Instructions) ---
    fun addStep() {
        _uiState.update { current ->
            current.copy(instructions = current.instructions + "")
        }
    }

    fun addStepIfEmpty() {
        if (_uiState.value.instructions.isEmpty()) {
            addStep()
        }
    }

    fun removeStep(index: Int) {
        _uiState.update { current ->
            if (index in current.instructions.indices) {
                val newList = current.instructions.toMutableList()
                newList.removeAt(index)
                current.copy(instructions = newList)
            } else current
        }
    }

    fun updateStep(index: Int, newValue: String) {
        _uiState.update { current ->
            if (index in current.instructions.indices) {
                val newList = current.instructions.toMutableList()
                newList[index] = newValue
                current.copy(instructions = newList)
            } else current
        }
    }

    fun saveRecipe(folderId: String?, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val state = _uiState.value
        val newRecipe = Recipe(
            id = "",
            title = state.title,
            description = state.description,
            prepTime = state.prepTime,
            cookTime = state.cookTime,
            servings = state.servings,
            ingredients = state.ingredients,
            instructions = state.instructions,
            folderId = folderId
        )
        recipeRepository.saveRecipe(newRecipe,
            onSuccess = {
                onSuccess()
                _uiState.value = CreateRecipeUiState() // Reset state after saving
            },
            onFailure = {
                onFailure
            }
        )
    }
}