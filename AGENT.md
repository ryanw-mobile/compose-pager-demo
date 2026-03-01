# AI Agent Instructions

## Project Overview
This is a Jetpack Compose Android application that demonstrates how to implement an **Endless Horizontal Pager** with advanced animations and haptic feedback. It serves as a reference for using the modern Jetpack Compose `HorizontalPager` without the need for traditional XML views or complex adapters.

### Main Technologies
- **Language:** Kotlin 2.3.10
- **UI Framework:** Jetpack Compose (Bill of Materials 2026.02.01)
- **Theme:** Material Design 3
- **Build System:** Gradle (Kotlin DSL)
- **Static Analysis:** Detekt, Kotlinter

### Architecture
- **MainActivity:** Entry point that sets up the theme and triggers `MainScreen`.
- **MainScreen:** Composable that defines the list of drawables and centers the `AnimatedViewPager`.
- **AnimatedViewPager:** The core component that manages an endless `HorizontalPager` by using a large multiplier. It also handles haptic feedback on page changes via `snapshotFlow`.
- **PagerAnimationModifier:** A custom modifier that applies `graphicsLayer` transformations (alpha, scale, rotationY) based on the pager's scroll offset.
- **Theme:** Standard Jetpack Compose theme located in `com.rwmobi.composepager.ui.theme`.

---

## Building and Running
The project requires **Java 21** and **Android Studio**.

### Key Gradle Commands
- **Build APK:** `./gradlew assembleDebug`
- **Build Release APK:** `./gradlew assembleRelease` (Note: requires signing configuration, which is guarded in `build.gradle.kts`)
- **Run Unit Tests:** `./gradlew test`
- **Run UI Tests:** `./gradlew connectedDebugAndroidTest`
- **Lint (Detekt):** `./gradlew detekt`
- **Lint (Kotlinter):** `./gradlew lintKotlin`
- **Auto-format Code:** `./gradlew formatKotlin`
- **Run All Checks:** `./gradlew check` (Depends on `detekt`)

---

## Development Conventions

### Coding Style
- **Kotlin-First:** The project follows standard Kotlin coding conventions.
- **Compose Best Practices:**
    - Use of `Modifier` parameters in all public composables.
    - Custom animations are encapsulated in dedicated `Modifier` extensions (e.g., `Modifier.pagerAnimation`).
    - Side effects are handled using `LaunchedEffect` and `snapshotFlow` for state observation.
- **Linting:** Automated formatting and static analysis are part of the build process (`preBuild` depends on `formatKotlin`).

### Testing Practices
- **Robot Pattern:** UI tests in `androidTest` follow the Robot pattern to separate test logic from UI selection.
- **Compose UI Testing:** Uses `createAndroidComposeRule` for interacting with the Compose UI.
- **Managed Devices:** The project is configured to run tests on a managed virtual device (`pixel2Api35`).

### Project Structure
- `app/src/main/java`: Application source code.
- `app/src/androidTest/java`: Instrumented UI tests.
- `app/src/main/res/drawable-nodpi`: Image assets for the pager demo.
- `gradle/libs.versions.toml`: Centralized dependency management.
