package com.example.recipekeeper.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipekeeper.R
import com.example.recipekeeper.ui.screens.auth.EmailTextField

@Composable
fun SettingsScreen(
    onLogout: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(rememberScrollState())
    ) {
        Card {
            Column (
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_large)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
            ) {
                Text(
                    text = "COMPTE",
                    style = MaterialTheme.typography.titleMedium,
                )

                EmailTextField(
                    email = email,
                    emailError = false,
                    onEmailChanged = { email = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mettre à jour l'email")
                }
            }
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

      Card() {
          Column(
              modifier = Modifier
                  .padding(dimensionResource(R.dimen.padding_large)),
              verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
          ) {
              Text(
                  text = "SUPPRESSION DU COMPTE",
                    style = MaterialTheme.typography.titleMedium,
              )

              Text(
                  text = "La suppression de votre compte est irréversible. Toutes vos données seront perdues.",
                  style = MaterialTheme.typography.bodyLarge
              )

              OutlinedButton(
                  onClick = {},
                  border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                  colors = ButtonDefaults.outlinedButtonColors(
                      contentColor = MaterialTheme.colorScheme.error
                  ),
                  modifier = Modifier.fillMaxWidth()
              ) {
                  Icon(
                      imageVector = Icons.Outlined.Delete,
                        contentDescription = null
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Supprimer le compte")
              }
          }
      }

        Spacer(modifier = Modifier.height(32.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            TextButton(
                onClick = onLogout
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(
                    text = "Se déconnecter",
                    style = MaterialTheme.typography.titleLarge
                    )
            }
        }
    }
}
