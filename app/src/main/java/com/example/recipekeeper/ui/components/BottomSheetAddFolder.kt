package com.example.recipekeeper.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.recipekeeper.R

@Composable
fun BottomSheetAddFolder(
    onAdd: (String) -> Unit
) {
    var folderName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = stringResource(R.string.add_folder), style = MaterialTheme.typography.titleMedium)

        androidx.compose.material3.OutlinedTextField(
            value = folderName,
            onValueChange = { folderName = it },
            label = { Text(stringResource(R.string.folder_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (folderName.isNotBlank()) onAdd(folderName)
            },
            modifier = Modifier.align(androidx.compose.ui.Alignment.End)
        ) {
            Text(stringResource(R.string.add))
        }
    }
}
