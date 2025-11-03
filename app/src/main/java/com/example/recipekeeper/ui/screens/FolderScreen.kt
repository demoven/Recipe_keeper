package com.example.recipekeeper.ui.screens

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.recipekeeper.data.models.Folder
import com.example.recipekeeper.data.models.Recipe
import com.example.recipekeeper.R
import com.example.recipekeeper.ui.sharedcomposable.CardField
import com.example.recipekeeper.ui.sharedcomposable.SectionTitle

@Composable
fun FolderScreen(
    folder: Folder,
    onNavigateToSubFolder: (Folder) -> Unit,
    onNavigateToRecipeDetails: (Recipe) -> Unit,
    ) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (folder.subFolders.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle(title = stringResource(R.string.folders))
            }
            items(
                items = folder.subFolders,
                key = { it.id }
            ) { folder ->
                CardField(
                    title = folder.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToSubFolder(folder) }
                )
            }
        }

        if (folder.recipes.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle(title = stringResource(R.string.recipes))
            }
            items(
                items = folder.recipes,
                key = { it.id }
            ) { recipe ->
                CardField(
                    title = recipe.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToRecipeDetails(recipe) }
                )
            }
        }
    }
}