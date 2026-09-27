# DIV EDIT Mobile Security Baseline

Security target: OWASP MASVS-L2 + MASVS-RESILIENCE.

Implemented foundation:
- Android FLAG_SECURE for protected editor surfaces.
- Native JNI runtime checks for debugger/instrumentation indicators.
- Root/emulator/test-key heuristics.
- Native symbol stripping and stack protection.
- R8/ProGuard hardening.
- Media validation must occur before native decoder input.
- No API secrets are embedded in the web bundle.
- Server-issued short-lived credentials are required for authenticated APIs.

Important limitations:
- Play Integrity verification is a server-side control and must be wired to the production backend before release.
- TLS public-key pinning must use the production API hostname and production SPKI pins; placeholders are intentionally not shipped.
- HMAC request signing requires a server-issued rotating secret; never hard-code a signing secret in the APK.
- SafetyNet is deprecated; Play Integrity is the required Android attestation API.
- Anti-debugging is a resilience signal, not a substitute for server-side authorization.
