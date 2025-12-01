package com.example.recipekeeper

import com.example.recipekeeper.ui.components.snackbar.SnackbarType
import com.example.recipekeeper.ui.models.RecipeKeeperScreen

data class RecipeKeeperUiState(
    val currentScreen: RecipeKeeperScreen = RecipeKeeperScreen.Home,
    val isTopBarVisible: Boolean = true,
    val isBottomBarVisible: Boolean = true,
    val isMainSheetVisible: Boolean = false,
    val isAddFolderSheetVisible: Boolean = false,
    // Folder Menu
    val isFolderMenuVisible: Boolean = false,
    val isRenameDialogVisible: Boolean = false,
    val isDeleteDialogVisible: Boolean = false,
    // Recipe Menu
    val isRecipeMenuVisible: Boolean = false,
    val isDeleteRecipeDialogVisible: Boolean = false,
    // Snackbar handler
    val snackbarMessage: String? = null,
    val snackbarType: SnackbarType = SnackbarType.Info,
)
