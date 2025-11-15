package com.example.recipekeeper.data.repository

import android.util.Log
import com.example.recipekeeper.data.models.Folder
import com.example.recipekeeper.data.models.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class RecipeRepository {
    private val TAG = "RecipeRepository"
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val recipesCollection = db.collection("users").document(userId ?: "").collection("recipes")
    private val foldersCollection = db.collection("users").document(userId ?: "").collection("folders")
    
    fun watchFolders(parentId: String?, onResult: (List<Folder>) -> Unit): ListenerRegistration {
        return foldersCollection
            .whereEqualTo("parentId", parentId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.d(TAG, "watchFolders failed: ", exception)
                    return@addSnapshotListener
                }
                val folders = snapshot?.map { it.toObject(Folder::class.java) } ?: emptyList()
                onResult(folders)
            }
    }

    fun watchRecipesInFolder(folderId: String?, onResult: (List<Recipe>) -> Unit): ListenerRegistration {
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