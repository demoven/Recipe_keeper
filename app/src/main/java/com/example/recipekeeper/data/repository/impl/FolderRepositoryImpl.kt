package com.example.recipekeeper.data.repository.impl

import android.util.Log
import com.example.recipekeeper.data.models.Folder
import com.example.recipekeeper.data.repository.IFolderRepository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject

class FolderRepositoryImpl: IFolderRepository {

    private val TAG = "FolderRepositoryImpl"
    private val db = FirebaseFirestore.getInstance()

    private lateinit var foldersCollection: CollectionReference

    override fun initialize(userId: String) {
        if (userId.isEmpty()) {
            throw IllegalArgumentException("User ID cannot be empty")
        }
        if (::foldersCollection.isInitialized) {
            Log.w(TAG, "FolderRepositoryImpl is already initialized. Re-initializing with new userId.")
        }
        foldersCollection = db.collection("users").document(userId).collection("folders")
    }

    override fun watchFolder(parentId: String?, onResult: (List<Folder>) -> Unit): ListenerRegistration {
        if(!::foldersCollection.isInitialized){
            throw UninitializedPropertyAccessException("FolderRepositoryImpl must be initialized with a valid userId before use.")
        }
        return foldersCollection
            .whereEqualTo("parentId", parentId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e(TAG, "watchRecipesInFolder failed: ", exception)
                    return@addSnapshotListener
                }
                val folders = snapshot?.mapNotNull { it.toObject<Folder>() } ?: emptyList()
                onResult(folders)
            }
    }

    override fun addFolder(folder: Folder, onSuccess: () -> Unit, onFailure: () -> Unit) {
        if(!::foldersCollection.isInitialized){
            throw UninitializedPropertyAccessException("FolderRepositoryImpl must be initialized with a valid userId before use.")
        }
        foldersCollection.add(folder)
            .addOnSuccessListener { docRef ->
                val generatedId = docRef.id
                // Update the "id" field in the document to keep the generated ID
                docRef.update("id", generatedId)
                    .addOnSuccessListener {
                        Log.d(TAG, "Folder added with ID: $generatedId")
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating folder ID", e)
                        onFailure()
                    }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding folder", e)
                onFailure()
            }
    }
}
