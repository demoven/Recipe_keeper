package com.example.recipekeeper.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun LoadingIndicator() {
    // La Box agit comme le conteneur parent (BoxScope)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center, // Optionnel: centre tout par défaut
    ) {
        // 1. Le fond (Surface)
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.3f),
        ) {}

        // 2. Le loader
        // Maintenant, Modifier.align est disponible car nous sommes dans une Box
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
        )
    }
}
