package com.example.recipekeeper.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.recipekeeper.RecipeKeeperScreen

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