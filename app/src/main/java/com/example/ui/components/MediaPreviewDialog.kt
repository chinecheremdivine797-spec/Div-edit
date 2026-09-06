package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AssetType
import com.example.model.MediaAsset
import com.example.ui.theme.*

@Composable
fun MediaPreviewDialog(
    asset: MediaAsset?,
    onDismiss: () -> Unit,
    onAddToTimeline: (MediaAsset) -> Unit,
    onOpenRename: (MediaAsset) -> Unit,
    onDeleteAsset: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (asset == null) return

    var isPreviewPlaying by remember { mutableStateOf(true) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CinemaSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CinemaOutline),
            modifier = modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 620.dp)
                .padding(16.dp)
                .testTag("media_preview_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(asset.colorTag))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MEDIA INSPECTOR & PREVIEW",
                            color = CinemaGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CinemaMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Media Preview Canvas Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF070B11))
                        .border(1.dp, Color(asset.colorTag).copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Visual placeholder / waveform animation
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = when (asset.type) {
                                AssetType.LIVE_ACTION -> Icons.Default.Videocam
                                AssetType.ANIMATION, AssetType.RENDERED_SCENE -> Icons.Default.Animation
                                AssetType.IMAGE_SEQUENCE, AssetType.STILL_IMAGE -> Icons.Default.Image
                                AssetType.VFX_ASSET -> Icons.Default.AutoAwesome
                                AssetType.AUDIO_SCORE, AssetType.FOLEY_SFX, AssetType.VOICE_OVER -> Icons.Default.GraphicEq
                                AssetType.LOGO -> Icons.Default.Star
                            },
                            contentDescription = null,
                            tint = Color(asset.colorTag),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = asset.name,
                            color = CinemaWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${asset.resolution} • ${if (asset.fps > 0) "${asset.fps} FPS" else "Audio Stream"} • ${asset.folderCategory}",
                            color = CinemaMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Bottom Playback Scrubber simulation
                    Surface(
                        color = Color.Black.copy(alpha = 0.65f),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = { isPreviewPlaying = !isPreviewPlaying },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPreviewPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = CinemaCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "DURATION: ${asset.durationMs / 1000}s (${asset.durationMs}ms)",
                                color = CinemaGold,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = ".${asset.fileExtension.uppercase()}",
                                color = CinemaWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Technical Specs Grid
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = CinemaSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CinemaOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            SpecItem(label = "Format / Codec", value = ".${asset.fileExtension.uppercase()} Master")
                            SpecItem(label = "Resolution", value = asset.resolution)
                            SpecItem(label = "Frame Rate", value = if (asset.fps > 0) "${asset.fps} fps" else "N/A")
                        }
                        Divider(color = CinemaOutline.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            SpecItem(label = "File Size", value = "${asset.fileSizeBytes / (1024 * 1024)} MB")
                            SpecItem(label = "Folder / Category", value = asset.folderCategory)
                            SpecItem(label = "Color Profile", value = "DCI-P3 Wide Gamut")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (asset.description.isNotEmpty()) {
                    Text(
                        text = asset.description,
                        color = CinemaMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                if (showDeleteConfirm) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF3B1818)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD63031)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Remove '${asset.name}' from project?",
                                color = CinemaWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Note: The original file on device storage remains safe and will NOT be deleted.",
                                color = Color(0xFFFF7675),
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { showDeleteConfirm = false }) {
                                    Text("Cancel", color = CinemaMuted)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        onDeleteAsset(asset.id)
                                        onDismiss()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD63031))
                                ) {
                                    Text("Remove from Project")
                                }
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { onOpenRename(asset) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CinemaWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CinemaOutline),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Rename", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { showDeleteConfirm = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF7675)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD63031).copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.1f)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Remove", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                onAddToTimeline(asset)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CinemaGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("preview_add_to_timeline_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add to Timeline", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecItem(label: String, value: String) {
    Column {
        Text(text = label, color = CinemaMuted, fontSize = 9.sp)
        Text(
            text = value,
            color = CinemaWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )
    }
}
