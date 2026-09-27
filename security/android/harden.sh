#!/usr/bin/env bash
set -euo pipefail
APP="android/app/src/main"
mkdir -p "$APP/java/ng/divstudio/divcut/security" "$APP/cpp" "$APP/res/xml"
cp security/android/SecurityGuard.kt "$APP/java/ng/divstudio/divcut/security/SecurityGuard.kt"
cp security/android/IntegrityClient.kt "$APP/java/ng/divstudio/divcut/security/IntegrityClient.kt"
cp security/android/KeystoreStore.kt "$APP/java/ng/divstudio/divcut/security/KeystoreStore.kt"
cp security/android/native/divsecurity.cpp "$APP/cpp/divsecurity.cpp"
cp security/android/native/CMakeLists.txt "$APP/cpp/CMakeLists.txt"
cp security/android/proguard-rules.pro android/app/proguard-rules-security.pro

# Harden cleartext transport. Production pinning is injected only when real pins are supplied.
cat > "$APP/res/xml/network_security_config.xml" <<'XML'
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
  <base-config cleartextTrafficPermitted="false">
    <trust-anchors>
      <certificates src="system"/>
    </trust-anchors>
  </base-config>
</network-security-config>
XML

python3 - <<'PY'
from pathlib import Path
p=Path("android/app/build.gradle")
s=p.read_text()
if "com.google.android.play:integrity:" not in s:
    s=s.replace("dependencies {", 'dependencies {\n    implementation "com.google.android.play:integrity:1.6.0"', 1)
if "externalNativeBuild" not in s:
    marker="android {"
    insert='''android {\n    externalNativeBuild {\n        cmake { path file("src/main/cpp/CMakeLists.txt") }\n    }\n    buildTypes {\n        debug {\n            minifyEnabled false\n            proguardFiles getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro", "proguard-rules-security.pro"\n        }\n        release {\n            minifyEnabled true\n            shrinkResources true\n            proguardFiles getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro", "proguard-rules-security.pro"\n        }\n    }'''
    s=s.replace(marker,insert,1)
p.write_text(s)
PY

python3 - <<'PY'
from pathlib import Path
cands=list(Path("android/app/src/main/java").rglob("MainActivity.java"))+list(Path("android/app/src/main/java").rglob("MainActivity.kt"))
if not cands: raise SystemExit("MainActivity not found")
p=cands[0]; s=p.read_text()
if "SecurityGuard" not in s:
    s=s.replace("import com.getcapacitor.BridgeActivity;", "import com.getcapacitor.BridgeActivity;\nimport android.os.Bundle;\nimport ng.divstudio.divcut.security.SecurityGuard;")
    s=s.replace("{\n}", "{\n    @Override public void onCreate(Bundle state) { super.onCreate(state); SecurityGuard.harden(this); SecurityGuard.enforce(); }\n}")
    p.write_text(s)
PY

# Generate an integrity manifest for bundled web assets.
mkdir -p android/app/src/main/assets
if [ -d dist ]; then
  (cd dist && find . -type f -print0 | sort -z | xargs -0 sha256sum) > android/app/src/main/assets/div-assets.sha256
fi
