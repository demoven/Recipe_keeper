package com.example.recipekeeper.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column // Import nécessaire
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items // Import pour itérer sur la liste dans LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button // Import pour les boutons
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipekeeper.R
import com.example.recipekeeper.data.models.Folder
import com.example.recipekeeper.di.factory.HomeViewModelFactory
import com.example.recipekeeper.ui.components.CardField
import com.example.recipekeeper.ui.components.SectionTitle

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

    // On utilise une Column pour placer la LazyRow au-dessus de la LazyVerticalGrid
    Column(modifier = modifier) {
        FoldersLayout(
            folders = uiState.folders,
            onNavigateToSubFolder = onNavigateToSubFolder
        )

        // --- Grille des recettes ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            // On utilise weight(1f) pour que la grille prenne tout l'espace restant en hauteur
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Section Title ---
            if (uiState.recipes.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    SectionTitle(title = stringResource(R.string.recipes))
                }
            }

            // --- Recipes Cards ---
            items(
                items = uiState.recipes,
                key = { it.id }
            ) { recipe ->
                CardField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToRecipeDetails(recipe.id) },
                    title = {
                        Text(
                            text = recipe.title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 40.dp)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun FoldersLayout(
    folders: List<Folder>,
    onNavigateToSubFolder: (String, String) -> Unit,
) {
    if (folders.isNotEmpty()) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items = folders, key = { it.id }) { folder ->
                FolderButton(
                    folderName = folder.name,
                    onNavigateToSubFolder = {
                        onNavigateToSubFolder(folder.id, folder.name)
                    }
                )
            }
        }
    }
}

@Composable
fun FolderButton(
    folderName: String,
    onNavigateToSubFolder: ()-> Unit
) {
    Button(
        onClick = onNavigateToSubFolder
    ) {
        Text(text = folderName)
    }
}