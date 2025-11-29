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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipekeeper.R
import com.example.recipekeeper.ui.screens.auth.EmailTextField
import com.example.recipekeeper.ui.screens.auth.PasswordTextField

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(),
    onLogout: () -> Unit
) {
    var showPasswordDialogSecurity by remember { mutableStateOf(false) }
    var showPasswordDialogEmail by remember { mutableStateOf(false) }
    var showPasswordDialogDeletion by remember { mutableStateOf(false) }

    if(showPasswordDialogSecurity) {
        PasswordDialog(
            onPasswordConfirmed = { password ->
                showPasswordDialogSecurity = false
            },
            onDismiss = {
                showPasswordDialogSecurity = false
            }
        )
    } else if(showPasswordDialogEmail) {
        PasswordDialog(
            onPasswordConfirmed = { password ->
                showPasswordDialogEmail = false
            },
            onDismiss = {
                showPasswordDialogEmail = false
            }
        )
    } else if(showPasswordDialogDeletion) {
        PasswordDialog(
            onPasswordConfirmed = { password ->
                showPasswordDialogDeletion = false
            },
            onDismiss = {
                showPasswordDialogDeletion = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {

        EmailCard(
            onEmailUpdate = { showPasswordDialogEmail = true }
        )

        SecurityCard(
            onPasswordChange = { showPasswordDialogSecurity = true }
        )

        DeletionAccountCard(
            onDeleteAccount = { showPasswordDialogDeletion = true }
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
                onClick = { onEmailUpdate(email) },
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

            // Champ 2 : Confirmer le mot de passe
            PasswordTextField(
                label = stringResource(R.string.new_password),
                password = newPassword,
                passwordError = false,
                passwordErrorMessage = null,
                onPasswordChanged = { newPassword = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onPasswordChange,
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

@Composable
fun PasswordDialog(
    onPasswordConfirmed: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    AlertDialog(
        modifier = Modifier.fillMaxWidth().padding(dimensionResource(R.dimen.padding_medium)),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        // CORRECTION : Appeler onDismiss directement
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.current_password)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
            ) {
                Text("Entrer votre mot de passe actuel pour confirmer l'action.")
                PasswordTextField(
                    label = stringResource(R.string.current_password),
                    password = password,
                    passwordError = false,
                    passwordErrorMessage = null,
                    onPasswordChanged = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = null
                )
            }
        },
        confirmButton = {
            // CORRECTION : Appeler onPasswordConfirmed avec le mot de passe
            Button(onClick = { onPasswordConfirmed(password) }) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            // CORRECTION : Appeler onDismiss directement
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}