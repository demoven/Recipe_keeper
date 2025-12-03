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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
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
                onIngredient = { viewModel.openIngredientsDialog() }
                onFermer = { viewModel.closeIngredientsDialog() }
                shouldShowIngredients = { uiState.showIngredientsDialog }
            }
        }

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                voiceHelper.startListening()
                viewModel.openVoiceInstructions()
            }
        }

    DisposableEffect(context) {
        voiceHelper.initialize()
        onDispose { voiceHelper.destroy() }
    }

    LaunchedEffect(recipeId) { viewModel.loadRecipe(recipeId) }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            voiceHelper.startListening()
            viewModel.openVoiceInstructions()
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
                    onShowIngredients = { viewModel.openIngredientsDialog() },
                )
            }
        }
    }

    if (uiState.showIngredientsDialog && uiState.recipe != null) {
        IngredientsDialog(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
            ingredients = uiState.recipe!!.ingredients,
            onDismiss = { viewModel.closeIngredientsDialog() },
        )
    }

    if (uiState.showVoiceInstructions) {
        VoiceInstructionsDialog(
            onDismiss = { viewModel.closeVoiceInstructions() },
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
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
                .padding(dimensionResource(id = R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // --- En-tête ---
        CookingHeader(
            isListening = isListening,
            onShowIngredients = onShowIngredients,
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.space_large)))

        // --- Barre de progression ---
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${stringResource(R.string.step)} ${currentStep + 1} / $totalSteps",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "${((currentStep + 1f) / totalSteps * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.height_small)))
            LinearProgressIndicator(
                progress = { progress },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.height_small))
                        .clip(CircleShape),
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.space_extra_large)))

        InstructionCard(
            instruction = recipe.instructions.getOrNull(currentStep) ?: "",
            modifier = Modifier.weight(1f),
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.space_extra_large)))

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
                        .size(dimensionResource(id = R.dimen.size_icon_small))
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        .padding(dimensionResource(id = R.dimen.padding_extra_small)),
            )
        } else {
            Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.size_icon_small)))
        }

        FilledTonalButton(onClick = onShowIngredients) {
            Icon(
                imageVector = Icons.Default.RestaurantMenu,
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(id = R.dimen.size_icon_small)),
            )
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.space_small)))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(R.string.ingredients),
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = "\"${stringResource(R.string.ingredients)}\"",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                )
            }
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
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.elevation_card_small)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(id = R.dimen.padding_large)),
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
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.space_medium)),
    ) {
        // Bouton Précédent
        FilledTonalButton(
            onClick = onPrevious,
            enabled = !isFirstStep,
            modifier =
                Modifier
                    .weight(1f)
                    .height(dimensionResource(id = R.dimen.height_button_large)),
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.space_small)))
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
                    .height(dimensionResource(id = R.dimen.height_button_large)),
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
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.space_small)))
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
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        properties = DialogProperties(usePlatformDefaultWidth = false),
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
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    )
                } else {
                    ingredients.forEach { ingredient ->
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = dimensionResource(id = R.dimen.space_small)),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .size(dimensionResource(id = R.dimen.size_dot_small))
                                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                            )
                            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.space_medium)))
                            Text(
                                text = ingredient,
                                style = MaterialTheme.typography.titleLarge,
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

@Composable
fun VoiceInstructionsDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = stringResource(R.string.voice_control),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))) {
                Text(
                    text = stringResource(R.string.voice_controle_next),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = stringResource(R.string.voice_controle_back),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = stringResource(R.string.voice_control_ingredients),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = stringResource(R.string.voice_control_close_ingredients),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text(stringResource(R.string.understood))
            }
        },
    )
}
