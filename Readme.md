# FormulaApp

FormulaApp is an Android application for working with mathematical formulas and calculations. It provides a clean UI for creating, editing, and evaluating formulas, and is built with Kotlin and Gradle using a standard Android app module layout.

**Key Features:**
- **Formula Editing:** Create and edit mathematical expressions.
- **Evaluation Engine:** Compute results for expressions with a reliable evaluator.
- **Modern Android:** Kotlin, Gradle, and AndroidX dependencies.

**Prerequisites:**
- **JDK:** Java 11 or newer installed and `JAVA_HOME` set.
- **Android SDK:** Installed (recommended via Android Studio).
- **Gradle Wrapper:** Project includes `gradlew` so no global Gradle install required.

**Quick Start (Windows PowerShell):**

1. Clone the repo:

```
git clone <repo-url> FormulaApp
cd FormulaApp
```

2. Open in Android Studio: use `File > Open` and select the project folder.

3. Build and run from command line (debug):

```powershell
.
\gradlew.bat assembleDebug
.
\gradlew.bat installDebug
```

Or run from Android Studio using the Run configuration.

**Build Release AAB:**

```powershell
.\gradlew.bat bundleRelease
# Output: app/build/outputs/bundle/release/app-release.aab
```

**Project Structure (important files):**
- **[app](app)**: Main Android app module.
- **[app/build.gradle.kts](app/build.gradle.kts)**: Module build script.
- **build.gradle.kts**: Top-level Gradle Kotlin DSL build file.
- **gradle/** and **gradlew(.bat)**: Gradle wrapper and properties.

**Testing:**
- Unit tests: run `.
\gradlew.bat test`.
- Instrumented UI tests: run `.
\gradlew.bat connectedAndroidTest` (requires device/emulator).

**Contributing:**
- Fork the repo and create feature branches.
- Open a Pull Request with a clear description and tests where applicable.
- Follow existing code style (Kotlin idioms) and add small, focused commits.

**License & Attribution:**
- Add or update licensing info in this repository root if required. If no license file exists, assume "All Rights Reserved" until a license is added.

**Next Steps / Notes:**
- If you want, I can add a CHANGELOG, contributing guidelines, or CI workflow for automated builds.

