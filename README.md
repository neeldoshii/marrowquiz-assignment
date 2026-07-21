# Marrow Quiz

MCQs from a remote API, streak-based feedback, and a results screen.

## Features

- **Splash & load** — fetches questions on launch with a loading state and retry on failure
- **Question flow** — one question at a time with four options, progress indicator, and animated transitions
- **Answer reveal** — highlights correct vs selected answers, then auto-advances after 2 seconds
- **Skip** — skip button or swipe horizontally to move on without committing an answer
- **Streak system** — tracks consecutive correct answers; flame badge lights up at 3+ with a celebration
- **Results** — correct/total, longest streak, skipped count, and restart quiz

## Demo Video

<!-- Replace with your walkthrough link -->



https://github.com/user-attachments/assets/ed617c43-5d23-4f26-89ae-32cc93d537ed



## Architecture

Layered UI / state / data:

```
UI (Compose screens) → ViewModel (StateFlow) → Repository → Retrofit API
```

- **Hilt** for DI
- **Single activity** with Navigation Compose (Splash → Quiz → Results)
- **QuizViewModel** owns quiz state: selection, streaks, skip counts, auto-advance

## Run

1. Open the project in Android Studio
2. Sync Gradle
3. Run on an emulator or device (API 24+)

Requires network access to load questions from the API.

## Tech

Kotlin · Jetpack Compose · Material 3 · Hilt · Retrofit · OkHttp · Navigation Compose · Coroutines / Flow
