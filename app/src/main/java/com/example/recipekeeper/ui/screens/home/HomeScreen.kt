package com.example.recipekeeper.ui.screens.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipekeeper.R
import com.example.recipekeeper.data.factory.HomeViewModelFactory
import com.example.recipekeeper.data.models.Folder
import com.example.recipekeeper.data.models.Recipe
import com.example.recipekeeper.data.repository.RecipeRepository
import com.example.recipekeeper.ui.sharedcomposable.CardField
import com.example.recipekeeper.ui.sharedcomposable.SectionTitle

@Composable
fun HomeScreen(
    onNavigateToSubFolder: (String) -> Unit,
    onNavigateToRecipeDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
    folderId: String? = null
) {
    // Charger les données avec le folderId
    val factory = HomeViewModelFactory(RecipeRepository())
    val homeViewModel: HomeViewModel = viewModel(factory = factory)
    val uiState by homeViewModel.uiState.collectAsState()

    LaunchedEffect(folderId) {
        homeViewModel.getFolders(folderId)
        homeViewModel.getRecipes(folderId)
    }

    Log.d("HomeScreen", "UI State folders: ${uiState.folders}")
    Log.d("HomeScreen", "UI State recipes: ${uiState.recipes}")
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (uiState.folders.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle(title = stringResource(R.string.folders))
            }
            items(
                items = uiState.folders,
                key = { it.id }
            ) { folder ->
                CardField(
                    title = folder.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToSubFolder(folder.id) }
                )
            }
        }

        if (uiState.recipes.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle(title = stringResource(R.string.recipes))
            }
            items(
                items = uiState.recipes,
                key = { it.id }
            ) { recipe ->
                CardField(
                    title = recipe.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToRecipeDetails(recipe.id) }
                )
            }
        }
    }
}