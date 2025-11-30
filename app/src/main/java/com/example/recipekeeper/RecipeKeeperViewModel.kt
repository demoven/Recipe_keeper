package com.example.recipekeeper

import androidx.lifecycle.ViewModel
import com.example.recipekeeper.data.repository.IAuthRepository
import com.example.recipekeeper.ui.models.RecipeKeeperScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecipeKeeperViewModel(
    private val authRepository: IAuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecipeKeeperUiState())
    val uiState: StateFlow<RecipeKeeperUiState> = _uiState.asStateFlow()
    val isUserLoggedIn: StateFlow<Boolean> = authRepository.isUserLoggedIn
    private val screensWithoutBottomBar = setOf(
        RecipeKeeperScreen.AddFolder,
        RecipeKeeperScreen.CreateRecipe,
        RecipeKeeperScreen.Account,
        RecipeKeeperScreen.Settings,
        RecipeKeeperScreen.Login,
        RecipeKeeperScreen.Register,
        RecipeKeeperScreen.RecipeDetail,
        RecipeKeeperScreen.Cooking
    )
    private val screensWithoutTopBar = setOf(
        RecipeKeeperScreen.Login,
        RecipeKeeperScreen.Register
    )

    fun isEmailVerified(): Boolean {
        return authRepository.isEmailVerified()
    }

    suspend fun reloadUser() {
        authRepository.reloadUser()
    }

    fun onNavigationChange(route: String?) {
        val routeBase = route?.substringBefore('?') ?: RecipeKeeperScreen.Home.name
        val screen = try {
            RecipeKeeperScreen.valueOf(routeBase)
        } catch (e: Exception) {
            RecipeKeeperScreen.Home
        }

        _uiState.value = _uiState.value.copy(
            currentScreen = screen,
            isTopBarVisible = screen !in screensWithoutTopBar,
            isBottomBarVisible = screensWithoutBottomBar.none { screenWithoutBottomBar ->
                routeBase.startsWith(screenWithoutBottomBar.name)
            }
        )
    }

    // Gestion des Sheets
    fun openMainSheet() { _uiState.value = _uiState.value.copy(isMainSheetVisible = true) }
    fun closeMainSheet() { _uiState.value = _uiState.value.copy(isMainSheetVisible = false) }

    fun openAddFolderSheet() {
        _uiState.value = _uiState.value.copy(isMainSheetVisible = false, isAddFolderSheetVisible = true)
    }
    fun closeAddFolderSheet() { _uiState.value = _uiState.value.copy(isAddFolderSheetVisible = false) }

    // Folder Menu and Dialogs
    fun showFolderMenu() { _uiState.value = _uiState.value.copy(isFolderMenuVisible = true) }
    fun hideFolderMenu() { _uiState.value = _uiState.value.copy(isFolderMenuVisible = false) }
    fun showRenameDialog() { _uiState.value = _uiState.value.copy(isRenameDialogVisible = true) }
    fun hideRenameDialog() { _uiState.value = _uiState.value.copy(isRenameDialogVisible = false) }
    fun showDeleteDialog() { _uiState.value = _uiState.value.copy(isDeleteDialogVisible = true) }
    fun hideDeleteDialog() { _uiState.value = _uiState.value.copy(isDeleteDialogVisible = false) }

    // Recipe Menu and Dialogs
    fun showRecipeMenu() { _uiState.value = _uiState.value.copy(isRecipeMenuVisible = true) }
    fun hideRecipeMenu() { _uiState.value = _uiState.value.copy(isRecipeMenuVisible = false) }
    fun showRecipeDeleteDialog() { _uiState.value = _uiState.value.copy(isDeleteRecipeDialogVisible = true) }
    fun hideRecipeDeleteDialog() { _uiState.value = _uiState.value.copy(isDeleteRecipeDialogVisible = false) }
}