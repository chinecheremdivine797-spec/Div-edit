package ng.divstudio.divcut.security;

import android.app.Activity;
import android.os.Build;
import android.os.Debug;
import android.view.WindowManager;
import java.io.File;

public final class SecurityGuard {
    static { System.loadLibrary("divsecurity"); }
    private SecurityGuard() {}

    public static void harden(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

    public static boolean verifyRuntime() {
        if (Debug.isDebuggerConnected() || Debug.waitingForDebugger()) return false;
        if (nativeInstrumentationDetected()) return false;
        if (rootHeuristics() || emulatorHeuristics()) return false;
        return true;
    }

    public static void enforce() {
        if (!verifyRuntime()) Runtime.getRuntime().exit(137);
    }

    private static boolean rootHeuristics() {
        String[] paths = {"/system/bin/su","/system/xbin/su","/sbin/su","/system/bin/magisk","/sbin/magisk"};
        for (String p : paths) if (new File(p).exists()) return true;
        String tags = Build.TAGS == null ? "" : Build.TAGS;
        return tags.toLowerCase().contains("test-keys");
    }

    private static boolean emulatorHeuristics() {
        String fp = String.valueOf(Build.FINGERPRINT).toLowerCase();
        String model = String.valueOf(Build.MODEL).toLowerCase();
        return fp.contains("generic") || fp.contains("emulator") || model.contains("sdk") || model.contains("emulator");
    }

    private static native boolean nativeInstrumentationDetected();
}
