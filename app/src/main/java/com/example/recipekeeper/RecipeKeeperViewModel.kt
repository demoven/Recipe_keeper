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
        RecipeKeeperScreen.Register
    )
    private val screensWithoutTopBar = setOf(
        RecipeKeeperScreen.Login,
        RecipeKeeperScreen.Register
    )

    fun logout() {
        authRepository.logout()
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
            isBottomBarVisible = screen !in screensWithoutBottomBar
        )
    }

    // Gestion des Sheets
    fun openMainSheet() { _uiState.value = _uiState.value.copy(isMainSheetVisible = true) }
    fun closeMainSheet() { _uiState.value = _uiState.value.copy(isMainSheetVisible = false) }

    fun openAddFolderSheet() {
        _uiState.value = _uiState.value.copy(isMainSheetVisible = false, isAddFolderSheetVisible = true)
    }
    fun closeAddFolderSheet() { _uiState.value = _uiState.value.copy(isAddFolderSheetVisible = false) }
}