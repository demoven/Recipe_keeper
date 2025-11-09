package com.example.recipekeeper.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Folder(
    val id: String = "",
    val name: String = "",
    val parentId: String? = null
): Parcelable