package com.example.recipekeeper

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.recipekeeper.ui.screens.home.HomeScreen
import com.example.recipekeeper.ui.screens.AccountScreen
import com.example.recipekeeper.ui.screens.recipe.CreateRecipeScreen
import com.example.recipekeeper.ui.screens.SettingsScreen
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.recipekeeper.data.models.Folder
import com.example.recipekeeper.di.AppContainer
import com.example.recipekeeper.ui.components.BottomNavigationBar
import com.example.recipekeeper.ui.components.BottomSheetAddFolder
import com.example.recipekeeper.ui.components.BottomSheetContent
import com.example.recipekeeper.ui.components.RecipeKeeperTopBar
import com.example.recipekeeper.ui.components.RenameFolderDialog
import com.example.recipekeeper.ui.components.actions.FolderActions
import com.example.recipekeeper.ui.models.RecipeKeeperScreen
import com.example.recipekeeper.ui.screens.auth.login.LoginScreen
import com.example.recipekeeper.ui.screens.auth.register.RegisterScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeKeeperApp(
    navController: NavHostController = rememberNavController(),
    appContainer: AppContainer
) {
    val authRepository = appContainer.authRepository
    val authFactory = appContainer.authFactory
    val recipeKeeperFactory = appContainer.recipeKeeperFactory

    val recipeKeeperViewModel: RecipeKeeperViewModel = viewModel(factory = recipeKeeperFactory)
    val uiState by recipeKeeperViewModel.uiState.collectAsState()
    val isLoggedIn by recipeKeeperViewModel.isUserLoggedIn.collectAsState()

    val currentUserId = authRepository.getCurrentUserId()

    val userContainer = remember(isLoggedIn, currentUserId) {
        if (isLoggedIn && currentUserId != null) {
            appContainer.createUserContainer(userId = currentUserId)
        } else {
            null
        }
    }

    val homeViewModelFactory = userContainer?.homeFactory

    val addFolderAction: (String, String?) -> Unit = { folderName, parentId ->
        userContainer?.addFolder(
            folder = Folder(
                name = folderName,
                parentId = parentId
            ),
            onSuccess = {},
            onFailure = {}
        )
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val currentFolderName = backStackEntry?.arguments?.getString("folderName")

    val initialTitle = if (currentFolderName != null) {
        currentFolderName
    } else {
        val currentScreen = try {
            RecipeKeeperScreen.valueOf(
                currentRoute?.substringBefore('?') ?: RecipeKeeperScreen.Home.name
            )
        } catch (e: Exception) {
            RecipeKeeperScreen.Home
        }
        stringResource(currentScreen.title)
    }

    var folderTitle by remember(currentRoute, currentFolderName) { mutableStateOf(initialTitle) }

    LaunchedEffect(currentFolderName) {
        folderTitle = currentFolderName ?: initialTitle
    }

    LaunchedEffect(currentRoute) {
        recipeKeeperViewModel.onNavigationChange(currentRoute)
    }

    val currentFolderId = if (currentRoute?.startsWith(RecipeKeeperScreen.Home.name) == true) {
        backStackEntry?.arguments?.getString("folderId")
    } else null

    val startDestination = remember {
        if (isLoggedIn && authRepository.isEmailVerified()) {
            RecipeKeeperScreen.Home.name
        } else {
            RecipeKeeperScreen.Login.name
        }
    }

    DisposableEffect(Unit) {
        onDispose { (authRepository as? AutoCloseable)?.close() }
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            try {
                recipeKeeperViewModel.reloadUser()
            } catch (e: Exception) {
                Log.e("RecipeKeeperApp", "Impossible de rafraîchir l'utilisateur", e)
            }
            val isVerified = authRepository.isEmailVerified()
            if (isVerified) {
                val currentRoute = navController.currentDestination?.route
                val isAuthScreen = currentRoute == RecipeKeeperScreen.Login.name ||
                        currentRoute == RecipeKeeperScreen.Register.name ||
                        currentRoute == null
                if (isAuthScreen) {
                    navController.navigate(RecipeKeeperScreen.Home.name) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    val mainSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val addFolderSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val updateFolderAction: (String, String) -> Unit = { folderId, newName ->
        userContainer?.folderRepository?.updateFolder(
            folderId = folderId,
            newName = newName,
            onSuccess = { /* TODO Gérer le succès, ex: Snackbar */ },
            onFailure = { /* TODO Gérer l'erreur */ }
        )
    }

    val deleteFolderAction: (String) -> Unit = { folderId ->
        coroutineScope.launch {
            userContainer?.deleteFolderRecursive(folderId)
        }
    }

    val showFolderActions =
        uiState.currentScreen == RecipeKeeperScreen.Home && currentFolderId != null

    var onSaveClick by remember { mutableStateOf<(() -> Unit)?>(null) }

    LaunchedEffect(currentRoute) {
        recipeKeeperViewModel.onNavigationChange(currentRoute)
        if (currentRoute?.startsWith(RecipeKeeperScreen.CreateRecipe.name) == false) {
            onSaveClick = null
        }
    }

    Scaffold(
        topBar = {
            if (uiState.isTopBarVisible) {
                val isCreateRecipeScreen = currentRoute?.startsWith(RecipeKeeperScreen.CreateRecipe.name) == true
                RecipeKeeperTopBar(
                    title = folderTitle,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
                    actions = {
                        if (showFolderActions) {
                            FolderActions(
                                onShowFolderMenu = { recipeKeeperViewModel.showFolderMenu() },
                                isFolderMenuVisible = uiState.isFolderMenuVisible,
                                hideFolderMenu = { recipeKeeperViewModel.hideFolderMenu() },
                                showRenameDialog = { recipeKeeperViewModel.showRenameDialog() },
                                showDeleteDialog = { recipeKeeperViewModel.showDeleteDialog() }
                            )
                        }
                        if (isCreateRecipeScreen) {
                            onSaveClick?.let {
                                IconButton(onClick = it) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Sauvegarder"
                                    )
                                }
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (uiState.isBottomBarVisible) {
                BottomNavigationBar(
                    currentScreen = uiState.currentScreen,
                    onNavigate = { screen ->
                        if (screen == RecipeKeeperScreen.CreateRecipe) {
                            recipeKeeperViewModel.openMainSheet()
                        } else if (uiState.currentScreen != screen) {
                            navController.navigate(screen.name) {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        if (uiState.isRenameDialogVisible && currentFolderId != null) {
            RenameFolderDialog(
                currentFolderName = folderTitle,
                onDismiss = { recipeKeeperViewModel.hideRenameDialog() },
                onConfirm = { newName ->
                    updateFolderAction(currentFolderId, newName)
                    folderTitle = newName
                    recipeKeeperViewModel.hideRenameDialog()
                }
            )
        }
        if (uiState.isDeleteDialogVisible && currentFolderId != null) {
            com.example.recipekeeper.ui.components.DeleteFolderDialog(
                onDismiss = { recipeKeeperViewModel.hideDeleteDialog() },
                onConfirm = {
                    recipeKeeperViewModel.hideDeleteDialog()
                    deleteFolderAction(currentFolderId)
                    navController.navigateUp()
                }
            )
        }
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            composable(
                route = "${RecipeKeeperScreen.Home.name}?folderId={folderId}&folderName={folderName}",
                arguments = listOf(
                    navArgument("folderId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("folderName") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    })
            ) { entry ->
                if (homeViewModelFactory != null) {
                    val folderId = entry.arguments?.getString("folderId")
                    HomeScreen(
                        folderId = folderId,
                        onNavigateToSubFolder = { subFolderId, subFolderName ->
                            navController.navigate("${RecipeKeeperScreen.Home.name}?folderId=$subFolderId&folderName=$subFolderName")
                        },
                        onNavigateToRecipeDetails = {},
                        homeFactory = homeViewModelFactory,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate(RecipeKeeperScreen.Login.name) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }

            }
            composable(RecipeKeeperScreen.Account.name) {
                AccountScreen()
            }
            composable(
                route = "${RecipeKeeperScreen.CreateRecipe.name}?folderId={folderId}",
                arguments = listOf(navArgument("folderId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { entry ->
                if (userContainer != null) {
                    val folderId = entry.arguments?.getString("folderId")
                    CreateRecipeScreen(
                        folderId = folderId,
                        createRecipeFactory = userContainer.createRecipeFactory,
                        onSetSaveAction = { action -> onSaveClick = action },
                        onRecipeSuccess = {
                            navController.navigateUp()
                        }
                    )
                }
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
                    authFactory = authFactory,
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
                    authFactory = authFactory,
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_large))
                        .fillMaxSize()
                )
            }
        }

        if (uiState.isMainSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { recipeKeeperViewModel.closeMainSheet() },
                sheetState = mainSheetState
            ) {
                BottomSheetContent(
                    onAddFolder = { recipeKeeperViewModel.openAddFolderSheet() },
                    onAddRecipe = {
                        recipeKeeperViewModel.closeMainSheet()
                        val route = if (currentFolderId != null) {
                            "${RecipeKeeperScreen.CreateRecipe.name}?folderId=$currentFolderId"
                        } else {
                            RecipeKeeperScreen.CreateRecipe.name
                        }
                        navController.navigate(route) { launchSingleTop = true }
                    }
                )
            }
        }

        if (uiState.isAddFolderSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { recipeKeeperViewModel.closeAddFolderSheet() },
                sheetState = addFolderSheetState
            ) {
                BottomSheetAddFolder(
                    onAdd = { folderName ->
                        recipeKeeperViewModel.closeAddFolderSheet()
                        addFolderAction(folderName, currentFolderId)
                    }
                )
            }
        }
    }
}
