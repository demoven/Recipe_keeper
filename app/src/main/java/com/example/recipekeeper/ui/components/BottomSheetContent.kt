package com.example.recipekeeper.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.recipekeeper.R.string.dossier

@Composable
fun BottomSheetContent(
    onAddFolder: () -> Unit,
    onAddRecipe: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "AJOUTER :", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onAddFolder) {
                Text(stringResource(dossier))
            }
            Button(onClick = onAddRecipe) {
                Text("RECETTE")
            }
        }
    }
}