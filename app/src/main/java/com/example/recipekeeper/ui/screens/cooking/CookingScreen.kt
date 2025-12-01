package com.example.recipekeeper.ui.screens.cooking

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipekeeper.R
import com.example.recipekeeper.data.models.Recipe
import com.example.recipekeeper.di.factory.CookingViewModelFactory

@Composable
fun CookingScreen(
    recipeId: String,
    cookingFactory: CookingViewModelFactory,
    onFinish: () -> Unit = {},
) {
    val viewModel: CookingViewModel = viewModel(factory = cookingFactory)
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showIngredientsDialog by remember { mutableStateOf(false) }
    val isListening = remember { mutableStateOf(false) }

    val voiceHelper =
        remember(context) {
            VoiceRecognition(context, isListening).apply {
                onSuivant = {
                    val isLastStep = uiState.currentStep >= (uiState.recipe?.instructions?.lastIndex ?: 0)
                    if (isLastStep) onFinish() else viewModel.nextStep()
                }
                onRetour = {
                    if (uiState.currentStep > 0) viewModel.previousStep()
                }
                onIngredient = { showIngredientsDialog = true }
                onFermer = { showIngredientsDialog = false }
                shouldShowIngredients = { showIngredientsDialog }
            }
        }

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) voiceHelper.startListening()
        }

    DisposableEffect(context) {
        voiceHelper.initialize()
        onDispose { voiceHelper.destroy() }
    }

    LaunchedEffect(recipeId) { viewModel.loadRecipe(recipeId) }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            voiceHelper.startListening()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
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
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            uiState.recipe != null -> {
                CookingContent(
                    recipe = uiState.recipe!!,
                    currentStep = uiState.currentStep,
                    isListening = isListening.value,
                    onNext = {
                        if (uiState.currentStep < (uiState.recipe!!.instructions.lastIndex)) {
                            viewModel.nextStep()
                        } else {
                            onFinish()
                        }
                    },
                    onPrevious = { viewModel.previousStep() },
                    onShowIngredients = { showIngredientsDialog = true },
                )
            }
        }
    }

    if (showIngredientsDialog && uiState.recipe != null) {
        IngredientsDialog(
            ingredients = uiState.recipe!!.ingredients,
            onDismiss = { showIngredientsDialog = false },
        )
    }
}

@Composable
fun CookingContent(
    recipe: Recipe,
    currentStep: Int,
    isListening: Boolean,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onShowIngredients: () -> Unit,
) {
    val totalSteps = recipe.instructions.size
    val progress by animateFloatAsState(
        targetValue = (currentStep + 1).toFloat() / totalSteps.toFloat(),
        label = stringResource(R.string.progress),
    )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // --- En-tête ---
        CookingHeader(
            title = recipe.title,
            isListening = isListening,
            onShowIngredients = onShowIngredients,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Barre de progression ---
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${stringResource(R.string.step)} ${currentStep + 1} / $totalSteps",
                    style = MaterialTheme.typography.labelLarge, // Style standard pour les labels
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "${((currentStep + 1f) / totalSteps * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        InstructionCard(
            instruction = recipe.instructions.getOrNull(currentStep) ?: "",
            modifier = Modifier.weight(1f),
        )

        Spacer(modifier = Modifier.height(32.dp))

        CookingControls(
            isFirstStep = currentStep == 0,
            isLastStep = currentStep == totalSteps - 1,
            onPrevious = onPrevious,
            onNext = onNext,
        )
    }
}

@Composable
fun CookingHeader(
    title: String,
    isListening: Boolean,
    onShowIngredients: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isListening) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = stringResource(R.string.active_listening),
                tint = MaterialTheme.colorScheme.primary,
                modifier =
                    Modifier
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        .padding(4.dp),
            )
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall, // Titre principal
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
            maxLines = 2,
        )

        IconButton(onClick = onShowIngredients) {
            Icon(
                imageVector = Icons.Default.RestaurantMenu,
                contentDescription = stringResource(R.string.ingredients),
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Composable
fun InstructionCard(
    instruction: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = instruction,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun CookingControls(
    isFirstStep: Boolean,
    isLastStep: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Bouton Précédent
        FilledTonalButton(
            onClick = onPrevious,
            enabled = !isFirstStep,
            modifier =
                Modifier
                    .weight(1f)
                    .height(56.dp),
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Text(stringResource(R.string.back), style = MaterialTheme.typography.labelLarge)
                Text(
                    "\"${stringResource(R.string.back)}\"",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                )
            }
        }

        Button(
            onClick = onNext,
            modifier =
                Modifier
                    .weight(1f)
                    .height(56.dp),
            colors =
                if (isLastStep) {
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                } else {
                    ButtonDefaults.buttonColors()
                },
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    if (isLastStep) stringResource(R.string.finish) else stringResource(R.string.next),
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    if (isLastStep) "\"${stringResource(R.string.finish)}\"" else "\"${stringResource(R.string.next)}\"",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                if (isLastStep) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
            )
        }
    }
}

@Composable
fun IngredientsDialog(
    ingredients: List<String>,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.ingredients),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (ingredients.isEmpty()) {
                    Text(
                        stringResource(R.string.no_ingredient_available),
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    )
                } else {
                    ingredients.forEach { ingredient ->
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .size(6.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = ingredient,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "${stringResource(R.string.close)} (\"${stringResource(R.string.close)}\")",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
    )
}
