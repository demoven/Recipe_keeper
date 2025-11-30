package com.example.recipekeeper.ui.screens.cooking

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
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
    val context = LocalContext.current

    var showIngredientsDialog by remember { mutableStateOf(false) }
    val isListeningRef = remember { mutableStateOf(false) }

    val voiceHelper = remember(context) {
        VoiceRecognition(context, isListeningRef).apply {
            onSuivant = {
                val isLastStep = uiState.currentStep >= (uiState.recipe?.instructions?.lastIndex ?: 0)
                if (isLastStep) {
                    onFinish()
                } else {
                    viewModel.nextStep()
                }
            }
            onRetour = {
                if (uiState.currentStep > 0) {
                    viewModel.previousStep()
                }
            }
            onIngredient = {
                showIngredientsDialog = true
            }
            onFermer = {
                showIngredientsDialog = false
            }
            shouldShowIngredients = { showIngredientsDialog }
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            voiceHelper.startListening()
        }
    }

    DisposableEffect(context) {
        voiceHelper.initialize()
        onDispose {
            voiceHelper.destroy()
        }
    }

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED -> {
                voiceHelper.startListening()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(48.dp))
                    Text(
                        text = recipe.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            showIngredientsDialog = true
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Commande vocale",
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = "Voir les ingrédients",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.previousStep() },
                        enabled = uiState.currentStep > 0
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Commande vocale",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Retour")
                        }
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Commande vocale",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
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

            if (showIngredientsDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showIngredientsDialog = false
                    },
                    title = {
                        Text(
                            text = "Ingrédients",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                        ) {
                            if (recipe.ingredients.isEmpty()) {
                                Text(
                                    text = "Aucun ingrédient disponible",
                                    fontSize = 16.sp,
                                    color = androidx.compose.ui.graphics.Color.Gray
                                )
                            } else {
                                recipe.ingredients.forEachIndexed { index, ingredient ->
                                    Text(
                                        text = "• $ingredient",
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                    if (index < recipe.ingredients.lastIndex) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showIngredientsDialog = false
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Commande vocale",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text("Fermer")
                            }
                        }
                    }
                )
            }
        }
    }
}