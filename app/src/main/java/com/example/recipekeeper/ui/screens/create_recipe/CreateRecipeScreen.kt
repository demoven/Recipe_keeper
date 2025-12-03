package com.example.recipekeeper.ui.screens.create_recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipekeeper.R
import com.example.recipekeeper.di.factory.CreateRecipeViewModelFactory
import com.example.recipekeeper.ui.components.LoadingIndicator
import com.example.recipekeeper.ui.components.SectionTitle

@Composable
fun CreateRecipeScreen(
    createRecipeFactory: CreateRecipeViewModelFactory,
    onRecipeSuccess: (String, String) -> Unit,
    folderId: String? = null,
    recipeId: String? = null,
    onSetSaveAction: (() -> Unit) -> Unit,
) {
    val createRecipeViewModel: CreateRecipeViewModel = viewModel(factory = createRecipeFactory)
    val uiState by createRecipeViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(recipeId) {
        if (recipeId != null) {
            createRecipeViewModel.getRecipeById(recipeId)
        } else {
            createRecipeViewModel.resetState()
        }
    }

    DisposableEffect(Unit) {
        onSetSaveAction {
            if (recipeId != null) {
                createRecipeViewModel.updateRecipe(
                    recipeId = recipeId,
                    onSuccess = onRecipeSuccess,
                )
            } else {
                createRecipeViewModel.saveRecipe(
                    onSuccess = onRecipeSuccess,
                    onFailure = {},
                    folderId = folderId,
                )
            }
        }

        onDispose {
            onSetSaveAction {}
        }
    }

    if (uiState.isLoading) {
        LoadingIndicator()
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
                .padding(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_large)),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.logo_open_no_bg),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.image_size_extra_large)),
            )
        }
        DescriptionLayout(
            recipeName = uiState.title,
            recipeDescription = uiState.description,
            onRecipeNameChange = { createRecipeViewModel.updateTitle(it) },
            onRecipeDescriptionChange = { createRecipeViewModel.updateDescription(it) },
        )
        TextFieldTransparent(
            value = uiState.prepTime.toString(),
            onValueChange = { createRecipeViewModel.updatePrepTime(it) },
            label = stringResource(R.string.preparation_time_label),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
            modifier = Modifier.fillMaxWidth(),
        )
        TextFieldTransparent(
            value = uiState.cookTime.toString(),
            onValueChange = { createRecipeViewModel.updateCookTime(it) },
            label = stringResource(R.string.cook_time_label),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
            modifier = Modifier.fillMaxWidth(),
        )
        TextFieldTransparent(
            value = uiState.servings.toString(),
            onValueChange = { createRecipeViewModel.updateServings(it) },
            label = stringResource(R.string.servings_label),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
            modifier = Modifier.fillMaxWidth(),
        )
        ListLayout(
            elements = uiState.ingredients,
            placeholder = stringResource(R.string.ingredients_example),
            title = "Ingrédients",
            onValueChange = { index, ingredient -> createRecipeViewModel.updateIngredient(index, ingredient) },
            onAdd = { createRecipeViewModel.addIngredient() },
            onRemove = { index -> createRecipeViewModel.removeIngredient(index) },
            modifier = Modifier.fillMaxWidth(),
            onJumpToNextSection = {
                createRecipeViewModel.addStepIfEmpty()
            },
        )
        ListLayout(
            elements = uiState.instructions,
            placeholder = stringResource(R.string.describe_step),
            title = stringResource(R.string.steps),
            numbered = true,
            singleLineTextField = false,
            onValueChange = { index, step -> createRecipeViewModel.updateStep(index, step) },
            onAdd = { createRecipeViewModel.addStep() },
            onRemove = { index -> createRecipeViewModel.removeStep(index) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun DescriptionLayout(
    recipeName: String,
    recipeDescription: String,
    onRecipeNameChange: (String) -> Unit,
    onRecipeDescriptionChange: (String) -> Unit,
) {
    Column {
        SectionTitle(
            title = stringResource(R.string.description),
        )
        TextFieldTransparent(
            value = recipeName,
            onValueChange = onRecipeNameChange,
            label = "Titre",
            keyboardOptions =
                KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(R.dimen.padding_medium)),
        )
        TextFieldTransparent(
            value = recipeDescription,
            onValueChange = onRecipeDescriptionChange,
            label = "Description",
            keyboardOptions =
                KeyboardOptions(
                    imeAction = ImeAction.Next,
                ),
            singleLine = false,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(R.dimen.padding_medium)),
        )
    }
}

@Composable
fun ListLayout(
    elements: List<String>,
    placeholder: String,
    title: String,
    onValueChange: (Int, String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (Int) -> Unit,
    modifier: Modifier = Modifier,
    numbered: Boolean = false,
    singleLineTextField: Boolean = true,
    onJumpToNextSection: (() -> Unit)? = null,
) {
    val focusRequesters = remember(elements.size) { List(elements.size) { FocusRequester() } }
    var shouldFocusLast by remember { mutableStateOf(false) }

    LaunchedEffect(elements.size) {
        if (shouldFocusLast && elements.isNotEmpty()) {
            focusRequesters.last().requestFocus()
            shouldFocusLast = false
        }
    }

    Column(modifier = modifier) {
        SectionTitle(title = title)
        elements.forEachIndexed { index, item ->
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(R.dimen.padding_small)),
                verticalAlignment = if (numbered) Alignment.Top else Alignment.CenterVertically,
            ) {
                if (numbered) {
                    Text(
                        text = "${index + 1}.",
                        modifier =
                            Modifier
                                .width(dimensionResource(R.dimen.width_medium))
                                .alignBy(FirstBaseline),
                        textAlign = TextAlign.End,
                    )
                    Spacer(modifier = Modifier.size(dimensionResource(R.dimen.size_small)))
                }
                TextFieldTransparent(
                    value = item,
                    onValueChange = { newValue -> onValueChange(index, newValue) },
                    placeholder = placeholder,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                        ),
                    keyboardActions =
                        KeyboardActions(
                            onNext = {
                                if (item.isBlank()) {
                                    onJumpToNextSection?.invoke()
                                    return@KeyboardActions
                                }

                                if (index == elements.lastIndex) {
                                    onAdd()
                                    shouldFocusLast = true
                                } else {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            },
                        ),
                    singleLine = singleLineTextField,
                    modifier =
                        Modifier
                            .weight(1f)
                            .then(if (numbered) Modifier.alignBy(FirstBaseline) else Modifier)
                            .focusRequester(focusRequesters[index]),
                )

                Spacer(modifier = Modifier.size(dimensionResource(R.dimen.size_small)))
                IconButton(
                    onClick = { onRemove(index) },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.delete),
                    )
                }
            }
        }

        val canAdd = elements.isEmpty() || elements.last().isNotBlank()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(
                onClick = {
                    onAdd()
                    shouldFocusLast = true
                },
                enabled = canAdd,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium)),
            ) {
                Text("+")
            }
        }
    }
}

@Composable
fun TextFieldTransparent(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    keyboardActions: KeyboardActions? = null,
    singleLine: Boolean = true,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label =
            if (label != null) {
                { Text(text = label, style = MaterialTheme.typography.labelLarge) }
            } else {
                null
            },
        placeholder =
            if (placeholder != null) {
                { Text(text = placeholder, maxLines = 1, softWrap = false, style = MaterialTheme.typography.labelLarge) }
            } else {
                null
            },
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
            ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions ?: KeyboardActions.Default,
        singleLine = singleLine,
        modifier = modifier,
    )
}
