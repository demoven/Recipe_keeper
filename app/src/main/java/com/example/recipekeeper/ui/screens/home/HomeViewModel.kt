package com.example.recipekeeper.ui.screens.home

import androidx.lifecycle.ViewModel
import com.example.recipekeeper.data.models.Recipe
import com.example.recipekeeper.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun getRecipes(folderId: String?) {
        return repository.getRecipesInFolder(folderId)
    }

    fun getFolders(parentId: String?) {
        repository.getFolders(parentId){
            folders ->
            _uiState.value = _uiState.value.copy(folders = folders)
        }
    }
}