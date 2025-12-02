package com.example.recipekeeper.data.repository.impl

import com.example.recipekeeper.data.models.Recipe
import com.example.recipekeeper.data.repository.IRecipeRepository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class RecipeRepositoryImpl : IRecipeRepository {
    private val db = FirebaseFirestore.getInstance()

    private var recipesCollection: CollectionReference? = null

    override fun initialize(userId: String) {
        if (userId.isEmpty()) {
            throw IllegalArgumentException("User ID cannot be empty")
        }
        recipesCollection = db.collection("users").document(userId).collection("recipes")
    }

    override fun getRecipeById(
        recipeId: String,
        onResult: (Recipe?) -> Unit,
    ) {
        val collection =
            recipesCollection
                ?: throw UninitializedPropertyAccessException("RecipeRepositoryImpl must be initialized with a valid userId before use.")

        collection
            .document(recipeId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val recipe = document.toObject(Recipe::class.java)
                    onResult(recipe)
                } else {
                    onResult(null)
                }
            }.addOnFailureListener { exception ->
                onResult(null)
            }
    }

    override fun watchRecipesInFolder(
        folderId: String?,
        onResult: (List<Recipe>) -> Unit,
    ): ListenerRegistration {
        val collection =
            recipesCollection
                ?: throw UninitializedPropertyAccessException("RecipeRepositoryImpl must be initialized with a valid userId before use.")

        return collection
            .whereEqualTo("folderId", folderId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                val recipes = snapshot?.map { it.toObject(Recipe::class.java) } ?: emptyList()
                onResult(recipes)
            }
    }

    override suspend fun deleteRecipesInFolder(folderId: String) {
        val collection =
            recipesCollection
                ?: throw UninitializedPropertyAccessException("RecipeRepositoryImpl must be initialized with a valid userId before use.")

        val snapshot =
            collection
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

    override fun saveRecipe(
        recipe: Recipe,
        onSuccess: (String, String) -> Unit,
        onFailure: () -> Unit,
    ) {
        val collection =
            recipesCollection
                ?: throw UninitializedPropertyAccessException("RecipeRepositoryImpl must be initialized with a valid userId before use.")

        val docRef =
            if (recipe.id.isNotEmpty()) {
                collection.document(recipe.id)
            } else {
                collection.document()
            }

        val recipeWithId = recipe.copy(id = docRef.id)

        docRef
            .set(recipeWithId)
            .addOnSuccessListener {
                onSuccess(docRef.id, recipeWithId.title)
            }.addOnFailureListener { e ->
                onFailure()
            }
    }

    override suspend fun deleteAllRecipes() {
        val collection =
            recipesCollection
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
        onFailure: () -> Unit,
    ) {
        val collection =
            recipesCollection
                ?: throw UninitializedPropertyAccessException("RecipeRepositoryImpl must be initialized with a valid userId before use.")

        collection
            .document(recipeId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener { e ->
                onFailure()
            }
    }

    override fun updateRecipe(
        recipe: Recipe,
        onSuccess: (String, String) -> Unit,
        onFailure: () -> Unit,
    ) {
        val collection =
            recipesCollection
                ?: throw UninitializedPropertyAccessException("RecipeRepositoryImpl must be initialized with a valid userId before use.")

        val updates =
            mapOf(
                "title" to recipe.title,
                "description" to recipe.description,
                "ingredients" to recipe.ingredients,
                "instructions" to recipe.instructions,
                "prepTime" to recipe.prepTime,
                "cookTime" to recipe.cookTime,
                "servings" to recipe.servings,
                "imageUrl" to recipe.imageUrl,
            )

        collection
            .document(recipe.id)
            .update(updates)
            .addOnSuccessListener {
                onSuccess(recipe.id, recipe.title)
            }.addOnFailureListener { e ->
                onFailure()
            }
    }
}
