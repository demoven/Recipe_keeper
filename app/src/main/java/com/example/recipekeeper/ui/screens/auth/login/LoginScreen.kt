package com.example.recipekeeper.ui.screens.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipekeeper.R
import com.example.recipekeeper.di.factory.AuthViewModelFactory
import com.example.recipekeeper.ui.components.FullScreenLoadingIndicator
import com.example.recipekeeper.ui.components.snackbar.SnackbarType
import com.example.recipekeeper.ui.screens.auth.EmailTextField
import com.example.recipekeeper.ui.screens.auth.PasswordTextField

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    authFactory: AuthViewModelFactory,
    onDisplayMessage: (String, SnackbarType) -> Unit,
) {
    val loginViewModel: LoginViewModel = viewModel(factory = authFactory)
    val uiState by loginViewModel.uiState.collectAsState()
    val loginError = stringResource(R.string.signin_failed)
    val context = LocalContext.current

    LaunchedEffect(uiState.emailVerificationError, uiState.loginError) {
        if (uiState.emailVerificationError) {
            onDisplayMessage(context.getString(R.string.check_mailbox), SnackbarType.Error)
            loginViewModel.updateEmailVerificationError(false)
        } else if (uiState.loginError) {
            onDisplayMessage(loginError, SnackbarType.Error)
            loginViewModel.updateLoginError(false)
        }
    }

    if (uiState.isLoading) {
        FullScreenLoadingIndicator()
    }

    Column(
        modifier =
            Modifier
                .padding(dimensionResource(R.dimen.padding_large))
                .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (uiState.isResetPasswordDialogVisible) {
            PasswordResetDialog(
                initialEmail = uiState.email,
                onDismiss = { loginViewModel.hideResetPasswordDialog() },
                onSend = { email -> loginViewModel.sendPasswordResetEmail(email) },
                isEmailSent = uiState.isResetPasswordEmailSent,
                errorMessage = if (uiState.resetPasswordError) stringResource(R.string.reset_password_error) else null,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_medium)),
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(0.6f, fill = true),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.logo_open_no_bg),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.image_size_extra_large)),
            )
        }

        LoginLayout(
            email = uiState.email,
            password = uiState.password,
            passwordError = uiState.passwordError,
            emailError = uiState.emailError,
            isLoading = uiState.isLoading,
            onEmailChanged = {
                loginViewModel.updateEmail(it)
            },
            onPasswordChanged = {
                loginViewModel.updatePassword(it)
            },
            onLogin = {
                loginViewModel.login()
            },
            onForgotPasswordClick = {
                loginViewModel.showResetPasswordDialog()
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.weight(0.4f, fill = true))

        OutlinedButton(
            onClick = {
                onNavigateToRegister()
                loginViewModel.resetAllFields()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.create_account))
        }
    }
}

@Composable
fun PasswordResetDialog(
    initialEmail: String,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit,
    isEmailSent: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier,
) {
    var email by remember { mutableStateOf(initialEmail) }
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        title = { Text(stringResource(R.string.reset_password)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isEmailSent) {
                    Text(
                        stringResource(R.string.email_sent),
                        color = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    Text(stringResource(R.string.enter_reset_email))
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(stringResource(R.string.email)) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (errorMessage != null) {
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        confirmButton = {
            if (!isEmailSent) {
                Button(onClick = { onSend(email) }) {
                    Text(stringResource(R.string.send))
                }
            } else {
                Button(onClick = onDismiss) {
                    Text(stringResource(R.string.close))
                }
            }
        },
        dismissButton = {
            if (!isEmailSent) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            }
        },
    )
}

@Composable
fun LoginLayout(
    email: String,
    password: String,
    passwordError: Boolean,
    emailError: Boolean,
    isLoading: Boolean,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLogin: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        EmailTextField(
            email = email,
            onEmailChanged = onEmailChanged,
            modifier = Modifier.fillMaxWidth(),
            emailError = emailError,
        )
        Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_extra_small)))
        PasswordTextField(
            label = stringResource(R.string.password),
            password = password,
            onPasswordChanged = onPasswordChanged,
            keyboardOptions =
                KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                ),
            keyboardActions =
                KeyboardActions(
                    onDone = { onLogin() },
                ),
            passwordError = passwordError,
            passwordErrorMessage = null,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_small)))
        Button(
            onClick = { onLogin() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
        ) {
            Text(stringResource(R.string.login))
        }
        Text(
            text = stringResource(R.string.forgot_password),
            modifier =
                Modifier
                    .clickable { onForgotPasswordClick() }
                    .padding(top = dimensionResource(R.dimen.padding_small)),
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
        )
    }
}
