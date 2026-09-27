#include <jni.h>
#include <string>
#include <fstream>
#include <sys/ptrace.h>
#include <unistd.h>

static bool hasTracer() {
    std::ifstream f("/proc/self/status");
    std::string line;
    while (std::getline(f, line)) {
        if (line.rfind("TracerPid:", 0) == 0) {
            return line.find_first_not_of(" \t", 10) != std::string::npos &&
                   line.substr(line.find_last_of(" \t")+1) != "0";
        }
    }
    return false;
}

static bool exists(const char* p) { return access(p, F_OK) == 0; }

extern "C" JNIEXPORT jboolean JNICALL
Java_ng_divstudio_divcut_security_SecurityGuard_nativeInstrumentationDetected(
        JNIEnv*, jobject) {
    // Fail closed on a traced process or common instrumentation artifacts.
    if (hasTracer()) return JNI_TRUE;
    const char* paths[] = {
        "/data/local/tmp/frida-server",
        "/data/local/tmp/re.frida.server",
        "/system/framework/XposedBridge.jar",
        "/data/adb/modules",
        "/usr/lib/libsubstrate.so"
    };
    for (const char* p : paths) if (exists(p)) return JNI_TRUE;
    return JNI_FALSE;
}
