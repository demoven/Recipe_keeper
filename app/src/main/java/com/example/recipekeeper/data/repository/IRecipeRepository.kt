package com.example.recipekeeper.data.repository

import com.example.recipekeeper.data.models.Recipe
import com.google.firebase.firestore.ListenerRegistration

interface IRecipeRepository {
    fun initialize(userId: String)

    fun getRecipeById(
        recipeId: String,
        onResult: (Recipe?) -> Unit,
    )

    fun watchRecipesInFolder(
        folderId: String?,
        onResult: (List<Recipe>) -> Unit,
    ): ListenerRegistration

    suspend fun deleteRecipesInFolder(folderId: String)

    fun saveRecipe(
        recipe: Recipe,
        onSuccess: (String, String) -> Unit,
        onFailure: () -> Unit,
    )

    fun updateRecipe(
        recipe: Recipe,
        onSuccess: (String, String) -> Unit,
        onFailure: () -> Unit,
    )

    suspend fun deleteRecipeById(
        recipeId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    )

    suspend fun deleteAllRecipes()
}
