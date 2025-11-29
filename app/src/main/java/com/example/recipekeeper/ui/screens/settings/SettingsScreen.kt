package com.example.recipekeeper.ui.screens.settings

import android.widget.Toast
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val uiState by settingsViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.emailUpdateError, uiState.emailUpdateSuccess, uiState.emailAlreadyExists) {
        if (uiState.emailUpdateError) {
            Toast.makeText(context, "erreur lors de la mise à jour de l'email", Toast.LENGTH_LONG).show()
            settingsViewModel.updateEmailUpdateError(false)
        } else if (uiState.emailUpdateSuccess) {
            Toast.makeText(context, "email de vérification envoyé", Toast.LENGTH_LONG).show()
            settingsViewModel.updateEmailUpdateSuccess(false)
        } else if (uiState.emailAlreadyExists) {
            Toast.makeText(context, "Email actuel", Toast.LENGTH_LONG).show()
            settingsViewModel.updateEmailAlreadyExists(false)
        }
    }

    LaunchedEffect(uiState.passwordUpdateError, uiState.passwordUpdateSuccess) {
        if (uiState.passwordUpdateError) {
            Toast.makeText(context, "erreur lors de la mise à jour du mot de passe", Toast.LENGTH_LONG).show()
            settingsViewModel.updatePasswordUpdateError(false)
        } else if (uiState.passwordUpdateSuccess) {
            Toast.makeText(context, "mot de passe mis à jour avec succès", Toast.LENGTH_LONG).show()
            settingsViewModel.updatePasswordUpdateSuccess(false)
        }
    }

    if(uiState.showPasswordDialogSecurity) {
        // Update User Password
        PasswordDialog(
            password = uiState.currentPassword,
            passwordError = uiState.currentPasswordError,
            onPasswordChanged = { settingsViewModel.updateCurrentPassword(it) },
            onPasswordConfirmed = {
                settingsViewModel.updateUserPassword()
            },
            onDismiss = {
                settingsViewModel.onDismissDialog()
            }
        )
    } else if(uiState.showPasswordDialogEmail) {

        // Update User EMAIL
        PasswordDialog(
            password = uiState.currentPassword,
            passwordError = uiState.currentPasswordError,
            onPasswordChanged = { settingsViewModel.updateCurrentPassword(it) },
            onPasswordConfirmed = {
                settingsViewModel.updateUserEmail()
            },
            onDismiss = {
                settingsViewModel.onDismissDialog()
            }
        )
    } else if(uiState.showPasswordDialogDeletion) {
        PasswordDialog(
            password = uiState.currentPassword,
            passwordError = uiState.currentPasswordError,
            onPasswordChanged = { settingsViewModel.updateCurrentPassword(it) },
            onPasswordConfirmed = {
                if (settingsViewModel.isCurrentPasswordValid()) {
                    settingsViewModel.updateShowPasswordDialogSecurity(false)
                    settingsViewModel.updateCurrentPassword("")
                }
            },
            onDismiss = {
                settingsViewModel.onDismissDialog()
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
            email = uiState.email,
            emailError = uiState.emailError,
            onEmailChanged = { settingsViewModel.updateEmail(it) },
            onEmailUpdate = {
                if(settingsViewModel.isEmailValid()) {
                    settingsViewModel.updateShowPasswordDialogEmail(true)
                }
            }
        )

        SecurityCard(
            newPassword = uiState.newPassword,
            newPasswordError = uiState.newPasswordError,
            onPasswordChanged = { settingsViewModel.updateNewPassword(it) },
            onPasswordUpdate = {
                if(settingsViewModel.isNewPasswordValid()) {
                    settingsViewModel.updateShowPasswordDialogSecurity(true)
                }
            }
        )

        DeletionAccountCard(
            onDeleteAccount = { settingsViewModel.updateShowPasswordDialogDeletion(true) }
        )

        SectionLogout(
            onLogout = onLogout
        )
    }
}

@Composable
fun EmailCard(
    email: String,
    emailError: Boolean,
    onEmailChanged: (String) -> Unit,
    onEmailUpdate: (String) -> Unit
) {
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
                emailError = emailError,
                onEmailChanged = { onEmailChanged(it) },
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
    newPassword: String,
    newPasswordError: Boolean,
    onPasswordChanged: (String) -> Unit,
    onPasswordUpdate: () -> Unit
) {
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
                passwordError = newPasswordError,
                passwordErrorMessage = stringResource(R.string.password_invalid_error),
                onPasswordChanged = { onPasswordChanged(it) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onPasswordUpdate,
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
    password: String,
    passwordError: Boolean,
    onPasswordChanged: (String) -> Unit,
    onPasswordConfirmed: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_medium)),
        properties = DialogProperties(usePlatformDefaultWidth = false),
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
                    passwordError = passwordError,
                    passwordErrorMessage = null,
                    onPasswordChanged = { onPasswordChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = null
                )
            }
        },
        confirmButton = {
            Button(onClick = { onPasswordConfirmed() }) {
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