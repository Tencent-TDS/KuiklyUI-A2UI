// A2UI Standalone Project — placeholder root build script.
//
// The real root build logic lives in *variant-specific* files that are
// selected by the matching settings script (see rootProject.buildFileName
// in `settings.gradle.kts` / `settings.2.0.ohos.gradle.kts`):
//
//   build.2.1.21.gradle.kts     ← default (Android + iOS Kotlin 2.1.21)
//   build.2.0.ohos.gradle.kts   ← HarmonyOS (Kotlin 2.0.21-KBA-010)
//
// This file is kept only so IDE tooling that scans for `build.gradle.kts`
// at the project root doesn't complain when the settings buildFileName
// hasn't been applied yet.
