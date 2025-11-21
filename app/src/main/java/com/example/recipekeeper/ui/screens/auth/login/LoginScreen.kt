package com.example.recipekeeper.ui.screens.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.example.recipekeeper.R
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipekeeper.di.factory.AuthViewModelFactory
import com.example.recipekeeper.ui.screens.auth.EmailTextField
import com.example.recipekeeper.ui.screens.auth.PasswordTextField

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onShowErrorMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    authFactory: AuthViewModelFactory
) {
    val loginViewModel: LoginViewModel = viewModel(factory = authFactory)
    val uiState by loginViewModel.uiState.collectAsState()
    val loginError = stringResource(R.string.signin_failed)
    LaunchedEffect(uiState.loginError) {
        if (uiState.loginError) {
            onShowErrorMessage(loginError)
            loginViewModel.updateLoginError(false)
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

        LoginLayout(
            email = uiState.email,
            password = uiState.password,
            passwordError = false,
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
fun LoginLayout(
    email: String,
    password: String,
    passwordError: Boolean,
    emailError: Boolean,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLogin: () -> Unit,
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
            passwordErrorMessage = "",
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
            text = stringResource(R.string.forgot_password)
        )
    }
}