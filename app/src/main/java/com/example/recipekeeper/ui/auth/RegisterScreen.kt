package com.example.recipekeeper.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.example.recipekeeper.R

@Composable
fun RegisterScreen(
    email: String,
    password: String,
    confirmedPassword: String,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmedPasswordChanged: (String) -> Unit,
    onRegister: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChanged,
            label = { Text(stringResource(R.string.email)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_extra_small)))
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChanged,
            label = { Text(stringResource(R.string.password)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_extra_small)))
        OutlinedTextField(
            value = confirmedPassword,
            onValueChange = onConfirmedPasswordChanged,
            label = { Text(stringResource(R.string.confirm_password)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions (
                onSend = { onRegister() }
            ),
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
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