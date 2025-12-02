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
import androidx.compose.ui.res.stringResource
import com.example.recipekeeper.R
import com.example.recipekeeper.ui.models.RecipeKeeperScreen

@Composable
fun BottomNavigationBar(
    currentScreen: RecipeKeeperScreen,
    onNavigate: (RecipeKeeperScreen) -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == RecipeKeeperScreen.Home,
            onClick = { onNavigate(RecipeKeeperScreen.Home) },
            icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.home)) },
            label = { Text(stringResource(R.string.home)) },
        )
        NavigationBarItem(
            selected = currentScreen == RecipeKeeperScreen.CreateRecipe,
            onClick = { onNavigate(RecipeKeeperScreen.CreateRecipe) },
            icon = { Icon(Icons.Default.AddCircle, contentDescription = stringResource(R.string.create)) },
            label = { Text(stringResource(R.string.create)) },
        )
        NavigationBarItem(
            selected = currentScreen == RecipeKeeperScreen.Settings,
            onClick = { onNavigate(RecipeKeeperScreen.Settings) },
            icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings)) },
            label = { Text(stringResource(R.string.settings)) },
        )
    }
}
