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

    override fun getRecipeById(recipeId: String, onResult: (Recipe?) -> Unit) {
        val collection = recipesCollection
            ?: throw UninitializedPropertyAccessException("RecipeRepositoryImpl must be initialized with a valid userId before use.")

        collection.document(recipeId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val recipe = document.toObject(Recipe::class.java)
                    onResult(recipe)
                } else {
                    Log.d(TAG, "No such document")
                    onResult(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                onResult(null)
            }
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
    override fun saveRecipe(recipe: Recipe, onSuccess: (String, String) -> Unit, onFailure: () -> Unit) {
        val collection = recipesCollection
            ?: throw UninitializedPropertyAccessException("RecipeRepositoryImpl must be initialized with a valid userId before use.")

        val docRef = if (recipe.id.isNotEmpty()) {
            collection.document(recipe.id)
        } else {
            collection.document()
        }

        val recipeWithId = recipe.copy(id = docRef.id)

        docRef.set(recipeWithId)
            .addOnSuccessListener {
                Log.d(TAG, "Recipe saved with ID: ${docRef.id}")
                onSuccess(docRef.id, recipeWithId.title)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error saving recipe", e)
                onFailure()
            }
    }

    override suspend fun deleteAllRecipes() {
        val collection = recipesCollection
            ?: throw UninitializedPropertyAccessException("RecipeRepositoryImpl must be initialized with a valid userId before use.")

        val snapshot = collection.get().await()

        if (snapshot.isEmpty) {
            return
        }
        val batch = db.batch()
        snapshot.documents.forEach { document ->
            batch.delete(document.reference)
        }
        batch.commit().await()
    }

    override suspend fun deleteRecipeById(
        recipeId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val collection = recipesCollection
            ?: throw UninitializedPropertyAccessException("RecipeRepositoryImpl must be initialized with a valid userId before use.")

        collection.document(recipeId).delete()
            .addOnSuccessListener {
                Log.d(TAG, "Recipe deleted with ID: $recipeId")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting recipe", e)
                onFailure()
            }
    }

    override fun updateRecipe(
        recipe: Recipe,
        onSuccess: (String, String) -> Unit,
        onFailure: () -> Unit
    ) {
        val collection = recipesCollection
            ?: throw UninitializedPropertyAccessException("RecipeRepositoryImpl must be initialized with a valid userId before use.")

        // On crée la map des champs à mettre à jour en EXCLUANT "folderId"
        // et "id" (qui sert de clé au document)
        val updates = mapOf(
            "title" to recipe.title,
            "description" to recipe.description,
            "ingredients" to recipe.ingredients,
            "instructions" to recipe.instructions,
            "prepTime" to recipe.prepTime,
            "cookTime" to recipe.cookTime,
            "servings" to recipe.servings,
            "imageUrl" to recipe.imageUrl
            // "folderId" est volontairement omis ici pour ne pas le modifier
        )

        collection.document(recipe.id).update(updates)
            .addOnSuccessListener {
                Log.d(TAG, "Recipe updated with ID: ${recipe.id}")
                onSuccess(recipe.id, recipe.title)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating recipe", e)
                onFailure()
            }
    }
}