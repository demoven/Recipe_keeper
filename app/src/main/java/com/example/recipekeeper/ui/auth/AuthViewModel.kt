package com.example.recipekeeper.ui.auth

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

//    var email by mutableStateOf("")
//        private set
    var password by mutableStateOf("")
        private set
    var confirmPassword by mutableStateOf("")
        private set

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        _uiState.value = _uiState.value.copy(
            isLoggedIn = user != null,
            email = user?.email ?: ""
        )
    }
    init {
        val user = firebaseAuth.currentUser
        _uiState.value = _uiState.value.copy(
            isLoggedIn = user != null,
            email = user?.email ?: ""
        )
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    fun updateEmail(updatedEmail: String) {
        _uiState.value = _uiState.value.copy(emailError = null)
        _uiState.value = _uiState.value.copy(email = updatedEmail)
    }

    fun updatePassword(updatedPassword: String) {
        _uiState.value = _uiState.value.copy(passwordError = null)
        password = updatedPassword
    }
    fun updateConfirmPassword(updatedConfirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPasswordError = null)
        confirmPassword = updatedConfirmPassword
    }
    fun resetPassword() {
        password = ""
        confirmPassword = ""
    }
    fun resetFields() {
        _uiState.value = _uiState.value.copy(email = "")
        password = ""
        confirmPassword = ""
    }

    fun resetErrors() {
        _uiState.value = _uiState.value.copy(
            emailError = null,
            passwordError = null,
            confirmPasswordError = null,
            errorMessage = null
        )
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    fun setErrorMessage(message: String?) {
        _uiState.value = _uiState.value.copy(errorMessage = message)
    }

    fun setEmailError(message: String?) {
        _uiState.value = _uiState.value.copy(emailError = message)
    }

    fun setPasswordError(message: String?) {
        _uiState.value = _uiState.value.copy(passwordError = message)
    }

    fun setConfirmPasswordError(message: String?) {
        _uiState.value = _uiState.value.copy(confirmPasswordError = message)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        _uiState.value = _uiState.value.copy(isLoggedIn = isLoggedIn)
    }

    private fun validateEmail(email: String): String? {
        return if (email.isBlank())
            "Email requis"
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            "Email invalide"
        else null
    }

    private fun isPasswordValid(password: String): Boolean {
        val lengthOk = password.length >= 12
        val upperOk = password.any { it.isUpperCase() }
        val lowerOk = password.any { it.isLowerCase() }
        val specialOk = password.any { !it.isLetterOrDigit() && !it.isWhitespace() } // exclut les espaces
        return lengthOk && upperOk && lowerOk && specialOk
    }

    private fun validatePassword(password: String): String? {
        return if (password.isBlank())
            "Mot de passe requis"
        else if (!isPasswordValid(password))
            "Le mot de passe doit contenir au moins 12 caractères dont une majuscule, une minuscule et un caractère spécial."
        else null
    }

    private fun validateConfirmPassword(confirmPassword: String): String? {
        return if (confirmPassword.isBlank())
            "Veuillez confirmer le mot de passe"
        else if (confirmPassword != password)
            "Les mots de passe ne correspondent pas"
        else null
    }

    fun register(onSuccess: () -> Unit) {
        val emailErr = validateEmail(_uiState.value.email)
        setEmailError(emailErr)
        val passwordErr = validatePassword(password)
        setPasswordError(passwordErr)
        val confirmPasswordErr = validateConfirmPassword(confirmPassword)
        setConfirmPasswordError(confirmPasswordErr)
        if (emailErr != null || passwordErr != null || confirmPasswordErr != null) {
            return
        }
        firebaseAuth.createUserWithEmailAndPassword(_uiState.value.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    setLoggedIn(true)
                    resetFields()
                    onSuccess()
                } else {
                    Log.d("AuthViewModel", "Registration failed: ${task.exception?.message}" )
                    setErrorMessage("Echec de l'inscription")
                }
            }
    }

    fun login(onSuccess: () -> Unit) {
        val emailErr = validateEmail(_uiState.value.email)
        setEmailError(emailErr)
        if (password.isBlank()) {
            setPasswordError("Mot de passe requis")
            return
        }
        if (emailErr != null) {
            return
        }
        firebaseAuth.signInWithEmailAndPassword(_uiState.value.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    setLoggedIn(true)
                    resetFields()
                    onSuccess()
                } else {
                    Log.d("AuthViewModel", "Login failed: ${task.exception?.message}" )
                    setErrorMessage("Echec de la connexion")
                }
            }
    }
    fun logout() {
        firebaseAuth.signOut()
        setLoggedIn(false)
        resetFields()
    }
}