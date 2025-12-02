package com.example.recipekeeper.ui.components.options

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
fun FolderDropDownOptions(
    onShowFolderMenu: () -> Unit,
    isFolderMenuVisible: Boolean,
    hideFolderMenu: () -> Unit,
    showRenameDialog: () -> Unit,
    showDeleteDialog: () -> Unit,
) {
    IconButton(onClick = { onShowFolderMenu() }) {
        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.folder_options))
    }
    DropdownMenu(
        expanded = isFolderMenuVisible,
        onDismissRequest = { hideFolderMenu() },
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.rename_folder)) },
            onClick = {
                hideFolderMenu()
                showRenameDialog()
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.delete_folder)) },
            onClick = {
                hideFolderMenu()
                showDeleteDialog()
            },
        )
    }
}
