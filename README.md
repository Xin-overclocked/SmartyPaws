## Prerequisites

Before running the app, ensure you have the following installed:

- Android Studio (version 2023.3.1)
- Java Development Kit (JDK 22.0.1)
- Android SDK (API Level 34)
- Gradle (version 8.6)
- Firebase account and Firebase project configured
- `google-services.json` file added to the app’s `src/main` directory

## Branching Strategy
We will use a branching strategy to organize our work. The main branch will be `main`, and we will create feature branches for development.

### Branch Naming Convention
- **Main Branch**: `main`
- **Feature Branches**: `feature/feature-name` (e.g., `feature/login-screen`)
- **Bugfix Branches**: `bugfix/issue-description` (e.g., `bugfix/fix-login-error`)

### Important Notes
- Always pull the latest changes from the main branch before creating a new branch.
- Use descriptive commit messages to make the history clear for everyone.
- Communicate with your team about what features or fixes you are working on to avoid overlap.

### Workflow
1. **Create a Branch**: When starting work on a new feature or bugfix, create a new branch from `main`:
   - Click on **Git** in the bottom toolbar, then select **Branches**.
   - Choose **New Branch from 'main'**, name your branch (e.g., `feature/your-feature-name`), and ensure it is created from `main`.

   Alternatively, you can use the command line:
   ```bash
   git checkout main
   git pull origin main
   git checkout -b feature/your-feature-name
   ```
3. **Make Changes**: Develop your feature or fix in your branch.
4. **Commit Changes**: Commit your changes with clear, descriptive messages:
   - This can also be done using the IDE (Intellij and Android Studio)
   
   Or via command line:
   ```bash
   git add .
   git commit -m "Add login screen UI"
   ```
5. **Push Your Branch**: Push your branch to the remote repository:
   In IntelliJ IDEA, you can do this by:
   Going to Git > Push or clicking the Push button in the toolbar.
   
   Alternatively, you can use the command line:
   ```bash
   git push origin feature/your-feature-name
   ```
6. **Create a Pull Request**: Once your work is ready, create a pull request (PR) to merge your branch into main. Ensure to request reviews from team members.
   - This can be done directly in the IDE if integrated with a service like GitHub or GitLab:
   - Use the Git menu or the GitHub tool window to create a PR and ensure to request reviews from team members.

7. **Merge Changes**: After approvals and successful checks, merge your PR into the main branch.

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

