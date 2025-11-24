package com.example.recipekeeper.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipekeeper.R
import com.example.recipekeeper.di.factory.HomeViewModelFactory
import com.example.recipekeeper.ui.components.CardField
import com.example.recipekeeper.ui.components.SectionTitle
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color

@Composable
fun HomeScreen(
    onNavigateToSubFolder: (String, String) -> Unit,
    onNavigateToRecipeDetails: (String) -> Unit,
    homeFactory: HomeViewModelFactory,
    modifier: Modifier = Modifier,
    folderId: String? = null
) {
    val homeViewModel: HomeViewModel = viewModel(factory = homeFactory)
    val uiState by homeViewModel.uiState.collectAsState()

    LaunchedEffect(folderId) {
        homeViewModel.loadData(folderId)
    }

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
                        .clickable { onNavigateToSubFolder(folder.id, folder.name) }
                )
            }

            // Divider entre Folders et Recipes
            if (uiState.recipes.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Divider(
                        color = Color(0xFFE0E0E0),
                        thickness = 1.dp,
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .fillMaxWidth()
                    )
                }
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

@Composable
fun FolderChips(
    folders: List<String>,
    selectedFolder: String?,
    onFolderSelected: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        folders.forEach { folder ->
            val isSelected = folder == selectedFolder
            Surface(
                color = if (isSelected) Color.Black else Color.White,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier
                    .clickable { onFolderSelected(folder) }
            ) {
                Text(
                    text = folder,
                    color = if (isSelected) Color.White else Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

