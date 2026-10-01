#!/usr/bin/env python3
"""Fail-fast static release gate for the Silky WMS Android project."""
from __future__ import annotations

import json
import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ANDROID_NS = "{http://schemas.android.com/apk/res/android}"
failures: list[str] = []
passes: list[str] = []


def check(condition: bool, message: str) -> None:
    (passes if condition else failures).append(message)


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8", errors="replace")

manifest_path = ROOT / "androidApp/src/main/AndroidManifest.xml"
manifest = ET.parse(manifest_path).getroot()
application = manifest.find("application")
check(application is not None, "Application manifest node exists")
if application is not None:
    check(application.get(ANDROID_NS + "allowBackup") == "false", "Cloud backup is disabled")
    check(application.get(ANDROID_NS + "usesCleartextTraffic") == "false", "Cleartext traffic is disabled")
    check(application.get(ANDROID_NS + "networkSecurityConfig") == "@xml/network_security_config", "Network security config is enforced")
    exported = [
        node.get(ANDROID_NS + "name")
        for node in list(application)
        if node.get(ANDROID_NS + "exported") == "true"
    ]
    check(exported == ["com.silkysys.warehouse.app.autentcation.splash.LauncherActivity"], "Only launcher activity is exported")
    check(not application.findall("provider"), "No broad FileProvider is declared")

permissions = {node.get(ANDROID_NS + "name") for node in manifest.findall("uses-permission")}
check(permissions <= {"android.permission.CAMERA", "android.permission.INTERNET", "android.permission.VIBRATE"}, "Permission set is minimized")

build_file = read("androidApp/build.gradle.kts")
check('release {' in build_file and 'isDebuggable = false' in build_file, "Release build is non-debuggable")
check('isMinifyEnabled = true' in build_file and 'isShrinkResources = true' in build_file, "Release shrinking is enabled")
check('verifyReleaseConfiguration' in build_file, "Release configuration gate is present")
check(not re.search(r'(storePassword|keyPassword)\s*=\s*"[^\"]+"', build_file), "Signing passwords are not embedded")

source_roots = [ROOT / "androidApp/src", ROOT / "sharedLogic/src"]
source_files = [p for root in source_roots for p in root.rglob("*") if p.suffix in {".kt", ".java", ".xml", ".kts"}]
source_text = "\n".join(p.read_text(encoding="utf-8", errors="replace") for p in source_files)
for pattern, description in [
    (r'(?:email|username)\s*=\s*"[^"\n]+@[^"\n]+"', "No email account is hardcoded in source"),
    (r'(?:password|passcode|secret)\s*=\s*"[^"\n]{4,}"', "No password-like value is hardcoded in source"),
    (r"LogLevel\.(ALL|BODY|HEADERS)", "No full network logging is enabled"),
    (r"\bLog\.[diewv]\(", "No Android runtime logging calls remain"),
    (r"println\(", "No stdout response logging remains"),
    (r"savePref\.password|passwordKey", "Passwords are not persisted"),
    (r"android\.device\.ScanDevice", "Unverified proprietary scanner class is not linked"),
    (r"scan\.rcv\.message", "Unprotected scanner broadcast action is removed"),
]:
    check(re.search(pattern, source_text, re.IGNORECASE) is None, description)

save_pref = read("androidApp/src/main/kotlin/com/silkysys/warehouse/app/ui/utility/SavePref.kt")
check("AES/GCM/NoPadding" in save_pref and "AndroidKeyStore" in save_pref, "Session storage uses Android Keystore AES-GCM")
check("fun hasActiveSession" in save_pref and "fun clearSession" in save_pref, "Session lifecycle controls are implemented")

keystores = list(ROOT.rglob("*.jks")) + list(ROOT.rglob("*.keystore"))
check(not keystores, "No signing keystore is stored in the project")

bad_xml: list[str] = []
hardcoded = 0
missing_semantics = 0
image_tags = {"ImageView", "ImageButton", "com.google.android.material.imageview.ShapeableImageView", "de.hdodenhof.circleimageview.CircleImageView"}
for path in (ROOT / "androidApp/src/main/res").rglob("*.xml"):
    try:
        root = ET.parse(path).getroot()
    except Exception as exc:
        bad_xml.append(f"{path.relative_to(ROOT)}: {exc}")
        continue
    if "/layout/" not in path.as_posix():
        continue
    for node in root.iter():
        tag = node.tag.split("}")[-1]
        if tag in image_tags and node.get(ANDROID_NS + "contentDescription") is None:
            missing_semantics += 1
        for attr in ("text", "hint", "contentDescription"):
            value = node.get(ANDROID_NS + attr)
            if value and value.strip() and not value.startswith(("@", "?", "%")):
                hardcoded += 1
check(not bad_xml, "All Android XML resources parse")
check(hardcoded == 0, "All layout text and hints use resources")
check(missing_semantics == 0, "All image controls declare accessibility semantics")

protected_activities = [
    "AddStockActivity.kt", "MainActivity.kt", "ProfileActivity.kt", "UpdatePasswordActivity.kt",
    "AddPackActivity.kt", "PackActivity.kt", "PackDetailsActivity.kt", "PickActivity.kt",
    "PickItemsActivity.kt", "PutAwayActivity.kt", "ProductDetailsActivity.kt", "BarcodeScanner.kt",
]
for filename in protected_activities:
    matches = list((ROOT / "androidApp/src/main/kotlin").rglob(filename))
    check(bool(matches) and all("requireAuthenticatedSession()" in p.read_text(encoding="utf-8") for p in matches), f"Session gate: {filename}")

report = {
    "passed": len(passes),
    "failed": len(failures),
    "checks": [{"status": "PASS", "message": x} for x in passes] + [{"status": "FAIL", "message": x} for x in failures],
}
print(json.dumps(report, indent=2))
sys.exit(1 if failures else 0)
