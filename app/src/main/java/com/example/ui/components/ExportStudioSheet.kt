package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class ExportStudioState(
    val resolution: String = "1080p",
    val fps: Int = 30,
    val quality: String = "High",
    val progress: Int = 0,
    val running: Boolean = false,
    val message: String = "Ready to export"
)

@Composable
fun ExportStudioSheet(
    state: ExportStudioState,
    onResolutionChange: (String) -> Unit,
    onFpsChange: (Int) -> Unit,
    onQualityChange: (String) -> Unit,
    onExport: () -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!state.running) onDismiss() },
        title = { Text("Export Studio") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Resolution: ${state.resolution}")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("720p", "1080p", "1440p", "4K").forEach { value ->
                        TextButton(onClick = { onResolutionChange(value) }, enabled = !state.running) { Text(value) }
                    }
                }
                Text("Frame rate: ${state.fps} FPS")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(24, 25, 30, 50, 60).forEach { value ->
                        TextButton(onClick = { onFpsChange(value) }, enabled = !state.running) { Text(value.toString()) }
                    }
                }
                Text("Quality: ${state.quality}")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Standard", "High", "Maximum").forEach { value ->
                        TextButton(onClick = { onQualityChange(value) }, enabled = !state.running) { Text(value) }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(state.message, style = MaterialTheme.typography.bodyMedium)
                LinearProgressIndicator(
                    progress = { state.progress.coerceIn(0, 100) / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("${state.progress.coerceIn(0, 100)}%")
            }
        },
        confirmButton = {
            if (state.running) Button(onClick = onCancel) { Text("Cancel") }
            else if (state.message.contains("failed", ignoreCase = true)) Button(onClick = onRetry) { Text("Retry") }
            else Button(onClick = onExport) { Text("Export MP4") }
        },
        dismissButton = { if (!state.running) OutlinedButton(onClick = onDismiss) { Text("Close") } }
    )
}
