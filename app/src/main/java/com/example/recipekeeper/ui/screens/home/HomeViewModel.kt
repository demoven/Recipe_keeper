package com.example.recipekeeper.ui.screens.home

import androidx.lifecycle.ViewModel
import com.example.recipekeeper.data.repository.IAuthRepository
import com.example.recipekeeper.data.repository.IFolderRepository
import com.example.recipekeeper.data.repository.IRecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(
    private val folderRepository: IFolderRepository,
    private val recipeRepository: IRecipeRepository,
    authRepository: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val currentUserId = authRepository.getCurrentUserId()
        if (currentUserId != null) {
            folderRepository.initialize(currentUserId)
            recipeRepository.initialize(currentUserId)
            loadData(null)
        }
    }
    fun loadData(folderId: String?) {
        folderRepository.watchFolder(folderId) { folders ->
            _uiState.value = _uiState.value.copy(folders = folders)
        }
        recipeRepository.watchRecipesInFolder(folderId) { recipes ->
            _uiState.value = _uiState.value.copy(recipes = recipes)
        }
    }
}
