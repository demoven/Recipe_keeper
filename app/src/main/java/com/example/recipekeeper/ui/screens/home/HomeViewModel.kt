package com.example.recipekeeper.ui.screens.home

import androidx.lifecycle.ViewModel
import com.example.recipekeeper.data.repository.IAuthRepository
import com.example.recipekeeper.data.repository.IFolderRepository
import com.example.recipekeeper.data.repository.IRecipeRepository
import com.google.firebase.firestore.ListenerRegistration
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

    private var foldersListener: ListenerRegistration? = null
    private var recipesListener: ListenerRegistration? = null


    init {
        loadData(null)
    }
    fun loadData(folderId: String?) {
        foldersListener?.remove()
        recipesListener?.remove()

        foldersListener = folderRepository.watchFolder(folderId) { folders ->
            _uiState.value = _uiState.value.copy(folders = folders)
        }
        recipesListener = recipeRepository.watchRecipesInFolder(folderId) { recipes ->
            _uiState.value = _uiState.value.copy(recipes = recipes)
        }
    }

    //TODO implements moveFolder
    fun moveFolder(folderId: String, targetParentId: String?) {
        folderRepository.moveFolder(
            folderId = folderId,
            newParentId = targetParentId,
            onSuccess = {
                // Optionnel : Gérer le succès (afficher un toast, fermer un dialogue, etc.)
            },
            onFailure = {
                // Optionnel : Gérer l'erreur
            }
        )
    }
    fun selectFolder(folder: String?) {
        _uiState.value = _uiState.value.copy(selectedFolder = folder)
    }

    override fun onCleared() {
        super.onCleared()
        foldersListener?.remove()
        recipesListener?.remove()
    }
}
