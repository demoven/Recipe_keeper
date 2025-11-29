package com.example.recipekeeper.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    onLogout: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Section: Modifier le mail
        Text("Modifier le mail", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = email,
            onValueChange = { email = it },
            label = { Text("Nouveau mail") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { /* a remplir avec la logique de modification */ },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Confirmer la modification")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Modifier le mot de passe
        Text("Modifier le mot de passe", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        //OutlinedTextField(
        //    modifier = Modifier.fillMaxWidth(),
        //    value = password,
        //    onValueChange = { password = it },
        //    label = { Text("Nouveau mot de passe") },
        //    visualTransformation = PasswordVisualTransformation()
        //)
        //Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { /* a remplir avec la logique de modification */ },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Modifier le mot de passe")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Supprimer le compte
        Text("Supprimer le compte", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { /* a remplir avec la logique de suppression */ },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Confirmer la suppression")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = onLogout
        ) {
            Text("Se déconnecter")
        }
    }
}
