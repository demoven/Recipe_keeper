package com.example.recipekeeper.ui.auth

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.recipekeeper.R

@Composable
fun PasswordTextField(
    label: String,
    password: String,
    passwordError: Boolean,
    passwordErrorMessage: String,
    onPasswordChanged: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier,
    keyboardActions: KeyboardActions? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChanged,
        label = { Text(label) },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions ?: KeyboardActions.Default,
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        isError = passwordError,
        supportingText = if (passwordError) {
            if(password.isBlank()) {
                { Text(stringResource(R.string.required_field)) }
            } else {
                { Text(passwordErrorMessage)}
            }
        } else null,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                val icon =
                    if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                Icon(imageVector = icon, contentDescription = null)
            }
        },
        modifier = modifier
    )
}

@Composable
fun EmailTextField(
    email: String,
    emailError: Boolean,
    onEmailChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
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
        isError = emailError,
        supportingText = if (emailError) {
            if (email.isBlank()) {
                { Text(stringResource(R.string.required_field))}
            } else{
                { Text(stringResource(R.string.invalid_email)) }
            }
        } else null,
        modifier = modifier
    )
}