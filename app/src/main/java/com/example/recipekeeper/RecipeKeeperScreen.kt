package com.example.recipekeeper

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
import com.example.recipekeeper.ui.screens.HomeScreen
import com.example.recipekeeper.ui.screens.AccountScreen
import com.example.recipekeeper.ui.screens.CreateRecipeScreen
import com.example.recipekeeper.ui.screens.SettingsScreen
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.recipekeeper.R.string.dossier
import com.example.recipekeeper.ui.auth.AuthViewModel
import com.example.recipekeeper.ui.auth.LoginScreen
import com.example.recipekeeper.ui.auth.RegisterScreen
import kotlinx.coroutines.launch

enum class RecipeKeeperScreen(@StringRes val title: Int) {
    Home(title = R.string.app_name),
    Account(title = R.string.account),
    CreateRecipe(title = R.string.create_recipe),
    AddFolder(title = R.string.add_folder),
    Settings(title = R.string.settings),
    Login(title = R.string.login),
    Register(title = R.string.register)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeKeeperApp(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val authUiState by authViewModel.uiState.collectAsState()
    val startDestination = if (authUiState.isLoggedIn) {
        RecipeKeeperScreen.Home.name
    } else {
        RecipeKeeperScreen.Login.name
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val signinError = stringResource(R.string.signin_failed)
    val registerError = stringResource(R.string.register_failed)
    LaunchedEffect(authUiState.isLoggedIn) {
        val target = if (authUiState.isLoggedIn) RecipeKeeperScreen.Home.name else RecipeKeeperScreen.Login.name
        if (navController.currentBackStackEntry?.destination?.route != target) {
            navController.navigate(target) {
                launchSingleTop = true
                //optional: clear the stack to the root to avoid unwanted history
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }
    LaunchedEffect(authUiState.loginError, authUiState.registerError) {
        if (authUiState.loginError) {
            snackbarHostState.showSnackbar(signinError)
            authViewModel.resetErrors()
        }
        if (authUiState.registerError) {
            snackbarHostState.showSnackbar(registerError)
            authViewModel.resetErrors()
        }
    }
    val currentRoute = backStackEntry?.destination?.route
    val currentScreen = RecipeKeeperScreen.valueOf(
        currentRoute ?: RecipeKeeperScreen.Home.name
    )
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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showMainSheet by remember { mutableStateOf(false) }
    var showAddFolderSheet by remember { mutableStateOf(false) }

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
                        } else if (currentScreen != screen) { // avoids navigating to the same route
                            navController.navigate(screen.name) {
                                // avoids duplicates and restores bottom nav destination state
                                launchSingleTop = true
                                // restoreState allows restoring saved destination state
                                restoreState = true
                                popUpTo(navController.graph.findStartDestination().id) {
                                    // saves bottom nav destination state
                                    saveState = true
                                }
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
            composable(RecipeKeeperScreen.Home.name) {
                HomeScreen(onNavigate = { navController.navigate(it.name) })
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
                        authViewModel.logout()
                        navController.navigate(RecipeKeeperScreen.Login.name) {
                            launchSingleTop = true
                            popUpTo(RecipeKeeperScreen.Settings.name) { inclusive = true }
                        }
                    }
                )
            }
            composable(RecipeKeeperScreen.AddFolder.name) {
                Text("Écran : Ajouter un dossier")
            }
            composable(RecipeKeeperScreen.Login.name) {
                LoginScreen(
                    email = authUiState.email,
                    password = authViewModel.password,
                    onEmailChanged = { authViewModel.updateEmail(it)},
                    onPasswordChanged = { authViewModel.updatePassword(it)},
                    onLogin = {
                        authViewModel.login() {
                            navController.navigate(RecipeKeeperScreen.Home.name) {
                                launchSingleTop = true
                                popUpTo(RecipeKeeperScreen.Login.name) { inclusive = true }
                            }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(RecipeKeeperScreen.Register.name)
                        authViewModel.resetPassword()
                        authViewModel.resetErrors()
                    },
                    emailError = authUiState.emailError,
                    passwordError = authUiState.passwordError,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_large))
                )
            }
            composable(RecipeKeeperScreen.Register.name) {
                RegisterScreen(
                    email = authUiState.email,
                    password = authViewModel.password,
                    confirmedPassword = authViewModel.confirmPassword,
                    emailError = authUiState.emailError,
                    passwordError = authUiState.passwordError,
                    confirmedPasswordError = authUiState.confirmPasswordError,
                    onEmailChanged = { authViewModel.updateEmail(it)},
                    onPasswordChanged = { authViewModel.updatePassword(it)},
                    onConfirmedPasswordChanged = { authViewModel.updateConfirmPassword(it)},
                    onRegister = {
                        authViewModel.register() {
                            navController.navigate(RecipeKeeperScreen.Home.name) {
                                launchSingleTop = true
                                popUpTo(RecipeKeeperScreen.Register.name) { inclusive = true }
                            }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(RecipeKeeperScreen.Login.name){
                            launchSingleTop = true
                            popUpTo(RecipeKeeperScreen.Register.name) { inclusive = true}
                        }
                        authViewModel.resetPassword()
                        authViewModel.resetErrors()
                    },
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_large))
                )
            }
        }
        // --- Première Bottom Sheet : choix ---
        if (showMainSheet) {
            ModalBottomSheet(
                onDismissRequest = { showMainSheet = false },
                sheetState = sheetState
            ) {
                BottomSheetContent(
                    onAddFolder = {
                        showMainSheet = false
                        showAddFolderSheet = true
                    },
                    onAddRecipe = {
                        showMainSheet = false
                        navController.navigate(RecipeKeeperScreen.CreateRecipe.name)
                    }
                )
            }
        }

