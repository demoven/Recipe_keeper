package com.example.recipekeeper.data.repository.impl

import android.util.Log
import com.example.recipekeeper.data.models.Recipe
import com.example.recipekeeper.data.repository.IRecipeRepository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class RecipeRepositoryImpl: IRecipeRepository {
    private val TAG = "RecipeRepositoryImpl"
    private val db = FirebaseFirestore.getInstance()

    private lateinit var recipesCollection: CollectionReference

    override fun initialize(userId: String) {
        if (userId.isEmpty()) {
            throw IllegalArgumentException("User ID cannot be empty")
        }
        if (::recipesCollection.isInitialized) {
            Log.w(TAG, "RecipeRepositoryImpl is already initialized. Re-initializing with new userId.")
        }
        recipesCollection = db.collection("users").document(userId).collection("recipes")
    }

    override fun watchRecipesInFolder(folderId: String?, onResult: (List<Recipe>) -> Unit): ListenerRegistration {
        if (!::recipesCollection.isInitialized) {
            throw IllegalStateException("Repository not initialized. Call initialize(userId) first.")
        }

        return recipesCollection
            .whereEqualTo("folderId", folderId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.d(TAG, "watchRecipesInFolder failed: ", exception)
                    return@addSnapshotListener
                }
                val recipes = snapshot?.map { it.toObject(Recipe::class.java) } ?: emptyList()
                onResult(recipes)
            }
    }
}