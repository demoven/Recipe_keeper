package com.example.recipekeeper

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.recipekeeper.ui.theme.HomeScreen
import com.example.recipekeeper.ui.theme.AccountScreen
import com.example.recipekeeper.ui.theme.CreateRecipeScreen
import com.example.recipekeeper.ui.theme.SettingsScreen
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

enum class RecipeKeeperScreen(@StringRes val title: Int) {
    Home(title = R.string.app_name),
    Account(title = R.string.app_name),
    CreateRecipe(title = R.string.app_name),
    Settings(title = R.string.app_name)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeKeeperApp(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentScreen = RecipeKeeperScreen.entries.find { it.name == currentRoute }
        ?: RecipeKeeperScreen.Home

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            RecipeKeeperTopBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentScreen = currentScreen,
                onNavigate = {  screen ->
                    if (screen == RecipeKeeperScreen.CreateRecipe) {
                        coroutineScope.launch { showSheet = true }
                    } else {
                        navController.navigate(screen.name)
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = RecipeKeeperScreen.Home.name,
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
                SettingsScreen(onNavigateToAccount = { navController.navigate(RecipeKeeperScreen.Account.name) })
            }

            composable("addFolder") { Text("Écran : Ajouter un dossier") }
            composable("addRecipe") {
                CreateRecipeScreen()
            }
        }
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState
            ) {
                BottomSheetContent(
                    onAddFolder = {
                        showSheet = false
                        navController.navigate("addFolder")
                    },
                    onAddRecipe = {
                        showSheet = false
                        navController.navigate("addRecipe")
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
                Text("DOSSIER 📁 ")
            }
            Button(onClick = onAddRecipe) {
                Text("RECETTE")
            }
        }
    }
}

