package com.example.recipekeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.recipekeeper.ui.theme.RecipeKeeperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeKeeperTheme {
                val appContainer = (application as RecipeKeeperApplication).container
                RecipeKeeperApp(appContainer = appContainer)
            }
        }
    }
}
