package com.example.recipekeeper

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecipeKeeperViewModel : ViewModel() {
    private val _isUserLoggedIn = MutableStateFlow(FirebaseAuth.getInstance().currentUser != null)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        _isUserLoggedIn.value = auth.currentUser != null
    }

    init {
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
        super.onCleared()
    }

    fun logout(){
        FirebaseAuth.getInstance().signOut()
    }
}