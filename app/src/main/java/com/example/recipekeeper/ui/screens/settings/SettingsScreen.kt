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
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipekeeper.R
import com.example.recipekeeper.ui.components.FullScreenLoadingIndicator
import com.example.recipekeeper.ui.components.snackbar.SnackbarType
import com.example.recipekeeper.ui.screens.auth.EmailTextField
import com.example.recipekeeper.ui.screens.auth.PasswordTextField

@Composable
fun SettingsScreen(
    onDisplayMessage: (String, SnackbarType) -> Unit,
    settingsViewModel: SettingsViewModel = viewModel(),
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val emailSuccessMessage = stringResource(R.string.check_mailbox)
    val passwordSuccessMessage = stringResource(R.string.password_update_success)

    // Affichage des messages de succès via la snackbar globale
    LaunchedEffect(uiState.emailUpdateSuccess, uiState.passwordUpdateSuccess) {
        when {
            uiState.emailUpdateSuccess -> {
                onDisplayMessage(emailSuccessMessage, SnackbarType.Info)
                settingsViewModel.clearSuccessFlags()
            }

            uiState.passwordUpdateSuccess -> {
                onDisplayMessage(passwordSuccessMessage, SnackbarType.Info)
                settingsViewModel.clearSuccessFlags()
            }
        }
    }

    if (uiState.isLoading) {
        FullScreenLoadingIndicator()
    }

    if (uiState.showPasswordDialogSecurity) {
        PasswordDialog(
            password = uiState.currentPassword,
            passwordError = uiState.currentPasswordError,
            messageResId = uiState.passwordDialogMessageResId,
            isLoading = uiState.isLoading,
            onPasswordChanged = { settingsViewModel.updateCurrentPassword(it) },
            onPasswordConfirmed = {
                settingsViewModel.updateUserPassword()
            },
            onDismiss = {
                settingsViewModel.onDismissDialog()
            },
        )
    } else if (uiState.showPasswordDialogEmail) {
        PasswordDialog(
            password = uiState.currentPassword,
            passwordError = uiState.currentPasswordError,
            messageResId = uiState.passwordDialogMessageResId,
            isLoading = uiState.isLoading,
            onPasswordChanged = { settingsViewModel.updateCurrentPassword(it) },
            onPasswordConfirmed = {
                settingsViewModel.updateUserEmail()
            },
            onDismiss = {
                settingsViewModel.onDismissDialog()
            },
        )
    } else if (uiState.showPasswordDialogDeletion) {
        PasswordDialog(
            password = uiState.currentPassword,
            passwordError = uiState.currentPasswordError,
            messageResId = uiState.passwordDialogMessageResId,
            isLoading = uiState.isLoading,
            onPasswordChanged = { settingsViewModel.updateCurrentPassword(it) },
            onPasswordConfirmed = {
                settingsViewModel.deleteAccount()
            },
            onDismiss = {
                settingsViewModel.onDismissDialog()
            },
        )
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_medium))
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
    ) {
        EmailCard(
            email = uiState.email,
            emailError = uiState.emailError,
            onEmailChanged = { settingsViewModel.updateEmail(it) },
            onEmailUpdate = {
                if (settingsViewModel.isEmailValid()) {
                    settingsViewModel.updateShowPasswordDialogEmail(true)
                }
            },
        )

        SecurityCard(
            newPassword = uiState.newPassword,
            newPasswordError = uiState.newPasswordError,
            onPasswordChanged = { settingsViewModel.updateNewPassword(it) },
            onPasswordUpdate = {
                if (settingsViewModel.isNewPasswordValid()) {
                    settingsViewModel.updateShowPasswordDialogSecurity(true)
                }
            },
        )

        DeletionAccountCard(
            onDeleteAccount = { settingsViewModel.updateShowPasswordDialogDeletion(true) },
        )

        SectionLogout(
            onLogout = { settingsViewModel.logout() },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun EmailCard(
    email: String,
    emailError: Boolean,
    onEmailChanged: (String) -> Unit,
    onEmailUpdate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(
            modifier =
                Modifier
                    .padding(dimensionResource(R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
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
                keyboardOptions =
                    KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Email,
                    ),
                keyboardActions =
                    KeyboardActions(
                        onDone = { onEmailUpdate(email) },
                    ),
            )

            Button(
                onClick = { onEmailUpdate(email) },
                modifier = Modifier.fillMaxWidth(),
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
    onPasswordUpdate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(
            modifier =
                Modifier
                    .padding(dimensionResource(R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        ) {
            Text(
                text = stringResource(R.string.security).uppercase(),
                style = MaterialTheme.typography.titleMedium,
            )

            PasswordTextField(
                label = stringResource(R.string.new_password),
                password = newPassword,
                passwordError = newPasswordError,
                passwordErrorMessage = stringResource(R.string.password_invalid_error),
                onPasswordChanged = { onPasswordChanged(it) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions =
                    KeyboardActions(
                        onDone = { onPasswordUpdate() },
                    ),
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = onPasswordUpdate,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.update_password))
            }
        }
    }
}

@Composable
fun DeletionAccountCard(
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(
            modifier =
                Modifier
                    .padding(dimensionResource(R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        ) {
            Text(
                text = stringResource(R.string.deletion_account).uppercase(),
                style = MaterialTheme.typography.titleMedium,
            )

            Text(
                text = stringResource(R.string.deletion_account_confirmation),
                style = MaterialTheme.typography.bodyLarge,
            )

            OutlinedButton(
                onClick = onDeleteAccount,
                border = BorderStroke(dimensionResource(R.dimen.border_thin), MaterialTheme.colorScheme.error),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.width_small)))
                Text(stringResource(R.string.delete_account))
            }
        }
    }
}

@Composable
fun SectionLogout(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        TextButton(
            onClick = onLogout,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_small)))
            Text(
                text = stringResource(R.string.logout),
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@Composable
fun PasswordDialog(
    password: String,
    passwordError: Boolean,
    messageResId: Int?,
    isLoading: Boolean = false,
    onPasswordChanged: (String) -> Unit,
    onPasswordConfirmed: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.current_password)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
            ) {
                Text(stringResource(R.string.enter_current_password))
                PasswordTextField(
                    label = stringResource(R.string.current_password),
                    password = password,
                    passwordError = passwordError,
                    passwordErrorMessage = null,
                    onPasswordChanged = { onPasswordChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = null,
                )
                if (messageResId != null) {
                    Text(
                        text = stringResource(id = messageResId),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isLoading,
                onClick = { onPasswordConfirmed() },
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
