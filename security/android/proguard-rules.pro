# DIV EDIT release hardening
-allowaccessmodification
-repackageclasses
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,AnnotationDefault
-keep class ng.divstudio.divcut.security.** { *; }
# Keep JNI entrypoint while allowing implementation obfuscation elsewhere.
-keepclasseswithmembers,includedescriptorclasses class * {
    native <methods>;
}
