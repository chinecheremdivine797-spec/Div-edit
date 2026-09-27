#!/usr/bin/env bash
set -euo pipefail
APP="android/app/src/main"
mkdir -p "$APP/java/ng/divstudio/divcut/security" "$APP/cpp" "$APP/res/xml"
cp security/android/SecurityGuard.java "$APP/java/ng/divstudio/divcut/security/SecurityGuard.java"
cp security/android/IntegrityClient.java "$APP/java/ng/divstudio/divcut/security/IntegrityClient.java"
cp security/android/KeystoreStore.java "$APP/java/ng/divstudio/divcut/security/KeystoreStore.java"
cp security/android/native/divsecurity.cpp "$APP/cpp/divsecurity.cpp"
cp security/android/native/CMakeLists.txt "$APP/cpp/CMakeLists.txt"
cp security/android/proguard-rules.pro android/app/proguard-rules-security.pro

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
    s=s.replace("android {", '''android {
    externalNativeBuild {
        cmake { path file("src/main/cpp/CMakeLists.txt") }
    }
    buildTypes {
        debug {
            minifyEnabled false
            shrinkResources false
            proguardFiles getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro", "proguard-rules-security.pro"
        }
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro", "proguard-rules-security.pro"
        }
    }''', 1)
# Explicitly keep debug resource shrinking disabled.
if "android.buildTypes.debug.shrinkResources = false" not in s:
    s += '''\n\n// DIV EDIT security build: resource shrinking requires code shrinking.\nandroid.buildTypes.debug.shrinkResources = false\nandroid.buildTypes.debug.minifyEnabled = false\n'''
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

mkdir -p android/app/src/main/assets
if [ -d dist ]; then
  (cd dist && find . -type f -print0 | sort -z | xargs -0 sha256sum) > android/app/src/main/assets/div-assets.sha256
fi
