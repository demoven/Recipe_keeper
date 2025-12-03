package com.example.recipekeeper.ui.screens.create_recipe

import androidx.lifecycle.ViewModel
import com.example.recipekeeper.data.models.Recipe
import com.example.recipekeeper.data.repository.IRecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CreateRecipeViewModel(
    private val recipeRepository: IRecipeRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateRecipeUiState())
    val uiState: StateFlow<CreateRecipeUiState> = _uiState.asStateFlow()

    fun updateTitle(updatedTitle: String) {
        _uiState.value = _uiState.value.copy(title = updatedTitle)
    }

    fun updateDescription(updatedDescription: String) {
        _uiState.value = _uiState.value.copy(description = updatedDescription)
    }

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
            } else {
                current
            }
        }
    }

    fun updatePrepTime(value: String) {
        if (value.all { it.isDigit() } || value.isEmpty()) {
            _uiState.update { it.copy(prepTime = value) }
        }
    }

    fun updateCookTime(value: String) {
        if (value.all { it.isDigit() } || value.isEmpty()) {
            _uiState.update { it.copy(cookTime = value) }
        }
    }

    fun updateServings(value: String) {
        if (value.all { it.isDigit() } || value.isEmpty()) {
            _uiState.update { it.copy(servings = value) }
        }
    }

    fun updateIngredient(
        index: Int,
        newValue: String,
    ) {
        _uiState.update { current ->
            if (index in current.ingredients.indices) {
                val newList = current.ingredients.toMutableList()
                newList[index] = newValue
                current.copy(ingredients = newList)
            } else {
                current
            }
        }
    }

    fun resetState() {
        _uiState.value = CreateRecipeUiState()
    }

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
            } else {
                current
            }
        }
    }

    fun updateStep(
        index: Int,
        newValue: String,
    ) {
        _uiState.update { current ->
            if (index in current.instructions.indices) {
                val newList = current.instructions.toMutableList()
                newList[index] = newValue
                current.copy(instructions = newList)
            } else {
                current
            }
        }
    }

    fun updateIsLoading(isLoading: Boolean) {
        _uiState.update { current ->
            current.copy(isLoading = isLoading)
        }
    }

    fun getRecipeById(recipeId: String) {
        if (_uiState.value.isLoading) return
        updateIsLoading(true)
        recipeRepository.getRecipeById(recipeId) { recipe ->
            if (recipe != null) {
                _uiState.value =
                    CreateRecipeUiState(
                        title = recipe.title,
                        description = recipe.description,
                        prepTime = recipe.prepTime.toString(),
                        cookTime = recipe.cookTime.toString(),
                        servings = recipe.servings.toString(),
                        ingredients = recipe.ingredients,
                        instructions = recipe.instructions,
                    )
            }
            updateIsLoading(false)
        }
    }

    fun saveRecipe(
        folderId: String?,
        onSuccess: (String, String) -> Unit,
        onFailure: () -> Unit,
    ) {
        if (_uiState.value.isLoading) return
        if (_uiState.value.title.isBlank() || _uiState.value.ingredients.isEmpty()) {
            onFailure()
            return
        }
        _uiState.update { current ->
            current.copy(
                ingredients = current.ingredients.filter { it.isNotBlank() },
                instructions = current.instructions.filter { it.isNotBlank() },
            )
        }
        updateIsLoading(true)
        val state = _uiState.value
        val newRecipe =
            Recipe(
                id = "",
                title = state.title,
                description = state.description,
                prepTime = state.prepTime.toIntOrNull() ?: 0,
                cookTime = state.cookTime.toIntOrNull() ?: 0,
                servings = state.servings.toIntOrNull() ?: 0,
                ingredients = state.ingredients,
                instructions = state.instructions,
                folderId = folderId,
            )
        recipeRepository.saveRecipe(
            newRecipe,
            onSuccess = { recipeId, recipeTitle ->
                onSuccess(recipeId, recipeTitle)
                _uiState.value = CreateRecipeUiState() // Reset state after saving
                updateIsLoading(false)
            },
            onFailure = {
                onFailure()
                updateIsLoading(false)
            },
        )
    }

    fun updateRecipe(
        recipeId: String,
        onSuccess: (String, String) -> Unit,
    ) {
        updateIsLoading(true)
        recipeRepository.updateRecipe(
            recipe =
                Recipe(
                    id = recipeId,
                    title = _uiState.value.title,
                    description = _uiState.value.description,
                    ingredients = _uiState.value.ingredients,
                    instructions = _uiState.value.instructions,
                    prepTime = _uiState.value.prepTime.toIntOrNull() ?: 0,
                    cookTime = _uiState.value.cookTime.toIntOrNull() ?: 0,
                    servings = _uiState.value.servings.toIntOrNull() ?: 0,
                ),
            onSuccess = { recipeId, recipeTitle ->
                onSuccess(recipeId, recipeTitle)
                updateIsLoading(false)
            },
            onFailure = {
                updateIsLoading(false)
            },
        )
    }
}
