package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class AdvancedToolItem(val title: String, val description: String, val category: String, val icon: ImageVector)

@Composable
fun AdvancedProductionToolsSheet(onDismiss: () -> Unit) {
    val tools = remember { listOf(
        AdvancedToolItem("Multi-Camera", "Sync and switch camera angles.", "EDIT", Icons.Default.Videocam),
        AdvancedToolItem("Nested Timelines", "Build scenes inside master sequences.", "EDIT", Icons.Default.AccountTree),
        AdvancedToolItem("Compound Clips", "Group complex edits into reusable units.", "EDIT", Icons.Default.Layers),
        AdvancedToolItem("Adjustment Layers", "Grade or effect multiple clips together.", "COLOR", Icons.Default.Tune),
        AdvancedToolItem("Bezier Motion", "Smooth keyframed camera and object paths.", "MOTION", Icons.Default.Gesture),
        AdvancedToolItem("Proxy Workflow", "Lightweight media for faster editing.", "PERFORMANCE", Icons.Default.Speed),
        AdvancedToolItem("Background Render", "Render heavy work without blocking editing.", "PERFORMANCE", Icons.Default.Memory),
        AdvancedToolItem("Auto Scene Detect", "Detect shots and scene boundaries.", "AI", Icons.Default.AutoAwesome),
        AdvancedToolItem("AI Rough Cut", "Generate an editable first cut.", "AI", Icons.Default.ContentCut),
        AdvancedToolItem("Continuity Check", "Find possible visual and dialogue continuity issues.", "AI", Icons.Default.FactCheck),
        AdvancedToolItem("Object Removal", "Prepare tracked masks for unwanted objects.", "VFX", Icons.Default.RemoveCircle),
        AdvancedToolItem("AI Rotoscope", "Isolate subjects for compositing.", "VFX", Icons.Default.Person),
        AdvancedToolItem("Sky Replacement", "Replace skies while preserving foreground detail.", "VFX", Icons.Default.Cloud),
        AdvancedToolItem("Color Curves", "Fine tune luminance and RGB channels.", "COLOR", Icons.Default.ShowChart),
        AdvancedToolItem("HSL Control", "Target hue, saturation and luminance.", "COLOR", Icons.Default.Colorize),
        AdvancedToolItem("LUT Manager", "Import and organize cinematic LUTs.", "COLOR", Icons.Default.Style),
        AdvancedToolItem("Dialogue Cleanup", "Prepare speech for noise reduction.", "AUDIO", Icons.Default.RecordVoiceOver),
        AdvancedToolItem("Audio Mixer", "Balance dialogue, music, Foley and ambience.", "AUDIO", Icons.Default.GraphicEq),
        AdvancedToolItem("Beat Detection", "Sync edits to music beats.", "AUDIO", Icons.Default.MusicNote),
        AdvancedToolItem("Transcript Editing", "Search dialogue and jump to timeline moments.", "TEXT", Icons.Default.Description),
        AdvancedToolItem("Subtitle Translation", "Create translated subtitle tracks.", "TEXT", Icons.Default.Translate),
        AdvancedToolItem("Animation Compositor", "Assemble rendered scenes and image sequences.", "ANIMATION", Icons.Default.Brush),
        AdvancedToolItem("Film Grain + Halation", "Add controlled film texture.", "FINISH", Icons.Default.MovieFilter),
        AdvancedToolItem("Version History", "Restore previous edit states.", "PROJECT", Icons.Default.History),
        AdvancedToolItem("Project Backup", "Create portable project backups.", "PROJECT", Icons.Default.Backup),
        AdvancedToolItem("AI Command Bar", "Turn natural-language instructions into editor actions.", "AI", Icons.Default.Psychology),
        AdvancedToolItem("Diagnostics", "Scan media, export, API and runtime failures.", "SYSTEM", Icons.Default.BugReport)
    )}
    var enabled by remember { mutableStateOf(setOf<String>()) }
    var filter by remember { mutableStateOf("ALL") }
    val categories = listOf("ALL", "EDIT", "AI", "VFX", "COLOR", "AUDIO", "TEXT", "ANIMATION", "PERFORMANCE", "PROJECT", "SYSTEM", "FINISH", "MOTION")
    val visible = tools.filter { filter == "ALL" || it.category == filter }

    AlertDialog(onDismissRequest = onDismiss, containerColor = CinemaSlate900,
        title = { Column {
            Text("ADVANCED PRODUCTION SUITE", color = GoldCinema, fontWeight = androidx.compose.ui.text.font.FontWeight.Black, fontSize = 17.sp)
            Text("Professional tools for DIV EDIT AI", color = CinemaSlate400, fontSize = 11.sp)
        }},
        text = { Column(Modifier.fillMaxWidth()) {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                categories.forEach { c -> FilterChip(selected = filter == c, onClick = { filter = c }, label = { Text(c, fontSize = 8.sp) }) }
            }
            Spacer(Modifier.height(8.dp))
            LazyColumn(Modifier.heightIn(max = 500.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                items(visible) { tool ->
                    val on = tool.title in enabled
                    Surface(color = CinemaSlate950, shape = MaterialTheme.shapes.small) {
                        Row(Modifier.fillMaxWidth().padding(9.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(tool.icon, null, tint = if (on) AnamorphicCyan else CinemaSlate300, modifier = Modifier.size(21.dp))
                            Spacer(Modifier.width(9.dp))
                            Column(Modifier.weight(1f)) {
                                Text(tool.title, color = CinemaSlate50, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 12.sp)
                                Text(tool.description, color = CinemaSlate400, fontSize = 9.sp)
                            }
                            Switch(checked = on, onCheckedChange = { enabled = if (it) enabled + tool.title else enabled - tool.title })
                        }
                    }
                }
            }
        }},
        confirmButton = { Button(onClick = onDismiss) { Text("DONE") } },
        dismissButton = { TextButton(onClick = { enabled = emptySet() }) { Text("RESET") } }
    )
}
