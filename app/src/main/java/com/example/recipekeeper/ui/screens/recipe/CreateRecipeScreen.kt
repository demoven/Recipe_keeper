package com.example.recipekeeper.ui.screens.recipe

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipekeeper.R
import com.example.recipekeeper.di.factory.CreateRecipeViewModelFactory

@Composable
fun CreateRecipeScreen(
    createRecipeFactory: CreateRecipeViewModelFactory,
    onRecipeSuccess: () -> Unit,
    folderId: String? = null,
    onSetSaveAction: (() -> Unit) -> Unit
) {
    val createRecipeViewModel: CreateRecipeViewModel = viewModel(factory = createRecipeFactory)
    val uiState by createRecipeViewModel.uiState.collectAsState()
    val scrollState =  rememberScrollState()

    DisposableEffect(Unit) {
        // QUAND ON ARRIVE SUR L'ÉCRAN : On définit l'action de sauvegarde
        onSetSaveAction {
            createRecipeViewModel.saveRecipe(
                onSuccess = onRecipeSuccess,
                onFailure = {},
                folderId = folderId
            )
        }

        // QUAND ON QUITTE L'ÉCRAN : On nettoie (on retire le bouton)
        onDispose {
            onSetSaveAction {} // On passe une fonction vide ou null selon votre implémentation
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .imePadding()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo_open_no_bg),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.image_size_extra_large))
            )
        }
        DescriptionLayout(
            recipeName = uiState.title,
            recipeDescription = uiState.description,
            onRecipeNameChange = { createRecipeViewModel.updateTitle(it) },
            onRecipeDescriptionChange = { createRecipeViewModel.updateDescription(it) },
        )
        Spacer(modifier = Modifier.padding(8.dp))
        // Liste Ingrédients connectée au ViewModel
        ListLayout(
            elements = uiState.ingredients,
            placeholder = "ex: 200g de farine",
            title = "Ingrédients",
            onValueChange = { index, ingredient -> createRecipeViewModel.updateIngredient(index, ingredient) },
            onAdd = { createRecipeViewModel.addIngredient() },
            onRemove = { index -> createRecipeViewModel.removeIngredient(index) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.padding(8.dp))

        // Liste Étapes connectée au ViewModel
        ListLayout(
            elements = uiState.instructions, // Attention au nom (steps vs instructions dans votre uiState)
            placeholder = "Décrire l'étape",
            title = "Étapes",
            numbered = true,
            singleLineTextField = false,
            onValueChange = { index, step -> createRecipeViewModel.updateStep(index, step) },
            onAdd = { createRecipeViewModel.addStep() },
            onRemove = { index -> createRecipeViewModel.removeStep(index) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun DescriptionLayout(
    recipeName: String,
    recipeDescription: String,
    onRecipeNameChange: (String) -> Unit,
    onRecipeDescriptionChange: (String) -> Unit
) {
    Column {
        Text ("Description")
        TextFieldTransparent(
            value = recipeName,
            onValueChange = onRecipeNameChange,
            label = "Titre",
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        TextFieldTransparent(
            value = recipeDescription,
            onValueChange = onRecipeDescriptionChange,
            label = "Description",
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            singleLine = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
    }
}

@Composable
fun ListLayout(
    elements: List<String>, // On reçoit une liste simple, pas Mutable
    placeholder: String,
    title: String,
    onValueChange: (Int, String) -> Unit, // Callback modification
    onAdd: () -> Unit,                    // Callback ajout
    onRemove: (Int) -> Unit,              // Callback suppression
    modifier: Modifier = Modifier,
    numbered: Boolean = false,
    singleLineTextField: Boolean = true,
) {
    // Gestion intelligente du focus : on recrée les focus si la taille de la liste change
    val focusRequesters = remember(elements.size) { List(elements.size) { FocusRequester() } }
    var shouldFocusLast by remember { mutableStateOf(false) }

    // Focus automatique sur le nouvel élément ajouté
    LaunchedEffect(elements.size) {
        if (shouldFocusLast && elements.isNotEmpty()) {
            focusRequesters.last().requestFocus()
            shouldFocusLast = false
        }
    }

    Column(modifier = modifier) {
        Text(title)
        elements.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = if (numbered) Alignment.Top else Alignment.CenterVertically
            ) {
                if (numbered) {
                    Text(
                        text = "${index + 1}.",
                        modifier = Modifier
                            .width(28.dp)
                            .alignBy(FirstBaseline),
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }
                TextFieldTransparent(
                    value = item,
                    onValueChange = { newValue -> onValueChange(index, newValue) },
                    placeholder = placeholder,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = singleLineTextField,
                    modifier = Modifier
                        .weight(1f)
                        .then(if (numbered) Modifier.alignBy(FirstBaseline) else Modifier)
                        .focusRequester(focusRequesters[index])
                )
                Spacer(modifier = Modifier.size(8.dp))
                IconButton(
                    onClick = { onRemove(index) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Supprimer"
                    )
                }
            }
        }

        // Bouton Ajouter
        val canAdd = elements.isEmpty() || elements.last().isNotBlank()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    onAdd()
                    shouldFocusLast = true
                },
                enabled = canAdd,
                modifier = Modifier.padding(top = 16.dp)
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
    modifier : Modifier = Modifier,
    label : String? = null,
    placeholder: String? = null,
    keyboardActions: KeyboardActions? = null,
    singleLine : Boolean = true
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = if(label != null) {
            { Text(text =label) }
        } else null,
        placeholder = if (placeholder != null) {
            { Text(text = placeholder, maxLines = 1, softWrap = false) }
        } else null,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent
        ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions ?: KeyboardActions.Default,
        singleLine = singleLine,
        modifier = modifier
    )
}