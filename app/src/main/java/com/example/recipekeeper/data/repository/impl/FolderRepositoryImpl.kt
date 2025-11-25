package com.example.recipekeeper.data.repository.impl

import android.util.Log
import com.example.recipekeeper.data.models.Folder
import com.example.recipekeeper.data.repository.IFolderRepository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class FolderRepositoryImpl : IFolderRepository {

    private val TAG = "FolderRepositoryImpl"
    private val db = FirebaseFirestore.getInstance()

    private var foldersCollection: CollectionReference? = null

    override fun initialize(userId: String) {
        if (userId.isEmpty()) {
            throw IllegalArgumentException("User ID cannot be empty")
        }
        foldersCollection = db.collection("users").document(userId).collection("folders")
    }

    override fun watchFolder(parentId: String?, onResult: (List<Folder>) -> Unit): ListenerRegistration {
        val collection = foldersCollection
            ?: throw UninitializedPropertyAccessException("FolderRepositoryImpl must be initialized with a valid userId before use.")
        return collection
            .whereEqualTo("parentId", parentId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e(TAG, "watchFolder failed: ", exception)
                    return@addSnapshotListener
                }
                val folders = snapshot?.mapNotNull { it.toObject<Folder>() } ?: emptyList()
                onResult(folders)
            }
    }

    override fun addFolder(folder: Folder, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val collection = foldersCollection
            ?: throw UninitializedPropertyAccessException("FolderRepositoryImpl must be initialized with a valid userId before use.")
        collection.add(folder)
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

    override fun updateFolder(
        folderId: String,
        newName: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val collection = foldersCollection
            ?: throw UninitializedPropertyAccessException("FolderRepositoryImpl must be initialized with a valid userId before use.")
        collection.document(folderId)
            .update("name", newName)
            .addOnSuccessListener {
                Log.d(TAG, "Folder updated with ID: $folderId")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating folder", e)
                onFailure()
            }
    }

    override fun moveFolder(
        folderId: String,
        newParentId: String?,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val collection = foldersCollection
            ?: throw UninitializedPropertyAccessException("FolderRepositoryImpl must be initialized with a valid userId before use.")
        collection.document(folderId)
            .update("parentId", newParentId)
            .addOnSuccessListener {
                Log.d(TAG, "Folder moved with ID: $folderId")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error moving folder", e)
                onFailure()
            }
    }

    override suspend fun getSubFolders(parentId: String): List<Folder> {
        val collection = foldersCollection
            ?: throw UninitializedPropertyAccessException("FolderRepositoryImpl must be initialized with a valid userId before use.")

        val snapshot = collection.whereEqualTo("parentId", parentId).get().await()
        return snapshot.toObjects(Folder::class.java)
    }

    override suspend fun deleteFolder(folderId: String) {
        foldersCollection?.document(folderId)?.delete()?.await()
    }
}
