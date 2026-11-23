# DroidClone Studio V1.0 — Alpha starter

An Android-native, offline-first APK inspection and clone-configuration project.

## Build method: GitHub Actions only

No Android Studio or local Gradle installation is required. GitHub Actions builds the Android debug APK in the cloud.

### Upload this project to GitHub

1. Sign in to GitHub and create a new **empty** repository named `DroidCloneStudio`.
2. Extract this ZIP on your device or computer.
3. Upload the **contents** of the `DroidCloneStudio` folder to the repository root. Keep the `.github/workflows/android.yml` file and its folder structure intact.
4. Commit the files to the `main` branch.

### Run the cloud build

1. Open the repository's **Actions** tab.
2. Select **Android APK Build**.
3. Select **Run workflow** and confirm the `main` branch.
4. Wait for the workflow to finish successfully.
5. Open the completed workflow run and download the `DroidCloneStudio-debug` artifact.
6. Extract the downloaded artifact ZIP. It contains `app-debug.apk`.

The workflow also runs automatically when code is pushed to `main` or `master`.

## Current alpha capabilities
- Select a single `.apk` through Android's Storage Access Framework.
- Inspect the source APK's app label, package ID, and version.
- Edit the proposed clone label and package ID in the UI.
- No APK is uploaded and no API key is needed.

## Important status
**This alpha does not yet rebuild, repackage, re-sign, or install a cloned APK.** The UI deliberately labels this limitation. A reliable on-device rebuild engine is the next milestone. APKs with anti-tamper, licensing, DRM, or signature-integrity checks are out of scope; changing an app's package ID can also break hard-coded package references, native code, providers, deep links, and backend integrations.

## GitHub Actions build details
- Runner: `ubuntu-latest`
- JDK: Temurin 17
- Gradle: 8.11.1
- Build task: `assembleDebug`
- Artifact: `DroidCloneStudio-debug`

## Next engineering milestone
Implement and test a compatible on-device APK transformation pipeline (binary AndroidManifest/resource handling, package-reference rewrite, rebuild, v2/v3 signing, and post-build verification) before enabling the Build Clone button.
