package com.example.recipekeeper

import androidx.lifecycle.ViewModel
import com.example.recipekeeper.data.repository.IAuthRepository
import kotlinx.coroutines.flow.StateFlow

class RecipeKeeperViewModel(
    private val authRepository: IAuthRepository,
) : ViewModel() {
    val isUserLoggedIn: StateFlow<Boolean> = authRepository.isUserLoggedIn

    fun logout(){
        authRepository.logout()
    }
}