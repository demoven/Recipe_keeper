package com.example.recipekeeper.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.recipekeeper.R
import com.example.recipekeeper.ui.screens.auth.EmailTextField
import com.example.recipekeeper.ui.screens.auth.PasswordTextField

@Composable
fun SettingsScreen(
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {

        EmailCard(
            onEmailUpdate = { /* TODO */ }
        )

        SecurityCard(
            onPasswordChange = { /* TODO */ }
        )

        DeletionAccountCard(
            onDeleteAccount = { /* TODO */ }
        )

        SectionLogout(
            onLogout = onLogout
        )
    }
}

@Composable
fun EmailCard(
    onEmailUpdate: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    Card {
        Column (
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            Text(
                text = stringResource(R.string.account).uppercase(),
                style = MaterialTheme.typography.titleMedium,
            )

            PasswordTextField(
                label = stringResource(R.string.current_password),
                password = currentPassword,
                passwordError = false,
                passwordErrorMessage = null,
                onPasswordChanged = {},
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            EmailTextField(
                email = email,
                emailError = false,
                onEmailChanged = { email = it },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = null
            )

            Button(
                onClick = { onEmailUpdate },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.update_email))
            }

        }
    }
}

@Composable
fun SecurityCard(
    onPasswordChange: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    Card {
        Column(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            // --- Section Mot de passe ---
            Text(
                text = stringResource(R.string.security).uppercase(),
                style = MaterialTheme.typography.titleMedium,
            )

            // Champ 1 : Nouveau mot de passe
            PasswordTextField(
                label = stringResource(R.string.current_password),
                password = newPassword,
                passwordError = false,
                passwordErrorMessage = null,
                onPasswordChanged = { newPassword = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            // Champ 2 : Confirmer le mot de passe
            PasswordTextField(
                label = stringResource(R.string.new_password),
                password = confirmNewPassword,
                passwordError = false,
                passwordErrorMessage = null,
                onPasswordChanged = { confirmNewPassword = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { onPasswordChange },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.update_password))
            }
        }
    }
}

@Composable
fun DeletionAccountCard(
    onDeleteAccount: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            Text(
                text = stringResource(R.string.deletion_account).uppercase(),
                style = MaterialTheme.typography.titleMedium,
            )

            Text(
                text = stringResource(R.string.deletion_account_confirmation),
                style = MaterialTheme.typography.bodyLarge
            )

            OutlinedButton(
                onClick = onDeleteAccount,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.delete_account))
            }
        }
    }
}

@Composable
fun SectionLogout(
    onLogout: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        TextButton(
            onClick = onLogout
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null
            )
            Spacer(modifier = Modifier.padding(start = 8.dp))
            Text(
                text = stringResource(R.string.logout),
                style = MaterialTheme.typography.titleLarge
                )
        }
    }
}
