package com.example.recipekeeper.ui.screens.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipekeeper.R
import com.example.recipekeeper.di.factory.AuthViewModelFactory
import com.example.recipekeeper.ui.screens.auth.EmailTextField
import com.example.recipekeeper.ui.screens.auth.PasswordTextField

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onShowErrorMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    authFactory: AuthViewModelFactory
) {
    val registerViewModel: RegisterViewModel = viewModel(factory = authFactory)
    val uiState by registerViewModel.uiState.collectAsState()
    val registerError = stringResource(R.string.register_failed)
    LaunchedEffect(uiState.registerError) {
        if (uiState.registerError) {
            onShowErrorMessage(registerError)
            registerViewModel.updateRegisterError(false)
        }
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
        RegisterLayout(
            email = uiState.email,
            password = uiState.password,
            confirmedPassword = uiState.confirmPassword,
            emailError = uiState.emailError,
            passwordError = uiState.passwordError,
            confirmedPasswordError = uiState.confirmPasswordError,
            onEmailChanged = {
                registerViewModel.updateEmail(it)
            },
            onPasswordChanged = {
                registerViewModel.updatePassword(it)
            },
            onConfirmedPasswordChanged = {
                registerViewModel.updateConfirmPassword(it)
            },
            onRegister = {
                registerViewModel.register()
            },
            onNavigateToLogin = {
                onNavigateToLogin()
                registerViewModel.resetAllFields()
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.weight(0.4f, fill = true))
    }
}

@Composable
fun RegisterLayout(
    email: String,
    password: String,
    confirmedPassword: String,
    emailError: Boolean,
    passwordError: Boolean,
    confirmedPasswordError: Boolean,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmedPasswordChanged: (String) -> Unit,
    onRegister: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailTextField(
            email = email,
            onEmailChanged = onEmailChanged,
            emailError = emailError,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_extra_small)))
        PasswordTextField(
            label = stringResource(R.string.password),
            password = password,
            onPasswordChanged = onPasswordChanged,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = null,
            passwordError = passwordError,
            passwordErrorMessage = stringResource(R.string.password_invalid_error),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_extra_small)))
        PasswordTextField(
            label = stringResource(R.string.confirm_password),
            password = confirmedPassword,
            onPasswordChanged = onConfirmedPasswordChanged,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onRegister() }
            ),
            passwordError = confirmedPasswordError,
            passwordErrorMessage = stringResource(R.string.passwords_do_not_match),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_small)))
        Button(
            onClick = { onRegister() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.register))
        }
        Text(
            text = stringResource(R.string.already_have_account),
            modifier = Modifier.clickable(onClick = onNavigateToLogin)
        )
    }
}