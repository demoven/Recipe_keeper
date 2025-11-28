package com.example.recipekeeper.data.repository

import com.example.recipekeeper.data.models.Recipe
import com.google.firebase.firestore.ListenerRegistration

interface IRecipeRepository {
    fun initialize(userId: String)

    fun getRecipeById(
        recipeId: String,
        onResult: (Recipe?) -> Unit
    )

    fun watchRecipesInFolder(
        folderId: String?,
        onResult: (List<Recipe>) -> Unit
    ): ListenerRegistration

    fun saveRecipe(
        recipe: Recipe,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )
}