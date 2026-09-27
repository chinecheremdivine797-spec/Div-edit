package ng.divstudio.divcut.security

import android.app.Activity
import android.os.Build
import android.os.Debug
import android.view.WindowManager
import java.io.File

object SecurityGuard {
    init { System.loadLibrary("divsecurity") }

    fun harden(activity: Activity) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    fun verifyRuntime(): Boolean {
        if (Debug.isDebuggerConnected() || Debug.waitingForDebugger()) return false
        if (nativeInstrumentationDetected()) return false
        if (rootHeuristics()) return false
        if (emulatorHeuristics()) return false
        return true
    }

    fun enforce(): Unit {
        if (!verifyRuntime()) Runtime.getRuntime().exit(137)
    }

    private fun rootHeuristics(): Boolean {
        val paths = listOf("/system/bin/su","/system/xbin/su","/sbin/su","/system/bin/magisk","/sbin/magisk")
        if (paths.any { File(it).exists() }) return true
        val tags = Build.TAGS ?: ""
        return tags.contains("test-keys", ignoreCase = true)
    }

    private fun emulatorHeuristics(): Boolean {
        val fp = Build.FINGERPRINT.lowercase()
        val model = Build.MODEL.lowercase()
        return fp.contains("generic") || fp.contains("emulator") || model.contains("sdk") || model.contains("emulator")
    }

    private external fun nativeInstrumentationDetected(): Boolean
}
