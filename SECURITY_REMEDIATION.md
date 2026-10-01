# Security and Quality Remediation

Date: 12 July 2026

## Implemented corrections

### Release engineering

- Removed the embedded release keystore from the repository.
- Removed signing passwords and aliases from Gradle source.
- Added environment-driven production signing.
- Added a release configuration gate for HTTPS, production URL, reCAPTCHA, and signing.
- Set release builds to `debuggable=false` with code and resource shrinking.
- Restricted release native packaging to ARM device ABIs.
- Restored the official Gradle wrapper JAR and added CI wrapper validation.

### Authentication and session security

- Removed the compiled test account and automatic credential population.
- Removed password persistence.
- Replaced plaintext session preferences with Android Keystore AES-GCM storage.
- Added migration from legacy preferences and deletion of legacy plaintext data.
- Added protected-screen session gates.
- Added strict login response validation before session creation.
- Added safer logout and session clearing while optionally preserving only the remembered email and device token.

### Network and application security

- Removed full Ktor request and response logging.
- Removed direct Android and stdout logging from application paths.
- Disabled cleartext traffic through the manifest and Network Security Configuration.
- Minimized default authentication headers and omitted blank values.
- Added bounded network timeouts and consistent HTTP error handling.
- Disabled Android cloud backup and device-to-device transfer of application data.
- Removed the broad FileProvider and unrestricted cache sharing.
- Changed every internal activity to `exported=false`.
- Reduced the permission set to camera, internet, and vibration.

### Barcode and WMS reliability

- Removed the unprotected scanner broadcast action and proprietary scanner class dependency.
- Replaced it with camera scanning plus a manual-entry fallback.
- Added barcode normalization, blank rejection, length limits, and control-character rejection.
- Prevented camera denial from terminating the application.
- Added safe handling for missing activity results, products, locations, quantities, and warehouse lists.
- Added unit tests for barcode and login validation.

### UI, accessibility, and localization

- Corrected the login field types, autofill metadata, keyboard actions, password visibility semantics, loading state, and touch targets.
- Replaced hardcoded layout text and hints with localized resources.
- Added image semantics or explicit decorative status to image controls.
- Replaced directional left and right layout properties with start and end equivalents where applicable.
- Added Arabic resources for new security, login, and scanning states.

## External actions still required

These actions cannot be completed in source code alone:

1. Rotate the account password that was supplied for testing and invalidate existing sessions.
2. Provision the production signing key through an approved secret-management process.
3. Supply the correct production and QA base URLs.
4. Supply and validate the Android reCAPTCHA site key for the production package.
5. Run payment-free WMS UAT on real supported devices and scanner hardware.
6. Confirm API authorization by role, tenant, company, and warehouse on the backend.
7. Run dependency and vulnerability scanning from a network-enabled CI runner.
8. Complete Arabic and English screenshot regression on all supported form factors.

## Architecture note

The project still contains both legacy Android XML code and newer KMP or Compose code. This remediation secures the active paths without redesigning the product. The next controlled modernization should consolidate networking, navigation, design tokens, storage, and error models domain by domain.
