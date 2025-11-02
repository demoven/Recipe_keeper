package com.example.recipekeeper.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.recipekeeper.R
import com.example.recipekeeper.data.models.Folder
import com.example.recipekeeper.data.models.Recipe
import com.example.recipekeeper.data.models.UserRecipes
@Composable
fun HomeScreen(
    userRecipes: UserRecipes,
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
        if (userRecipes.folders.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle(title = stringResource(R.string.folders))
            }
            items(
                items = userRecipes.folders,
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

        if (userRecipes.recipes.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle(title = stringResource(R.string.recipes))
            }
            items(
                items = userRecipes.recipes,
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

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun CardField(
    title: String,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(8.dp)

    Card(
        modifier = modifier,
        shape = cardShape
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(R.drawable.logo_open_no_bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )
            Text(
                text = title,
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 12.dp)
            )
        }
    }
}