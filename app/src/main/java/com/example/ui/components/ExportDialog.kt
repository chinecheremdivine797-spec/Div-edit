package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import com.example.viewmodel.ExportProgressState

@Composable
fun ExportDialog(
    exportState: ExportProgressState,
    defaultBurnIn: Boolean,
    onStartExport: (codec: String, resolution: String, bitrate: String, burnIn: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCodec by remember { mutableStateOf("Apple ProRes 422 HQ") }
    var selectedResolution by remember { mutableStateOf("4K DCI (4096x2160)") }
    var selectedBitrate by remember { mutableStateOf("Cinema Master (150 Mbps)") }
    var burnInWatermark by remember { mutableStateOf(defaultBurnIn) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .testTag("export_dialog"),
            shape = RoundedCornerShape(12.dp),
            color = CinemaSlate950,
            border = BorderStroke(1.5.dp, GoldCinema)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CinemaSlate900)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(GoldCinemaGlow, CircleShape)
                                .border(1.dp, GoldCinema, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = GoldCinema,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "THEATRICAL CINEMA EXPORT STUDIO",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = CinemaSlate50
                            )
                            Text(
                                text = "Apple ProRes / DCI Master / Festival & Studio Delivery",
                                fontSize = 10.sp,
                                color = GoldCinema
                            )
                        }
                    }

                    if (!exportState.isExporting) {
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = CinemaSlate400)
                        }
                    }
                }

                if (exportState.isExporting || exportState.exportCompleted) {
                    // Export Progress Monitor
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (exportState.exportCompleted) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(AudioEmerald.copy(alpha = 0.2f), CircleShape)
                                    .border(2.dp, AudioEmerald, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AudioEmerald,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "MASTER CUT RENDER COMPLETE",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = CinemaSlate50
                            )
                            Text(
                                text = exportState.outputFileName,
                                fontSize = 11.sp,
                                color = AnamorphicCyan,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldCinema,
                                    contentColor = CinemaSlate950
                                ),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Text("DONE & RETURN TO WORKSPACE", fontWeight = FontWeight.Black)
                            }
                        } else {
                            // In-progress render pass
                            Text(
                                text = "RENDERING CINEMA MASTER...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldCinema,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = exportState.currentPass,
                                fontSize = 11.sp,
                                color = CinemaSlate200,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            LinearProgressIndicator(
                                progress = { exportState.progressPercent / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp),
                                color = GoldCinema,
                                trackColor = CinemaSlate800,
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Frame: ${exportState.currentFrame} / ${exportState.totalFrames}",
                                    fontSize = 10.sp,
                                    color = CinemaSlate400,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "${exportState.progressPercent}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldCinema,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                } else {
                    // Export Configuration Form
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Codec Selection
                        Text("MASTER CODEC", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldCinema, fontFamily = FontFamily.Monospace)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Apple ProRes 422 HQ", "H.265 Cinema", "H.264 Master", "AV1 High").forEach { codec ->
                                FilterChip(
                                    selected = selectedCodec == codec,
                                    onClick = { selectedCodec = codec },
                                    label = { Text(codec, fontSize = 9.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GoldCinema,
                                        selectedLabelColor = CinemaSlate950
                                    )
                                )
                            }
                        }

                        // Resolution
                        Text("DELIVERY RESOLUTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AnamorphicCyan, fontFamily = FontFamily.Monospace)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("4K DCI (4096x2160)", "4K UHD (3840x2160)", "2K Scope", "1080p FHD").forEach { res ->
                                FilterChip(
                                    selected = selectedResolution == res,
                                    onClick = { selectedResolution = res },
                                    label = { Text(res, fontSize = 9.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AnamorphicCyan,
                                        selectedLabelColor = CinemaSlate950
                                    )
                                )
                            }
                        }

                        // Bitrate Control
                        Text("BITRATE & QUALITY TARGET", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CinemaSlate200, fontFamily = FontFamily.Monospace)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Cinema Master (150 Mbps)", "Festival Rec (80 Mbps)", "Web 4K (45 Mbps)").forEach { br ->
                                FilterChip(
                                    selected = selectedBitrate == br,
                                    onClick = { selectedBitrate = br },
                                    label = { Text(br, fontSize = 9.sp) }
                                )
                            }
                        }

                        // Burn-In Watermark Options
                        Surface(
                            color = CinemaSlate900,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (burnInWatermark) RecordRed else CinemaSlate700)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (burnInWatermark) "Burn-In Studio Watermark (Enabled)" else "Clean Master (No Watermark)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (burnInWatermark) RecordRed else CinemaSlate50
                                    )
                                    Text(
                                        text = if (burnInWatermark) "Permanently bakes watermark into film frames" else "Exports pristine broadcast clean plate",
                                        fontSize = 9.sp,
                                        color = CinemaSlate400
                                    )
                                }
                                Switch(
                                    checked = burnInWatermark,
                                    onCheckedChange = { burnInWatermark = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = RecordRed)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Render Button
                        Button(
                            onClick = {
                                onStartExport(selectedCodec, selectedResolution, selectedBitrate, burnInWatermark)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldCinema,
                                contentColor = CinemaSlate950
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_start_render")
                        ) {
                            Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "RENDER THEATRICAL CINEMA MASTER",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
