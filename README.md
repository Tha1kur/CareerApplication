# Career Guidance Community

**Status: work in progress.** An Android app written in Kotlin with Jetpack Compose. The existing implementation includes career quizzes and recommendations, college and scholarship screens, profiles, parental views, and gamification.

This repository preserves an unfinished project. Feature presence in source code does not mean the flows have been validated end to end.

## Open and build

1. Open this repository folder in Android Studio.
2. Configure an installed Android SDK in your local `local.properties`, or let Android Studio create it. The app targets and compiles against SDK 36; its minimum SDK is 24.
3. Let Android Studio synchronize the Gradle project. Use the checked-in Gradle wrapper. The wrapper currently specifies Gradle 8.13 and the version catalog specifies Android Gradle Plugin 8.13.2.
4. Select a compatible Gradle JDK in Android Studio, then build or run the `app` module.

From a configured terminal, `./gradlew :app:assembleDebug` builds a debug APK. A fresh build and toolchain compatibility check are still required; this command was not run during the storage cleanup.

## Current baseline

The 2026-09-29 cleanup preserved all existing source and local changes, including the pre-existing Android Gradle Plugin version bump. Rebuildable Gradle/Kotlin caches and build outputs were deleted. Android Studio settings and SDK paths remain local and are excluded from new commits.

The build configuration mixes Kotlin plugin versions and declares some dependencies both through the version catalog and inline. Resolve and test that configuration before claiming reproducibility. The repository currently includes Android Studio's example tests; broader feature coverage remains to be established.

See [ROADMAP.md](ROADMAP.md) for the next focused improvements. Existing Git history has been preserved.
