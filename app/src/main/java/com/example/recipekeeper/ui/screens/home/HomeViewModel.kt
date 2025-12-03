package com.example.recipekeeper.ui.screens.home

import androidx.lifecycle.ViewModel
import com.example.recipekeeper.data.repository.IFolderRepository
import com.example.recipekeeper.data.repository.IRecipeRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(
    private val folderRepository: IFolderRepository,
    private val recipeRepository: IRecipeRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var foldersListener: ListenerRegistration? = null
    private var recipesListener: ListenerRegistration? = null

    init {
        loadData(null)
    }

    fun updateIsLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    fun loadData(folderId: String?) {
        updateIsLoading(true)
        foldersListener?.remove()
        recipesListener?.remove()

        foldersListener =
            folderRepository.watchFolder(folderId) { folders ->
                val sortedFolders = folders.sortedBy { it.name.lowercase() }
                _uiState.value = _uiState.value.copy(folders = sortedFolders)
                updateIsLoading(false)
            }
        recipesListener =
            recipeRepository.watchRecipesInFolder(folderId) { recipes ->
                val sortedRecipes = recipes.sortedBy { it.title.lowercase() }
                _uiState.value = _uiState.value.copy(recipes = sortedRecipes)
                updateIsLoading(false)
            }
    }

    override fun onCleared() {
        super.onCleared()
        foldersListener?.remove()
        recipesListener?.remove()
    }
}
