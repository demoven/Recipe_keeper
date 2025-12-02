package com.example.recipekeeper.ui.components.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

class RecipeKeeperSnackbarVisuals(
    override val message: String,
    val type: SnackbarType, // <--- On stocke le type ICI
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
) : SnackbarVisuals
