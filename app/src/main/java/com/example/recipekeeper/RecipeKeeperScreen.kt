package com.example.recipekeeper

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.recipekeeper.ui.screens.home.HomeScreen
import com.example.recipekeeper.ui.screens.AccountScreen
import com.example.recipekeeper.ui.screens.CreateRecipeScreen
import com.example.recipekeeper.ui.screens.SettingsScreen
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.recipekeeper.R.string.dossier
import com.example.recipekeeper.data.factory.RecipeKeeperViewModelFactory
import com.example.recipekeeper.data.repository.RecipeRepository
import com.example.recipekeeper.ui.screens.auth.login.LoginScreen
import com.example.recipekeeper.ui.screens.auth.register.RegisterScreen
import kotlinx.coroutines.launch

enum class RecipeKeeperScreen(@StringRes val title: Int) {
    Home(title = R.string.app_name),
    Account(title = R.string.account),
    CreateRecipe(title = R.string.create_recipe),
    AddFolder(title = R.string.add_folder),
    Settings(title = R.string.settings),
    Login(title = R.string.login),
    Register(title = R.string.register),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeKeeperApp(
    navController: NavHostController = rememberNavController()
) {
    val factory = RecipeKeeperViewModelFactory(RecipeRepository())
    val recipeKeeperViewModel: RecipeKeeperViewModel = viewModel(factory = factory)
    val isLoggedIn by recipeKeeperViewModel.isUserLoggedIn.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val startDestination = if (isLoggedIn) {
        RecipeKeeperScreen.Home.name
    } else {
        RecipeKeeperScreen.Login.name
    }
    val snackbarHostState = remember { SnackbarHostState() }

    val currentRoute = backStackEntry?.destination?.route
    val currentRouteBase = currentRoute?.substringBefore('?')
    val currentScreen = try {
        RecipeKeeperScreen.valueOf(currentRouteBase ?: RecipeKeeperScreen.Home.name)
    } catch (e: Exception) {
        Log.d("RecipeKeeperApp", "Erreur : $e")
        if (currentRoute?.startsWith(RecipeKeeperScreen.Home.name) == true) {
            RecipeKeeperScreen.Home
        } else {
            RecipeKeeperScreen.Home
        }
    }

    val screensWithoutBottomBar = setOf(
        RecipeKeeperScreen.AddFolder,
        RecipeKeeperScreen.CreateRecipe,
        RecipeKeeperScreen.Account,
        RecipeKeeperScreen.Settings,
        RecipeKeeperScreen.Login,
        RecipeKeeperScreen.Register
    )
    val screensWithoutTopBar = setOf(
        RecipeKeeperScreen.Login,
        RecipeKeeperScreen.Register
    )
    val showTopBar = currentScreen !in screensWithoutTopBar
    val showBottomBar = currentScreen !in screensWithoutBottomBar

    val mainSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val addFolderSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val coroutineScope = rememberCoroutineScope()
    var showMainSheet by remember { mutableStateOf(false) }
    var showAddFolderSheet by remember { mutableStateOf(false) }

    val currentFolderId = if (currentRouteBase == RecipeKeeperScreen.Home.name) {
        backStackEntry?.arguments?.getString("folderId")
    } else null

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .imePadding()
                    .navigationBarsPadding()
            ) },
        topBar = {
            if (showTopBar) {
                RecipeKeeperTopBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentScreen = currentScreen,
                    onNavigate = { screen ->
                        if (screen == RecipeKeeperScreen.CreateRecipe) {
                            coroutineScope.launch { showMainSheet = true }
                        } else if (currentScreen != screen) {
                            navController.navigate(screen.name) {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            composable(
                route = "${RecipeKeeperScreen.Home.name}?folderId={folderId}",
                arguments = listOf(navArgument("folderId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) {
                entry ->
                val folderId = entry.arguments?.getString("folderId")
                HomeScreen(
                    folderId = folderId,
                    onNavigateToSubFolder = { subFolderId ->
                        navController.navigate("${RecipeKeeperScreen.Home.name}?folderId=$subFolderId")                     },
                    onNavigateToRecipeDetails = {},
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(RecipeKeeperScreen.Account.name) {
                AccountScreen()
            }
            composable(RecipeKeeperScreen.CreateRecipe.name) {
                CreateRecipeScreen()
            }
            composable(RecipeKeeperScreen.Settings.name) {
                SettingsScreen(
                    onNavigateToAccount = { navController.navigate(RecipeKeeperScreen.Account.name) },
                    onLogout = {
                        recipeKeeperViewModel.logout()
                    }
                )
            }
            composable(RecipeKeeperScreen.AddFolder.name) {
                Text("Écran : Ajouter un dossier")
            }
            composable(RecipeKeeperScreen.Login.name) {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(RecipeKeeperScreen.Register.name)
                    },
                    onShowErrorMessage = { message ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(message)
                        }
                    },
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_large))
                        .fillMaxSize()
                )
            }
            composable(RecipeKeeperScreen.Register.name) {
                RegisterScreen(
                    onNavigateToLogin = {
                        navController.navigate(RecipeKeeperScreen.Login.name) {
                            launchSingleTop = true
                            popUpTo(RecipeKeeperScreen.Register.name) { inclusive = true }
                        }
                    },
                    onShowErrorMessage = { message ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(message)
                        }
                    },
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_large))
                        .fillMaxSize()
                )
            }
        }

        if (showMainSheet) {
            ModalBottomSheet(
                onDismissRequest = { showMainSheet = false },
                sheetState = mainSheetState
            ) {
                BottomSheetContent(
                    onAddFolder = {
                        showMainSheet = false
                        showAddFolderSheet = true
                    },
                    onAddRecipe = {
                        showMainSheet = false
                        navController.navigate(RecipeKeeperScreen.CreateRecipe.name) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        if (showAddFolderSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddFolderSheet = false },
                sheetState = addFolderSheetState
            ) {
                BottomSheetAddFolder(
                    onAdd = { folderName ->
                        showAddFolderSheet = false
                        recipeKeeperViewModel.addFolder(
                            folderName = folderName,
                            parentId = currentFolderId
                        )
                    }
                )
            }
        }
    }
}