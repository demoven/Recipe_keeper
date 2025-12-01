package com.example.recipekeeper

import android.app.Application
import com.example.recipekeeper.di.AppContainer

class RecipeKeeperApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}
