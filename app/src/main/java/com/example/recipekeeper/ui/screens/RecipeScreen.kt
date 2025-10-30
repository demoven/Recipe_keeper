package com.example.recipekeeper.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
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
            recipeName = recipeName,
            onRecipeNameChange = onRecipeNameChange,
            label = "Titre",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        TextFieldTransparent(
            recipeName = recipeDescription,
            onRecipeNameChange = onRecipeDescriptionChange,
            label = "Description",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
    }
}

@Composable
fun TextFieldTransparent(
    recipeName: String,
    onRecipeNameChange: (String) -> Unit,
    label : String,
    modifier : Modifier = Modifier
) {
    TextField(
        value = recipeName,
        onValueChange = onRecipeNameChange,
        label = { Text(label) },
        modifier = modifier,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent
        ),
    )
}