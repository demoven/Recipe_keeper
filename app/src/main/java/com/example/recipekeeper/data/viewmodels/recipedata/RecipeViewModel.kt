package com.example.recipekeeper.data.viewmodels.recipedata

import androidx.lifecycle.ViewModel
import com.example.recipekeeper.data.models.Folder
import com.example.recipekeeper.data.models.Recipe
import com.example.recipekeeper.data.models.UserRecipes

class RecipeViewModel: ViewModel() {
    fun getFakeRecipeDataSet1(): List<Recipe> {
        return listOf(
            Recipe(
                id = "1",
                title = "Spaghetti Bolognese",
                ingredients = listOf("Spaghetti", "Ground Beef", "Tomato Sauce", "Onion", "Garlic"),
                instructions = listOf("Cook spaghetti according to package instructions.", "In a separate pan, sauté onion and garlic.", "Add ground beef and cook until browned.", "Pour in tomato sauce and simmer for 20 minutes.", "Serve sauce over spaghetti.")
            ),
            Recipe(
                id = "2",
                title = "Chicken Curry",
                ingredients = listOf("Chicken", "Curry Powder", "Coconut Milk", "Onion", "Rice"),
                instructions = listOf("Cook rice according to package instructions.", "In a pan, sauté onion until translucent.", "Add chicken pieces and cook until no longer pink.", "Stir in curry powder and cook for 2 minutes.", "Pour in coconut milk and simmer until chicken is cooked through.", "Serve curry over rice.")
            )
        )
    }
    fun getFakeRecipeDataSet2(): List<Recipe> {
        return listOf(
            Recipe(
                id = "3",
                title = "Vegetable Stir Fry",
                ingredients = listOf("Mixed Vegetables", "Soy Sauce", "Garlic", "Ginger", "Rice"),
                instructions = listOf("Cook rice according to package instructions.", "In a wok, heat oil and sauté garlic and ginger.", "Add mixed vegetables and stir fry until tender-crisp.", "Pour in soy sauce and toss to coat.", "Serve stir fry over rice.")
            ),
            Recipe(
                id = "4",
                title = "Beef Tacos",
                ingredients = listOf("Ground Beef", "Taco Shells", "Lettuce", "Tomato", "Cheese"),
                instructions = listOf("Cook ground beef in a pan until browned.", "Warm taco shells according to package instructions.", "Assemble tacos with beef, lettuce, tomato, and cheese.", "Serve immediately.")
            )
        )
    }
    fun getFakeFolderData(): List<Folder> {
        return listOf(
            Folder(
                id = "f1",
                name = "Italian Recipes",
                recipes = getFakeRecipeDataSet1(),
                subFolders = emptyList()
            ),
            Folder(
                id = "f2",
                name = "Indian Recipes",
                recipes = getFakeRecipeDataSet2(),
                subFolders = emptyList()
            ),
            Folder (
                id = "f3",
                name = "Quick Meals",
                recipes = listOf(),
                subFolders = listOf(
                    Folder(
                        id = "f3a",
                        name = "15-Minute Recipes",
                        recipes = getFakeRecipeDataSet1(),
                        subFolders = emptyList()
                    ),
                    Folder(
                        id = "f3b",
                        name = "30-Minute Recipes",
                        recipes = getFakeRecipeDataSet2(),
                        subFolders = emptyList()
                    ))
            )
        )
    }
    fun getFakeUserRecipesData() = UserRecipes(
        userId = "user123",
        recipes = getFakeRecipeDataSet1(),
        folders = getFakeFolderData()
    )
}