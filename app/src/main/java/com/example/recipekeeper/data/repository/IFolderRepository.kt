package com.example.recipekeeper.data.repository

import com.example.recipekeeper.data.models.Folder
import com.google.firebase.firestore.ListenerRegistration

interface IFolderRepository {
    fun initialize(userId: String)

    fun watchFolder(
        parentId: String?,
        onResult: (List<Folder>) -> Unit
    ): ListenerRegistration

    fun addFolder(
        folder: Folder,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )
}