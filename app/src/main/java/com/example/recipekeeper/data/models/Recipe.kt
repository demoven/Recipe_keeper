package com.example.recipekeeper.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val ingredients: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
    val prepTime: Int = 0, // in minutes
    val cookTime: Int = 0, // in minutes
    val servings: Int = 0,
    val imageUrl: String? = null,
    val folderId: String? = null,
) : Parcelable
