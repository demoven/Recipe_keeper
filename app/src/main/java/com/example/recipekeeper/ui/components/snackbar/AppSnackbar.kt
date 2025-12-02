package com.example.recipekeeper.ui.components.snackbar

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.recipekeeper.R

@Composable
fun AppSnackbar(hostState: SnackbarHostState) {
    SnackbarHost(
        hostState = hostState,
        modifier =
            Modifier
                .imePadding()
                .padding(dimensionResource(R.dimen.padding_large)),
    ) { data ->
        val customVisuals = data.visuals as? RecipeKeeperSnackbarVisuals
        val type = customVisuals?.type ?: SnackbarType.Info

        val containerColor =
            when (type) {
                SnackbarType.Error -> MaterialTheme.colorScheme.errorContainer
                SnackbarType.Info -> MaterialTheme.colorScheme.primaryContainer
            }
        val contentColor =
            when (type) {
                SnackbarType.Error -> MaterialTheme.colorScheme.onErrorContainer
                SnackbarType.Info -> MaterialTheme.colorScheme.onPrimaryContainer
            }

        Snackbar(
            shape = MaterialTheme.shapes.medium,
            containerColor = containerColor,
            contentColor = contentColor,
            dismissAction = {
                IconButton(onClick = { data.dismiss() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = contentColor,
                    )
                }
            },
            content = {
                Text(
                    text = data.visuals.message,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
        )
    }
}
