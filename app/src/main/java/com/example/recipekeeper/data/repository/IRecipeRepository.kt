package com.example.recipekeeper.data.repository

import com.example.recipekeeper.data.models.Recipe
import com.google.firebase.firestore.ListenerRegistration

interface IRecipeRepository {
    fun initialize(userId: String)

    fun watchRecipesInFolder(
        folderId: String?,
        onResult: (List<Recipe>) -> Unit
    ): ListenerRegistration

    suspend fun deleteRecipesInFolder(folderId: String)
    fun saveRecipe(
        recipe: Recipe,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )
}