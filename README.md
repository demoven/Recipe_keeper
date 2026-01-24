# Recipe Keeper 🍳

**Recipe Keeper** is a modern Android application built with **Kotlin** and **Jetpack Compose** that allows users to create, organize, and manage their favorite culinary recipes. The app features a robust **voice-controlled cooking mode**, enabling a hands-free experience while in the kitchen.

## ✨ Features

* **Recipe Management**: Create, read, update, and delete recipes with detailed information including preparation time, cooking time, servings, ingredients, and step-by-step instructions.
* **Folder Organization**: Organize your recipes into folders and subfolders for better categorization.
* **Visual Categories**: Select from pre-defined visual themes for your recipes (Dessert, Soup, Cookies, Main Dish).
* **Hands-Free Cooking Mode**: A specialized screen for cooking that utilizes **Voice Recognition** to navigate steps without touching the screen.
* **Cloud Sync**: Data is securely stored and synchronized using **Firebase Firestore** and **Firebase Authentication**.
* **Modern UI**: Built entirely with **Jetpack Compose** following Material Design 3 guidelines.

## 🎤 Voice Commands (Cooking Mode)

The application supports French voice commands to help you cook without dirtying your screen. The following commands are available:

| Command | Action |
| :--- | :--- |
| **"Suivant"** | Go to the next step. |
| **"Retour"** | Go back to the previous step. |
| **"Ingrédient"** | Open the ingredients list overlay. |
| **"Fermer"** | Close the ingredients list overlay. |
| **"Terminer"** / **"Terminé"** | Finish the recipe. |

> **Note:** The voice recognition model is configured for the French language (`fr-FR`).

## 🛠️ Tech Stack

* **Language**: [Kotlin](https://kotlinlang.org/)
* **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
* **Backend**: [Firebase](https://firebase.google.com/) (Firestore & Authentication)
* **Navigation**: Android Navigation Compose
* **Asynchronous Programming**: Coroutines & Flow
* **Architecture**: MVVM (Model-View-ViewModel)
* **Minimum SDK**: 24 (Android 7.0)
* **Target SDK**: 36

## 🚀 Getting Started

### Prerequisites

* Android Studio Ladybug (or newer)
* JDK 11
* A Firebase Console project

### Installation

1.  **Clone the repository**
    ```bash
    git clone [https://github.com/your-username/recipe-keeper.git](https://github.com/your-username/recipe-keeper.git)
    ```

2.  **Firebase Configuration**
    * Go to the [Firebase Console](https://console.firebase.google.com/).
    * Create a new project.
    * Add an Android app with the package name: `com.example.recipekeeper`.
    * Enable **Authentication** (Email/Password) and **Cloud Firestore**.
    * Download the `google-services.json` file.
    * Place the `google-services.json` file in the `app/` directory of the project.

3.  **Build the project**
    * Open the project in Android Studio.
    * Sync Gradle files.
    * Run the application on an emulator or physical device.

## 📂 Project Structure

The project follows a clean architecture pattern separation:

* **`data/`**: Contains data models (`Recipe`, `Folder`, `AuthUser`) and repositories.
* **`di/`**: Dependency Injection containers and factories (`AppContainer`, `ViewModelFactory`).
* **`ui/`**:
    * **`components/`**: Reusable Compose UI elements (`RecipeCard`, `AppTopBar`).
    * **`screens/`**: Feature-specific screens (`home`, `create_recipe`, `cooking`, `auth`).
    * **`theme/`**: Theme definitions (Color, Type, Shape).

## 🔐 Permissions

The app requires the following permissions to function correctly:

* `INTERNET`: To communicate with Firebase.
* `ACCESS_NETWORK_STATE`: To check for connectivity.
* `RECORD_AUDIO`: For the voice recognition feature during cooking.
