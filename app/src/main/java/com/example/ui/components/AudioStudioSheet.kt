package com.example.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.model.MediaAsset
import com.example.model.TimelineTrack
import com.example.ui.theme.*

@Composable
fun AudioStudioSheet(
    tracks: List<TimelineTrack>,
    foleyAssets: List<MediaAsset>,
    onAddFoley: (MediaAsset) -> Unit,
    onDismiss: () -> Unit
) {
    var masterVol by remember { mutableStateOf(1.0f) }
    var dialogVol by remember { mutableStateOf(0.9f) }
    var scoreVol by remember { mutableStateOf(0.75f) }
    var sfxVol by remember { mutableStateOf(0.85f) }
    var dialogEnhancer by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("audio_studio_dialog"),
            shape = RoundedCornerShape(12.dp),
            color = CinemaSlate950,
            border = BorderStroke(1.5.dp, AudioEmerald)
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
                                .background(AudioEmerald.copy(alpha = 0.2f), CircleShape)
                                .border(1.dp, AudioEmerald, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = AudioEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AUDIO STUDIO & FOLEY CONSOLE",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = CinemaSlate50
                            )
                            Text(
                                text = "Multitrack Master Mixer // Dialog Enhancement // 96kHz Sound Cues",
                                fontSize = 10.sp,
                                color = AudioEmerald
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = CinemaSlate400)
                    }
                }

                // Main Multitrack Faders and Foley Cues
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left: Mixing Console Faders
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                            .background(CinemaSlate900, RoundedCornerShape(8.dp))
                            .border(1.dp, CinemaSlate700, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "MULTITRACK CONSOLE FADERS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AudioEmerald,
                            fontFamily = FontFamily.Monospace
                        )

                        // Channel Faders
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ChannelFaderRow("MASTER BUS", masterVol) { masterVol = it }
                            ChannelFaderRow("A1: DIALOG", dialogVol) { dialogVol = it }
                            ChannelFaderRow("A2: SCORE", scoreVol) { scoreVol = it }
                            ChannelFaderRow("A3: FOLEY & SFX", sfxVol) { sfxVol = it }
                        }

                        // Dialog Enhancer toggle
                        Surface(
                            color = CinemaSlate950,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, if (dialogEnhancer) AudioEmerald else CinemaSlate700)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Neural Dialog Enhancer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CinemaSlate50)
                                    Text("Dynamic noise suppression & clarity boost", fontSize = 9.sp, color = CinemaSlate400)
                                }
                                Switch(
                                    checked = dialogEnhancer,
                                    onCheckedChange = { dialogEnhancer = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = AudioEmerald)
                                )
                            }
                        }
                    }

                    // Right: Foley Sound Effects Browser
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(CinemaSlate900, RoundedCornerShape(8.dp))
                            .border(1.dp, CinemaSlate700, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "FOLEY & CINEMATIC SFX LIBRARY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldCinema,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Tap to audition & add directly to timeline SFX track",
                            fontSize = 9.sp,
                            color = CinemaSlate400,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(foleyAssets) { asset ->
                                Surface(
                                    color = CinemaSlate950,
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(0.5.dp, CinemaSlate700),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(asset.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CinemaSlate50)
                                            Text("${asset.durationMs / 1000f}s // 48kHz WAV", fontSize = 9.sp, color = CinemaSlate400)
                                        }
                                        Button(
                                            onClick = { onAddFoley(asset) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = AudioEmerald,
                                                contentColor = CinemaSlate950
                                            ),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("Cue", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelFaderRow(label: String, volume: Float, onVolumeChange: (Float) -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CinemaSlate200, fontFamily = FontFamily.Monospace)
            Text("${(volume * 100).toInt()}%", fontSize = 10.sp, color = AudioEmerald, fontFamily = FontFamily.Monospace)
        }
        Slider(
            value = volume,
            onValueChange = onVolumeChange,
            valueRange = 0f..1.5f,
            colors = SliderDefaults.colors(
                thumbColor = AudioEmerald,
                activeTrackColor = AudioEmerald,
                inactiveTrackColor = CinemaSlate800
            )
        )
    }
}
