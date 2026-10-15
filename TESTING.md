# SNAIL Android Testing Builds

Current testing version: **2.1** (versionCode 8).

## Get the latest APK

1. Open the [Android APK build workflow](https://github.com/OrbitEast/snail/actions/workflows/android-apk.yml).
2. Select the newest completed run on the `main` branch.
3. In **Artifacts**, download the artifact named `SNAIL-v<version>-debug-apk` (for example, `SNAIL-v2.1-debug-apk`). The artifact name updates automatically when the app version changes.
4. Extract the ZIP and install `app-debug.apk` on your Android device.

## Build behavior

- Every push to `main` starts a debug APK build.
- You can start one manually from the workflow page using **Run workflow**.
- APKs are stored as GitHub Actions artifacts for 30 days.
- The APK is a **debug/testing build**, not a signed production release.
- A failed build will not have an APK artifact. Open the workflow run and inspect the build logs.

## Versioning

The current app version is configured in `app/build.gradle.kts`. For the next test release, increase `versionCode` and update `versionName` before committing. Android requires each install-over update to have a higher version code.
