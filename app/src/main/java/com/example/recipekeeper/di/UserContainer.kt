package com.example.recipekeeper.di

import com.example.recipekeeper.data.models.Folder
import com.example.recipekeeper.data.models.Recipe
import com.example.recipekeeper.data.repository.IAuthRepository
import com.example.recipekeeper.data.repository.IFolderRepository
import com.example.recipekeeper.data.repository.impl.FolderRepositoryImpl
import com.example.recipekeeper.data.repository.impl.RecipeRepositoryImpl
import com.example.recipekeeper.di.factory.CreateRecipeViewModelFactory
import com.example.recipekeeper.di.factory.HomeViewModelFactory

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

    fun addFolder(folder: Folder, onSuccess: () -> Unit, onFailure: () -> Unit) {
        folderRepository.addFolder(
            folder = folder,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun saveRecipe(recipe: Recipe, onSuccess: () -> Unit, onFailure: () -> Unit) {
        recipeRepository.saveRecipe(
            recipe = recipe,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}