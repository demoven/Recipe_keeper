package com.example.recipekeeper.di

import com.example.recipekeeper.data.repository.IAuthRepository
import com.example.recipekeeper.data.repository.impl.AuthRepositoryImpl
import com.example.recipekeeper.di.factory.AuthViewModelFactory
import com.example.recipekeeper.di.factory.RecipeKeeperViewModelFactory

class AppContainer {
    val authRepository: IAuthRepository = AuthRepositoryImpl()
    val authFactory = AuthViewModelFactory(authRepository = authRepository)
    val recipeKeeperFactory = RecipeKeeperViewModelFactory(
        authRepository = authRepository
    )

    fun createUserContainer(userId: String): UserContainer {
        return UserContainer(
            authRepository = authRepository,
            userId = userId
        )
    }
}