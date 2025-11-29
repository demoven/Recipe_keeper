package com.example.recipekeeper.di

import com.example.recipekeeper.data.models.Folder
import com.example.recipekeeper.data.models.Recipe
import com.example.recipekeeper.data.repository.IAuthRepository
import com.example.recipekeeper.data.repository.IFolderRepository
import com.example.recipekeeper.data.repository.impl.FolderRepositoryImpl
import com.example.recipekeeper.data.repository.impl.RecipeRepositoryImpl
import com.example.recipekeeper.di.factory.CookingViewModelFactory
import com.example.recipekeeper.di.factory.CreateRecipeViewModelFactory
import com.example.recipekeeper.di.factory.HomeViewModelFactory
import com.example.recipekeeper.di.factory.RecipeDetailViewModelFactory
import com.example.recipekeeper.di.factory.SettingsViewModelFactory

class UserContainer(
    authRepository: IAuthRepository,
    userId: String
) {
    val folderRepository: IFolderRepository = FolderRepositoryImpl().apply {
        initialize(userId)
    }
    val recipeRepository = RecipeRepositoryImpl().apply {
        initialize(userId)
    }

    val homeFactory: HomeViewModelFactory = HomeViewModelFactory(
        folderRepository = folderRepository,
        recipeRepository = recipeRepository,
        authRepository = authRepository
    )

    val createRecipeFactory: CreateRecipeViewModelFactory = CreateRecipeViewModelFactory(
        recipeRepository = recipeRepository
    )

    val recipeDetailFactory: RecipeDetailViewModelFactory = RecipeDetailViewModelFactory(
        recipeRepository = recipeRepository
    )

    val cookingFactory: CookingViewModelFactory = CookingViewModelFactory(
        recipeRepository = recipeRepository
    )

    val settingsFactory: SettingsViewModelFactory = SettingsViewModelFactory(
        authRepository = authRepository,
        recipeRepository = recipeRepository,
        folderRepository = folderRepository
    )

    fun addFolder(folder: Folder, onSuccess: () -> Unit, onFailure: () -> Unit) {
        folderRepository.addFolder(
            folder = folder,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    suspend fun deleteFolderRecursive(folderId: String) {
        val subfolders = folderRepository.getSubFolders(folderId)
        subfolders.forEach { folder ->
            deleteFolderRecursive(folder.id)
        }
        recipeRepository.deleteRecipesInFolder(folderId)
        folderRepository.deleteFolder(folderId)
    }
}