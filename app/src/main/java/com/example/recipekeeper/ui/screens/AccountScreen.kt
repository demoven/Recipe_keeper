package com.example.recipekeeper.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AccountScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isEmailSectionVisible by remember { mutableStateOf(false) }
    var isPasswordSectionVisible by remember { mutableStateOf(false) }
    var isDeleteSectionVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Gestion du compte :", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.clickable { isEmailSectionVisible = !isEmailSectionVisible },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Modifier le mail", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Icon(
                imageVector = if (isEmailSectionVisible) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Dérouler"
            )
        }

        if (isEmailSectionVisible) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Nouveau mail") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* a remplir avec la logique de modification */ }) {
                Text("Confirmer la modification")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.clickable { isPasswordSectionVisible = !isPasswordSectionVisible },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Modifier le mot de passe", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Icon(
                imageVector = if (isPasswordSectionVisible) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Dérouler"
            )
        }

        if (isPasswordSectionVisible) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Nouveau mot de passe") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* a remplir avec la logique de modification */ }) {
                Text("Confirmer la modification")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.clickable { isDeleteSectionVisible = !isDeleteSectionVisible },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Supprimer le compte", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Icon(
                imageVector = if (isDeleteSectionVisible) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Dérouler"
            )
        }

        if (isDeleteSectionVisible) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* a remplir avec la logique de suppression */ }) {
                Text("Confirmer la suppression")
            }
        }
    }
}