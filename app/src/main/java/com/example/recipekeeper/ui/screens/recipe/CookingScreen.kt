package com.example.recipekeeper.ui.screens.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.recipekeeper.di.factory.CookingViewModelFactory

@Composable
fun CookingScreen(
    recipeId: String,
    cookingFactory: CookingViewModelFactory,
    onFinish: () -> Unit = {}
) {
    val viewModel: CookingViewModel = viewModel(factory = cookingFactory)
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
            Text(text = uiState.error ?: "Une erreur est survenue", textAlign = TextAlign.Center)
        }
    } else {
        uiState.recipe?.let { recipe ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Titre
                Text(
                    text = recipe.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                // Instruction
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Étape ${uiState.currentStep + 1} / ${recipe.instructions.size}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light
                    )
                    Text(
                        text = recipe.instructions[uiState.currentStep],
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }

                // Boutons de navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.previousStep() },
                        enabled = uiState.currentStep > 0
                    ) {
                        Text("Précédent")
                    }
                    Button(
                        onClick = {
                            if (uiState.currentStep < recipe.instructions.lastIndex) {
                                viewModel.nextStep()
                            } else {
                                onFinish()
                            }
                        }
                    ) {
                        Text(
                            if (uiState.currentStep < recipe.instructions.lastIndex) {
                                "Suivant"
                            } else {
                                "Terminer"
                            }
                        )
                    }
                }
            }
        }
    }
}