        // --- Deuxième Bottom Sheet : ajout dossier ---
        if (showAddFolderSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddFolderSheet = false },
                sheetState = sheetState
            ) {
                AddFolderBottomSheet(
                    onAdd = { folderName ->
                        showAddFolderSheet = false
                        println("Dossier ajouté : $folderName")
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeKeeperTopBar(
    currentScreen: RecipeKeeperScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}
@Composable
fun BottomNavigationBar(
    currentScreen: RecipeKeeperScreen,
    onNavigate: (RecipeKeeperScreen) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == RecipeKeeperScreen.Home,
            onClick = { onNavigate(RecipeKeeperScreen.Home) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Accueil") },
            label = { Text("Accueil") }
        )
        NavigationBarItem(
            selected = currentScreen == RecipeKeeperScreen.CreateRecipe,
            onClick = { onNavigate(RecipeKeeperScreen.CreateRecipe) },
            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Créer") },
            label = { Text("Créer") }
        )
        NavigationBarItem(
            selected = currentScreen == RecipeKeeperScreen.Settings,
            onClick = { onNavigate(RecipeKeeperScreen.Settings) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Paramètres") },
            label = { Text("Paramètres") }
        )
    }
}
@Composable
fun BottomSheetContent(
    onAddFolder: () -> Unit,
    onAddRecipe: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "AJOUTER :", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onAddFolder) {
                Text(stringResource(dossier))
            }
            Button(onClick = onAddRecipe) {
                Text("RECETTE")
            }
        }
    }
}

@Composable
fun AddFolderBottomSheet(
    onAdd: (String) -> Unit
) {
    var folderName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Ajouter un dossier", style = MaterialTheme.typography.titleMedium)

        androidx.compose.material3.OutlinedTextField(
            value = folderName,
            onValueChange = { folderName = it },
            label = { Text("Nom du dossier") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (folderName.isNotBlank()) onAdd(folderName)
            },
            modifier = Modifier.align(androidx.compose.ui.Alignment.End)
        ) {
            Text("Ajouter")
        }
    }
}
