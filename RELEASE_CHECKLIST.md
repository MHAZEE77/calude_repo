# WMS Release Checklist

A release is approved only when every applicable item passes for the exact signed artifact.

## Configuration

- [ ] Production URL uses HTTPS and points to the approved production tenant.
- [ ] QA and production configurations are separated.
- [ ] reCAPTCHA site key matches the Android package and production environment.
- [ ] Release keystore is supplied outside source control.
- [ ] Signing certificate ownership, expiry, backup, and recovery are documented.
- [ ] Supplied test account password has been rotated and prior sessions invalidated.

## Automated gates

- [ ] `python3 scripts/security_gate.py`
- [ ] `./gradlew --validate-wrapper`
- [ ] `./gradlew :androidApp:testDebugUnitTest`
- [ ] `./gradlew :androidApp:lintDebug`
- [ ] `./gradlew :androidApp:verifyReleaseConfiguration`
- [ ] `./gradlew :androidApp:assembleRelease`
- [ ] Dependency and secret scans pass.
- [ ] Release APK or AAB is scanned and its SHA-256 recorded.

## Functional WMS coverage

- [ ] Login, logout, token refresh, expired session, lockout, and invalid credentials.
- [ ] Tenant, company, role, and warehouse authorization boundaries.
- [ ] Product lookup, valid barcode, invalid barcode, duplicate scan, and rapid repeated scan.
- [ ] Receiving or add stock, warehouse selection, bin selection, and quantity limits.
- [ ] Put-away assignment, unassignment, invalid bin, duplicate bin, and concurrency conflict.
- [ ] Picking waiting and started lists, partial quantity, over-pick prevention, and completion.
- [ ] Packing creation, item validation, quantity validation, completion, and re-open restrictions.
- [ ] Offline, timeout, reconnect, retry, process death, and duplicate-request protection.

## Device and UX coverage

- [ ] Supported handheld terminals and tablets.
- [ ] Android API levels used by field devices.
- [ ] Camera scanner and every approved integrated scanner model.
- [ ] Permission denial and restricted manual-entry mode.
- [ ] Arabic RTL and English LTR.
- [ ] Large font, screen reader, focus order, contrast, and 48dp touch targets.
- [ ] Low memory, long shift, large catalog, rapid scanning, and weak connectivity.

## Final approval

- [ ] Engineering approval.
- [ ] QA approval.
- [ ] Security approval.
- [ ] Product and warehouse operations approval.
- [ ] Exact release artifact hash approved and archived.
