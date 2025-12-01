package com.example.recipekeeper.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipekeeper.R
import com.example.recipekeeper.data.models.Folder
import com.example.recipekeeper.data.models.Recipe
import com.example.recipekeeper.di.factory.HomeViewModelFactory
import com.example.recipekeeper.ui.components.RecipeCard
import com.example.recipekeeper.ui.components.SectionTitle

@Composable
fun HomeScreen(
    onNavigateToSubFolder: (String, String) -> Unit,
    onNavigateToRecipeDetails: (String, String) -> Unit,
    homeFactory: HomeViewModelFactory,
    folderId: String? = null,
) {
    val homeViewModel: HomeViewModel = viewModel(factory = homeFactory)
    val uiState by homeViewModel.uiState.collectAsState()

    LaunchedEffect(folderId) {
        homeViewModel.loadData(folderId)
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
    ) {
        if (uiState.folders.isNotEmpty()) {
            FoldersLayout(
                folders = uiState.folders,
                onNavigateToSubFolder = onNavigateToSubFolder,
                modifier = Modifier.fillMaxWidth(),
            )
            HorizontalDivider(
                modifier =
                    Modifier
                        .fillMaxWidth(),
            )
        }
        CardsLayout(
            recipes = uiState.recipes,
            onNavigateToRecipeDetails = onNavigateToRecipeDetails,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun FoldersLayout(
    folders: List<Folder>,
    onNavigateToSubFolder: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        SectionTitle(
            title = stringResource(R.string.folders),
        )
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_extra_small)))

        if (folders.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(items = folders, key = { it.id }) { folder ->
                    FolderButton(
                        folderName = folder.name,
                        onNavigateToSubFolder = {
                            onNavigateToSubFolder(folder.id, folder.name)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun CardsLayout(
    recipes: List<Recipe>,
    onNavigateToRecipeDetails: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
    ) {
        if (recipes.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle(title = stringResource(R.string.recipes))
            }
        }

        items(
            items = recipes,
            key = { it.id },
        ) { recipe ->
            RecipeCard(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToRecipeDetails(recipe.id, recipe.title) },
                title = recipe.title,
            )
        }
    }
}

@Composable
fun FolderButton(
    folderName: String,
    onNavigateToSubFolder: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onNavigateToSubFolder,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small)),
        )
        Text(text = folderName.replaceFirstChar { it.uppercase() })
    }
}
