package com.example.recipekeeper.ui.screens.auth.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.example.recipekeeper.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipekeeper.di.factory.AuthViewModelFactory
import com.example.recipekeeper.ui.screens.auth.EmailTextField
import com.example.recipekeeper.ui.screens.auth.PasswordTextField

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    authFactory: AuthViewModelFactory
) {
    val loginViewModel: LoginViewModel = viewModel(factory = authFactory)
    val uiState by loginViewModel.uiState.collectAsState()
    val loginError = stringResource(R.string.signin_failed)
    val context = LocalContext.current

    LaunchedEffect(uiState.emailVerificationError) {
        if (uiState.emailVerificationError) {
            Toast.makeText(context, context.getString(R.string.check_mailbox), Toast.LENGTH_SHORT).show()
            loginViewModel.updateEmailVerificationError(false)
        }
    }

    LaunchedEffect(uiState.loginError) {
        if (uiState.loginError) {
            Toast.makeText(context, loginError, Toast.LENGTH_SHORT).show()
            loginViewModel.updateLoginError(false)
        }
    }

    if (uiState.isResetPasswordDialogVisible) {
        ResetPasswordDialog(
            initialEmail = uiState.email,
            onDismiss = { loginViewModel.hideResetPasswordDialog() },
            onSend = { email -> loginViewModel.sendPasswordResetEmail(email) },
            isEmailSent = uiState.isResetPasswordEmailSent,
            errorMessage = uiState.resetPasswordError
        )
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f, fill = true),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo_open_no_bg),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.image_size_extra_large))
            )
        }

        LoginLayout(
            email = uiState.email,
            password = uiState.password,
            passwordError = uiState.passwordError,
            emailError = uiState.emailError,
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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(0.4f, fill = true))

        OutlinedButton(
            onClick = {
                onNavigateToRegister()
                loginViewModel.resetAllFields()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.create_account))
        }
    }
}

@Composable
fun ResetPasswordDialog(
    initialEmail: String,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit,
    isEmailSent: Boolean,
    errorMessage: String?
) {
    var email by remember { mutableStateOf(initialEmail) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.reset_password)) },
        text = {
            Column {
                if (isEmailSent) {
                    Text(stringResource(R.string.email_sent), color = MaterialTheme.colorScheme.primary)
                } else {
                    Text(stringResource(R.string.enter_reset_email))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(stringResource(R.string.email)) },
                        singleLine = true
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
        }
    )
}

@Composable
fun LoginLayout(
    email: String,
    password: String,
    passwordError: Boolean,
    emailError: Boolean,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLogin: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailTextField(
            email = email,
            onEmailChanged = onEmailChanged,
            modifier = Modifier.fillMaxWidth(),
            emailError = emailError
        )
        Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_extra_small)))
        PasswordTextField(
            label = stringResource(R.string.password),
            password = password,
            onPasswordChanged = onPasswordChanged,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onLogin() }
            ),
            passwordError = passwordError,
            passwordErrorMessage = null,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_small)))
        Button(
            onClick = { onLogin() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.login))
        }
        Text(
            text = stringResource(R.string.forgot_password),
            modifier = Modifier
                .clickable { onForgotPasswordClick() }
                .padding(top = 8.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}