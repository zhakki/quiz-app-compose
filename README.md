# Variant B — Trivia Quiz Game
A simple Android app that uses the Trivia Quiz API as a source of quiz questions
## The application allows:
- choose a question category
- choose the difficulty level of the questions
- choose the number of questions
- uses a token to reduce the number of repeated questions
- uses Room question cache
- uses Room for results history
- a question and 4 answer options are displayed. The user receives points for choosing the correct answer.
- at the end of the quiz, the number of correct answers and the score are displayed.
- the quiz history is displayed: date, category, and result.
## Technologies used
- Kotlin: Primary programming language
- Kotlin Coroutines: For asynchronous programming and background tasks
- Kotlin Flow: Reactive data streams (StateFlow, SharedFlow)
- Jetpack Compose: Declarative UI framework with Material 3
- Compose Navigation: Type-safe navigation between screens
- MVVM Architecture: Clean separation of concerns (Model-View-ViewModel)
- Repository Pattern: Centralized data access logic
- Jetpack ViewModel: UI state management and lifecycle awareness
- Room Persistence: Local SQLite database for caching and history
- Retrofit & OkHttp: Type-safe HTTP client for API requests
- Gson: JSON serialization and deserialization
- KSP (Kotlin Symbol Processing): Faster annotation processing for Room
## To start
1. Clone repository
1. Open project in Android Studio
1. Wait for Gradle Sync to finish
1. Run the app in emulator or device (Run ▶)
## The Team
1. Zinaida Romanova 231803EDTR - geisterin - role: UI / Layout / Screens
1. Ilona Žakovitš 231818EDTR - zhakki - role: Room / local database / history / leaderboard
1. Margus Apinis 231788EDTR - maapin - role: Retrofit / MVVM / Business Logic
