package com.example.recipekeeper.ui.screens.recipe

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipekeeper.di.factory.RecipeDetailViewModelFactory
import com.example.recipekeeper.ui.models.RecipeKeeperScreen

@Composable
fun RecipeDetailScreen(
    recipeId: String,
    recipeDetailFactory: RecipeDetailViewModelFactory,
    onNavigateToCooking: (String) -> Unit
) {
    val viewModel: RecipeDetailViewModel = viewModel(factory = recipeDetailFactory)
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (uiState.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = uiState.error ?: "Une erreur est survenue")
        }
    } else {
        uiState.recipe?.let { recipe ->
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = recipe.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        if (recipe.description.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Description :", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(recipe.description, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    if (recipe.ingredients.isNotEmpty()) {
                        item {
                            Text("Ingrédients", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(recipe.ingredients) { ingredient ->
                            Text("- $ingredient", modifier = Modifier.padding(bottom = 4.dp))
                        }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }

                    if (recipe.instructions.isNotEmpty()) {
                        item {
                            Text("Instructions", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        itemsIndexed(recipe.instructions) { index, instruction ->
                            Text("${index + 1}. $instruction", modifier = Modifier.padding(bottom = 8.dp))
                        }
                    }
                }

                Button(
                    onClick = { onNavigateToCooking(recipe.id) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                ) {
                    Text("En Cuisine !")
                }
            }
        }
    }
}