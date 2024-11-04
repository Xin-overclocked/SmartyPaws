## Prerequisites

Before running the app, ensure you have the following installed:

- Android Studio (version 2023.3.1)
- Java Development Kit (JDK 22.0.1)
- Android SDK (API Level 34)
- Gradle (version 8.6)
- Firebase account and Firebase project configured
- `google-services.json` file added to the app’s `src/main` directory


## Setup

### SDK Versions

To ensure consistency across all developers' environments, the SDK versions (`compileSdk`, `minSdk`, and `targetSdk`) are defined in the `gradle.properties` file.

In `gradle.properties`, you'll find:

```properties
COMPILE_SDK_VERSION=34
MIN_SDK_VERSION=24
TARGET_SDK_VERSION=34
```

### Configuration
Ensure your environment is set up with:

- Java Development Kit (JDK) 8 or higher
- Android SDK matching the versions specified in gradle.properties
- Open build.gradle.kts to see how these values are referenced for consistency:

```
 android {
    compileSdk = (project.findProperty("COMPILE_SDK_VERSION") as String).toInt()

    defaultConfig {
        minSdk = (project.findProperty("MIN_SDK_VERSION") as String).toInt()
        targetSdk = (project.findProperty("TARGET_SDK_VERSION") as String).toInt()
    }
}
```


## Project Structure
```bash
com.example.myapp
│
├── data                 # Handles data operations (local/remote sources)
│   ├── repository       # Repositories for data access
│   ├── models           # Data entities
│   ├── local            # Firebase Firestore and Realtime Database integration
│   └── remote           # Retrofit API services (if applicable)
├── domain               # Core business logic
│   ├── models           # Domain models
│   └── usecases         # Business use cases
├── presentation         # UI components and ViewModels
│   ├── ui               # Activities and Fragments
│   ├── adapters         # RecyclerView adapters
│   └── viewmodels       # UI-related data management
├── utils                # Utility classes
└── di                   # Dependency injection (Hilt/Dagger)
```

## Features

- **Authentication**: Login, registration (`AuthRepository`) using Firebase Authentication.
- **Flashcards**: Create, edit, and study flashcards (`FlashcardRepository`) stored in Firebase Firestore.
- **Quizzes**: Manage quizzes (`QuizRepository`) with Firebase as the backend.
- **Friends**: Social interactions (`FriendRepository`) stored in Firebase.
- **AI Content**: Generate flashcards/quizzes from PDFs.
- **Cloud Storage**: Firebase Firestore and Realtime Database for data persistence.
- **Remote API**: Retrofit for external API calls (if any).

## Setup

1. Clone the repository:
    git clone https://github.com/Xin-overclocked/SmartyPaws.git

2. Open in Android Studio and sync Gradle.

3. Set up Firebase in your project:
    - Add the `google-services.json` file to your project.
    - Enable Firestore, Realtime Database, and Firebase Authentication.

4. Run on an emulator or device.

