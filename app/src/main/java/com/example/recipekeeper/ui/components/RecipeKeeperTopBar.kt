package com.example.recipekeeper.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.recipekeeper.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeKeeperTopBar(
    title: @Composable () -> Unit, // <-- changer ici
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    onSaveAction: (() -> Unit)? = null
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        actions = {
            if (onSaveAction != null) {
                IconButton(onClick = onSaveAction) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Sauvegarder"
                    )
                }
            }
        }
    )
}