package com.example.recipekeeper

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

// --- Énumération des écrans ---
enum class RecipeKeeperScreen(@StringRes val title: Int) {
    Home(title = R.string.app_name),
    Account(title = R.string.app_name),
    CreateRecipe(title = R.string.app_name),
    Settings(title = R.string.app_name)
}

// --- Barre du haut ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeKeeperAppBar(
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

// --- Application principale ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeKeeperApp(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = RecipeKeeperScreen.valueOf(
        backStackEntry?.destination?.route ?: RecipeKeeperScreen.Home.name
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(currentScreen.title)) }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentScreen = currentScreen,
                onNavigate = { navController.navigate(it.name) }
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
        }
    }
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







