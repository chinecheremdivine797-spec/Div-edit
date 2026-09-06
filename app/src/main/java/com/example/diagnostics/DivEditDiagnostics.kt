package com.example.diagnostics

import java.io.File

class DivEditDiagnostics {
    enum class Severity { INFO, WARNING, ERROR }

    data class Diagnostic(
        val severity: Severity,
        val component: String,
        val message: String,
        val repairHint: String? = null
    )

    fun scanInput(file: File): List<Diagnostic> = buildList {
        if (!file.exists()) add(Diagnostic(Severity.ERROR, "MEDIA", "Input file does not exist", "Relink or re-import the source media."))
        else if (file.length() == 0L) add(Diagnostic(Severity.ERROR, "MEDIA", "Input file is empty", "Import a valid media file."))
        else add(Diagnostic(Severity.INFO, "MEDIA", "Media file is present: ${file.name}"))
    }

    fun inspectExport(resultSuccess: Boolean, log: String, error: String? = null): List<Diagnostic> = if (resultSuccess) {
        listOf(Diagnostic(Severity.INFO, "EXPORT", "Export completed successfully"))
    } else {
        listOf(Diagnostic(Severity.ERROR, "EXPORT", error ?: "Export failed", "Inspect the FFmpeg log and retry after fixing the reported input/configuration issue."))
    }
}
