package com.example.recipekeeper.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Folder(
    val id: String = "",
    val name: String = "",
    val recipes: List<Recipe> = emptyList(),
    val subFolders: List<Folder> = emptyList()
): Parcelable