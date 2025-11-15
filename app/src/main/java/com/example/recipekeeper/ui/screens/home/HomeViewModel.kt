package com.example.recipekeeper.ui.screens.home

import androidx.lifecycle.ViewModel
import com.example.recipekeeper.data.repository.RecipeRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    private var recipesListener: ListenerRegistration? = null
    private var foldersListener: ListenerRegistration? = null
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun watchRecipes(folderId: String?) {
        recipesListener?.remove()
        recipesListener = repository.watchRecipesInFolder(folderId) { recipes ->
            _uiState.value = _uiState.value.copy(recipes = recipes)
        }
    }

    fun watchFolders(parentId: String?) {
        foldersListener?.remove()
        foldersListener = repository.watchFolders(parentId) { folders ->
            _uiState.value = _uiState.value.copy(folders = folders)
        }
    }

    override fun onCleared() {
        recipesListener?.remove()
        foldersListener?.remove()
        super.onCleared()
    }
}