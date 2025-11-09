package com.example.recipekeeper.data.repository

import android.util.Log
import com.example.recipekeeper.data.models.Folder
import com.example.recipekeeper.data.models.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RecipeRepository {
    private val TAG = "RecipeRepository"
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val recipesCollection = db.collection("users").document(userId ?: "").collection("recipes")
    private val foldersCollection = db.collection("users").document(userId ?: "").collection("folders")

    fun getFolders(parentId: String?, onResult:(List<Folder>) -> Unit) {
        Log.d(TAG, "Fetching folders with parentId: $parentId")
        foldersCollection
            .whereEqualTo("parentId", parentId)
            .get()
            .addOnSuccessListener { result ->
                val folders = result.map { it.toObject(Folder::class.java) }
                onResult(folders)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "getInitialFolders failed: ", exception)
            }
    }

    fun getRecipesInFolder(folderId: String?, onResult:(List<Recipe>) -> Unit) {
        Log.d(TAG, "Fetching recipes in folder: $folderId")
        recipesCollection
            .whereEqualTo("folderId", folderId)
            .get()
            .addOnSuccessListener { result ->
                val recipes = result.map { it.toObject(Recipe::class.java) }
                onResult(recipes)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "getRecipesWithoutFolder failed: ", exception)
            }
    }
}