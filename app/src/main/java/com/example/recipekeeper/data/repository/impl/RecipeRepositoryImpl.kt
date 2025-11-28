package com.example.recipekeeper.data.repository.impl

import android.util.Log
import com.example.recipekeeper.data.models.Recipe
import com.example.recipekeeper.data.repository.IRecipeRepository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class RecipeRepositoryImpl : IRecipeRepository {
    private val TAG = "RecipeRepositoryImpl"
    private val db = FirebaseFirestore.getInstance()

    private var recipesCollection: CollectionReference? = null

    override fun initialize(userId: String) {
        if (userId.isEmpty()) {
            throw IllegalArgumentException("User ID cannot be empty")
        }
        recipesCollection = db.collection("users").document(userId).collection("recipes")
    }

    override fun watchRecipesInFolder(folderId: String?, onResult: (List<Recipe>) -> Unit): ListenerRegistration {
        val collection = recipesCollection
            ?: throw UninitializedPropertyAccessException("RecipeRepositoryImpl must be initialized with a valid userId before use.")

        return collection
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

    override suspend fun deleteRecipesInFolder(folderId: String) {
        val collection = recipesCollection
            ?: throw UninitializedPropertyAccessException("RecipeRepositoryImpl must be initialized with a valid userId before use.")

        val snapshot = collection
            .whereEqualTo("folderId", folderId)
            .get()
            .await()

        if (snapshot.isEmpty) {
            return
        }
        val batch = db.batch()
        snapshot.documents.forEach { document ->
            batch.delete(document.reference)
        }
        batch.commit().await()
    }
}