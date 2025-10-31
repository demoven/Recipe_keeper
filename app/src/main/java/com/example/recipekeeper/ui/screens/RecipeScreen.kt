package com.example.recipekeeper.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.recipekeeper.R

@Composable
fun CreateRecipeScreen() {
    var recipeName by remember { mutableStateOf("") }
    var recipeDescription by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.logo_open_no_bg),
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen.image_size_extra_large))
        )

        DescriptionLayout(
            recipeName = recipeName,
            recipeDescription = recipeDescription,
            onRecipeNameChange = { recipeName = it },
            onRecipeDescriptionChange = { recipeDescription = it }
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        IngredientsLayout()
    }
}

// Ajoutez ce data class (en haut du fichier par exemple)
data class IngredientUiState(
    val quantity: String = "",
    val unit: String = "",
    val name: String = ""
)

@Composable
fun IngredientsLayout() {
    val ingredients = remember { mutableStateListOf(IngredientUiState()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        ingredients.forEachIndexed { index, ing ->
            IngredientField(
                quantity = ing.quantity,
                onQuantityChange = { q ->
                    ingredients[index] = ing.copy(quantity = q)
                },
                unit = ing.unit,
                onUnitChange = { u ->
                    ingredients[index] = ing.copy(unit = u)
                },
                ingredientName = ing.name,
                onIngredientNameChange = { n ->
                    ingredients[index] = ing.copy(name = n)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { ingredients.add(IngredientUiState()) },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("+")
            }
        }
    }
}
@Composable
fun IngredientField(
    quantity: String,
    onQuantityChange: (String) -> Unit,
    unit: String,
    onUnitChange: (String) -> Unit,
    ingredientName: String,
    onIngredientNameChange: (String) -> Unit,
    modifier : Modifier = Modifier
) {
    Row (
        modifier = modifier
    ) {
        TextFieldTransparent(
            value = quantity.toString(),
            onValueChange = onQuantityChange,
            placeholder = "Quantité",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.weight(1f)
        )
        TextFieldTransparent(
            value = unit,
            onValueChange = onUnitChange,
            placeholder = "Unité",
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.weight(1f)
        )
        TextFieldTransparent(
            value = ingredientName,
            onValueChange = onIngredientNameChange,
            placeholder = "Ingrédient",
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.weight(2f)
        )
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
    keyboardActions: KeyboardActions? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = if(label != null) {
            { Text(text =label) }
        } else null,
        placeholder = {
            if (placeholder != null) Text(
                text = placeholder,
                maxLines = 1,
                softWrap = false
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent
        ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions ?: KeyboardActions.Default,
        singleLine = true, //TODO changer pour les descriptions
        modifier = modifier
    )
}

