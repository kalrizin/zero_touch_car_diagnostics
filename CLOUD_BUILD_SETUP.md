# Cloud Build Setup for Android APK

This document describes the GitHub Actions workflow that automatically builds the Android APK in the cloud.

## Overview

The repository now has automated CI/CD pipelines that build the Android APK whenever code is pushed to any branch. The builds run on GitHub-hosted runners (Ubuntu) and produce APK artifacts that can be downloaded from the Actions tab.

## Workflow Files

### 1. `.github/workflows/build-release.yml`
Main workflow for building Android APK releases.

**Triggers:**
- Push to any branch
- Pull requests to `main`
- Manual dispatch via GitHub UI

**Configuration:**
- **Flutter Version:** 3.24.0 (stable)
- **Dart SDK:** 3.5.0 (bundled with Flutter 3.24.0)
- **Java Version:** 17 (Temurin distribution)
- **Build Command:** `flutter build apk --release`

**Key Features:**
- Automatic dependency installation
- Optional keystore signing (via secrets)
- APK artifact upload for easy download
- Runs on every push for continuous integration

### 2. `.github/workflows/dart.yml`
Workflow for Dart analysis and testing.

**Triggers:**
- Push to `main`
- Pull requests to `main`

**Steps:**
- Runs `flutter analyze` for static code analysis
- Runs `flutter test` for unit tests

## Fixed Issues

The following issues were resolved to enable successful cloud builds:

1. **Dart SDK Version Mismatch**
   - **Problem:** `pubspec.yaml` specified SDK version `^3.10.4`, but Flutter 3.24.0 includes Dart 3.5.0
   - **Solution:** Updated SDK constraint to `>=3.5.0 <4.0.0`

2. **Flutter Version Update**
   - **Previous:** Flutter 3.16.0
   - **Updated:** Flutter 3.24.0 (aligned with referenced commit 13334df4)

3. **Java Version Alignment**
   - **Previous:** Java 21
   - **Updated:** Java 17 (matches Android build.gradle.kts configuration)

4. **Workflow Syntax**
   - Fixed conditional expression for keystore decode step
   - Removed problematic `if: ${{ secrets.KEYSTORE_BASE64 != '' }}` syntax
   - Implemented bash-level conditional instead

## Build Artifacts

After each successful build, the APK is uploaded as a GitHub Actions artifact:
- **Artifact Name:** `ZTCD_v1.32.11.beta.apk`
- **Location:** `build/app/outputs/flutter-apk/app-release.apk`
- **Retention:** 90 days (GitHub default)

## Accessing Build Artifacts

1. Go to the [Actions tab](https://github.com/kalrizin/zero_touch_car_diagnostics/actions)
2. Click on a completed workflow run
3. Scroll to the "Artifacts" section at the bottom
4. Download the APK file

## Signing Configuration (Optional)

For production releases, you can configure keystore signing via GitHub Secrets:

1. **KEYSTORE_BASE64:** Base64-encoded keystore file
   ```bash
   base64 -i your-keystore.jks | pbcopy  # macOS
   base64 -i your-keystore.jks           # Linux
   ```

2. **KEYSTORE_PASSWORD:** Keystore password
3. **KEY_ALIAS:** Key alias
4. **KEY_PASSWORD:** Key password

The workflow will automatically use these secrets if available, otherwise it builds with debug signing.

## Manual Workflow Dispatch

You can manually trigger a build via the GitHub UI:
1. Go to Actions → Build and Upload Release APK
2. Click "Run workflow"
3. Select the branch
4. Click "Run workflow"

## Requirements

- **Repository Access:** Write access to trigger workflows
- **Workflow Approval:** For pull requests from forks, workflows need approval
- **Secrets:** Optional signing secrets (see above)

## Troubleshooting

### Build fails with "SDK version solving failed"
- Ensure `pubspec.yaml` SDK constraint matches the Flutter version in the workflow
- Current: `sdk: '>=3.5.0 <4.0.0'` for Flutter 3.24.0

### Keystore errors during build
- Verify all four keystore secrets are configured correctly
- Check that keystore file is properly base64-encoded
- Ensure keystore password and key password are correct

### Workflow shows "action_required"
- For pull requests from forks, maintainers must approve workflow runs
- For branch protection, check repository settings

## Version Information

- **Flutter SDK:** 3.24.0
- **Dart SDK:** 3.5.0
- **Java:** 17 (Temurin)
- **Android Compile SDK:** 36
- **Min SDK:** (defined by Flutter)
- **Target SDK:** (defined by Flutter)

## References

- Referenced Commit: [13334df4](https://github.com/kalrizin/zero_touch_car_diagnostics/commit/13334df4afa2d7350e2770f0ffaf835a415953c2)
- Flutter Documentation: https://docs.flutter.dev/deployment/android
- GitHub Actions: https://docs.github.com/en/actions
