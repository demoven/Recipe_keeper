package com.example.recipekeeper

import androidx.lifecycle.ViewModel
import com.example.recipekeeper.data.models.Folder
import com.example.recipekeeper.data.repository.IAuthRepository
import com.example.recipekeeper.data.repository.IFolderRepository
import kotlinx.coroutines.flow.StateFlow

class RecipeKeeperViewModel(
    private val authRepository: IAuthRepository,
    private val folderRepository: IFolderRepository
) : ViewModel() {
    val isUserLoggedIn: StateFlow<Boolean> = authRepository.isUserLoggedIn

    init {
        val currentUserId = authRepository.getCurrentUserId()
        if (currentUserId != null) {
            folderRepository.initialize(currentUserId)
        }
    }
    fun logout(){
        authRepository.logout()
    }

    fun addFolder(folderName: String, parentId: String?) {
        folderRepository.addFolder(folder = Folder(name = folderName, parentId = parentId), onSuccess = {}, onFailure = {})
    }
}