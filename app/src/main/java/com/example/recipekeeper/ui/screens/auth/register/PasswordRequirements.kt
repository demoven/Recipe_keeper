package com.example.recipekeeper.ui.screens.auth.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.recipekeeper.R

@Composable
fun PasswordRequirements(
    password: String,
    modifier: Modifier = Modifier,
) {
    val isLengthValid = password.length >= 12
    val hasUppercase = password.any { it.isUpperCase() }
    val hasLowercase = password.any { it.isLowerCase() }
    val hasSpecialChar = password.any { !it.isLetterOrDigit() && !it.isWhitespace() }

    Column(modifier = modifier) {
        RequirementRow(
            text = stringResource(id = R.string.password_requirement_length),
            isValid = isLengthValid,
        )
        RequirementRow(
            text = stringResource(id = R.string.password_requirement_uppercase),
            isValid = hasUppercase,
        )
        RequirementRow(
            text = stringResource(id = R.string.password_requirement_lowercase),
            isValid = hasLowercase,
        )
        RequirementRow(
            text = stringResource(id = R.string.password_requirement_special),
            isValid = hasSpecialChar,
        )
    }
}

@Composable
private fun RequirementRow(
    text: String,
    isValid: Boolean,
) {
    val color: Color = if (isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    val icon = if (isValid) Icons.Filled.CheckCircle else Icons.Filled.Error

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
        )
        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_extra_small)))
        Text(text = text, color = color, style = MaterialTheme.typography.bodySmall)
    }
}
