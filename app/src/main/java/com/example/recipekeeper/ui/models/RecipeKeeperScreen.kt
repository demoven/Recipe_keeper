package com.example.recipekeeper.ui.models

import androidx.annotation.StringRes
import com.example.recipekeeper.R

enum class RecipeKeeperScreen(
    @StringRes val title: Int,
) {
    Home(title = R.string.app_name),
    Account(title = R.string.account),
    CreateRecipe(title = R.string.create_recipe),
    AddFolder(title = R.string.add_folder),
    Settings(title = R.string.settings),
    Login(title = R.string.login),
    Register(title = R.string.register),
    RecipeDetail(title = R.string.recipe_detail),
    Cooking(title = R.string.cooking),
}
