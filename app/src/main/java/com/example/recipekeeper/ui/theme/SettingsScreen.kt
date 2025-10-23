package com.example.recipekeeper.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.Button

@Composable
fun SettingsScreen(
    onNavigateToAccount: () -> Unit
) {
    Column {
        Text("Paramètres")
        Button(onClick = onNavigateToAccount) {
            Text("Mon compte")
        }
    }
}