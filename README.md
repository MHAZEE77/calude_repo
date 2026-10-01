# Silky Systems Warehouse KMP

Secured Kotlin Multiplatform warehouse application for Android and iOS. The Android application includes receiving and stock addition, put-away, picking, packing, product lookup, barcode scanning, authentication, and warehouse selection.

## Project structure

- `androidApp`: Android application, XML screens, scanner flow, authentication, and Android security controls.
- `sharedLogic`: shared domain, API, repository, DTO, and use-case code.
- `sharedUI`: shared Compose Multiplatform UI code.
- `iosApp`: iOS application entry point.
- `scripts/security_gate.py`: fail-fast static security and release validation.

## Required local configuration

Do not commit credentials, keystores, signing passwords, API tokens, or reCAPTCHA keys.

Set the following environment variables or Gradle properties before building a production release:

```text
WMS_PRODUCTION_BASE_URL=https://your-production-host/
WMS_QA_BASE_URL=https://your-qa-host/
WMS_RECAPTCHA_SITE_KEY=your-site-key
WMS_KEYSTORE_FILE=/absolute/path/to/release-keystore.jks
WMS_KEYSTORE_PASSWORD=stored-outside-source-control
WMS_KEY_ALIAS=release-key-alias
WMS_KEY_PASSWORD=stored-outside-source-control
```

The repository includes `keystore.properties.example` as a reference only. The actual keystore and secrets must be supplied by CI or an approved local secret store.

## Build commands

```bash
python3 scripts/security_gate.py
./gradlew :androidApp:testDebugUnitTest
./gradlew :androidApp:lintDebug
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:verifyReleaseConfiguration
./gradlew :androidApp:assembleRelease
```

The release task fails when HTTPS, reCAPTCHA, or signing configuration is missing or unsafe.

## Security behavior

- Passwords are never stored.
- Session tokens, company data, user data, and warehouse selections are encrypted with Android Keystore AES-GCM.
- Cloud backup and cleartext network traffic are disabled.
- Only the launcher activity is exported.
- API bodies, headers, tokens, and credentials are not logged.
- The scanner uses camera or manual entry. The unprotected vendor broadcast receiver was removed.
- The release build is non-debuggable, minified, resource-shrunk, and restricted to production ARM ABIs.

## Scanner hardware

The generic camera scanner is operational without a proprietary scanner library. Dedicated WMS terminal buttons or scan engines require a signed vendor SDK or a documented permission-protected integration. Do not restore an unrestricted broadcast receiver.

## Release process

Read `SECURITY_REMEDIATION.md`, `RELEASE_CHECKLIST.md`, and `VALIDATION_REPORT.md` before producing a customer build. Runtime approval still requires the actual QA backend, warehouse roles, barcode devices, supported handheld terminals, and real picking and packing workflows.
