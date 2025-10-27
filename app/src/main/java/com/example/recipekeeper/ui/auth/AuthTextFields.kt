package com.example.recipekeeper.ui.auth

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
    password: String,
    onPasswordChanged: (String) -> Unit,
    imeAction: ImeAction,
    modifier: Modifier = Modifier,
    passwordError: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChanged,
        label = { Text(stringResource(R.string.password)) },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction
        ),
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        isError = passwordError != null,
        supportingText = if (passwordError != null) {
            { Text(passwordError, color = MaterialTheme.colorScheme.error) }
        }
        else null,
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
    onEmailChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    emailError: String? = null
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
        isError = emailError != null,
        supportingText = if (emailError != null) {
            { Text(emailError, color = MaterialTheme.colorScheme.error) }
        } else null,
        modifier = modifier
    )
}