package com.example.recipekeeper.ui.components.actions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.recipekeeper.R

@Composable
fun RecipeActions(
    onShowRecipeMenu: () -> Unit,
    isRecipeMenuVisible: Boolean,
    hideRecipeMenu: () -> Unit,
    showModifyDialog: () -> Unit,
    showDeleteDialog: () -> Unit
) {
    IconButton(onClick = { onShowRecipeMenu() }) {
        Icon(Icons.Default.MoreVert, contentDescription = "Options de la recette")
    }
    DropdownMenu(
        expanded = isRecipeMenuVisible,
        onDismissRequest = { hideRecipeMenu() }
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.modify_recipe)) },
            onClick = {
                hideRecipeMenu()
                showModifyDialog()
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.delete_recipe)) },
            onClick = {
                hideRecipeMenu()
                showDeleteDialog()
            }
        )
    }
}