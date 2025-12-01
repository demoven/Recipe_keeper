package com.example.recipekeeper.ui.screens.recipe_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipekeeper.R
import com.example.recipekeeper.data.models.Recipe
import com.example.recipekeeper.di.factory.RecipeDetailViewModelFactory

@Composable
fun RecipeDetailScreen(
    recipeId: String,
    recipeDetailFactory: RecipeDetailViewModelFactory,
    onNavigateToCooking: (String) -> Unit,
) {
    val viewModel: RecipeDetailViewModel = viewModel(factory = recipeDetailFactory)
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.error_loading_recipe),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            uiState.recipe != null -> {
                RecipeDetailContent(
                    recipe = uiState.recipe!!,
                    onNavigateToCooking = onNavigateToCooking,
                )
            }
        }
    }
}

@Composable
fun RecipeDetailContent(
    recipe: Recipe,
    onNavigateToCooking: (String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    top = dimensionResource(R.dimen.padding_medium),
                    start = dimensionResource(R.dimen.padding_medium),
                    end = dimensionResource(R.dimen.padding_medium),
                    bottom = dimensionResource(R.dimen.padding_extra_large),
                ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_large)),
        ) {
            item {
                RecipeStatsRow(recipe)
            }

            if (recipe.description.isNotEmpty()) {
                item {
                    SectionContent(
                        title = "Description",
                    ) {
                        Text(
                            text = recipe.description,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                        )
                    }
                }
            }

            if (recipe.ingredients.isNotEmpty()) {
                item {
                    Text(
                        text = "Ingrédients",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                items(recipe.ingredients) { ingredient ->
                    Row(modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_extra_small))) {
                        Text("• ", fontWeight = FontWeight.Bold)
                        Text(ingredient, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            if (recipe.instructions.isNotEmpty()) {
                item {
                    Text(
                        text = "Instructions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                itemsIndexed(recipe.instructions) { index, instruction ->
                    Row(modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_small))) {
                        Text(
                            text = "${index + 1}. ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(dimensionResource(R.dimen.width_medium)),
                        )
                        Text(
                            text = instruction,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        Button(
            onClick = { onNavigateToCooking(recipe.id) },
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium)),
        ) {
            Text("En Cuisine !", modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_small)))
        }
    }
}

@Composable
fun RecipeStatsRow(
    recipe: Recipe,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_radius_medium)))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(dimensionResource(R.dimen.padding_medium)),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StatItem(
            icon = Icons.Default.AccessTime,
            value = "${recipe.prepTime} ${stringResource(R.string.minutes_abbreviation)}",
            label = stringResource(R.string.prep_label),
        )
        StatItem(
            icon = Icons.Default.Timer,
            value = "${recipe.cookTime} ${stringResource(R.string.minutes_abbreviation)}",
            label = stringResource(R.string.cooking_label),
        )
        StatItem(
            icon = Icons.Default.RestaurantMenu,
            value = "${recipe.servings} ${stringResource(R.string.persons_abbreviation)}",
            label = stringResource(R.string.portions_label),
        )
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(dimensionResource(R.dimen.padding_large)),
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.height_extra_small)))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun SectionContent(
    title: String,
    content: @Composable () -> Unit,
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.height_small)))
        content()
    }
}
