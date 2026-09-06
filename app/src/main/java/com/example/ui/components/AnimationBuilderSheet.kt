package com.example.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.ui.theme.*

@Composable
fun AnimationBuilderSheet(
    onDismiss: () -> Unit
) {
    var cellRate by remember { mutableStateOf("12 FPS (On-Twos)") }
    var onionSkinning by remember { mutableStateOf(true) }
    var handDrawnGrain by remember { mutableStateOf(true) }
    var animePostGlow by remember { mutableStateOf(true) }
    var currentFrameIndex by remember { mutableStateOf(14) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("animation_builder_dialog"),
            shape = RoundedCornerShape(12.dp),
            color = CinemaSlate950,
            border = BorderStroke(1.5.dp, AnimationAmber)
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
                                .background(AnimationAmber.copy(alpha = 0.2f), CircleShape)
                                .border(1.dp, AnimationAmber, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Brush,
                                contentDescription = null,
                                tint = AnimationAmber,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ANIMATION POST-PRODUCTION STUDIO",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = CinemaSlate50
                            )
                            Text(
                                text = "Anime & 2D/3D Cell Timing // Onion Skinning // Mixed Media Plates",
                                fontSize = 10.sp,
                                color = AnimationAmber
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = CinemaSlate400)
                    }
                }

                // Controls
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Cell Timing & Rate
                    Surface(
                        color = CinemaSlate900,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, CinemaSlate700)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("ANIMATION CELL TIMING", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AnimationAmber, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("12 FPS (On-Twos)", "24 FPS (On-Ones)", "8 FPS (Stylized)", "60 FPS (3D CGI)").forEach { rate ->
                                    FilterChip(
                                        selected = cellRate == rate,
                                        onClick = { cellRate = rate },
                                        label = { Text(rate, fontSize = 9.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = AnimationAmber,
                                            selectedLabelColor = CinemaSlate950
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Onion Skinning Controller
                    Surface(
                        color = CinemaSlate900,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, CinemaSlate700)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Onion Skinning Scrub Preview", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CinemaSlate50)
                                    Text("Renders 2 past frames (cyan) and 2 future frames (magenta)", fontSize = 9.sp, color = CinemaSlate400)
                                }
                                Switch(
                                    checked = onionSkinning,
                                    onCheckedChange = { onionSkinning = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = AnimationAmber)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Scrub Cell Frame: Frame #$currentFrameIndex", fontSize = 10.sp, color = CinemaSlate200, fontFamily = FontFamily.Monospace)
                            Slider(
                                value = currentFrameIndex.toFloat(),
                                onValueChange = { currentFrameIndex = it.toInt() },
                                valueRange = 1f..48f,
                                colors = SliderDefaults.colors(thumbColor = AnimationAmber, activeTrackColor = AnimationAmber)
                            )
                        }
                    }

                    // Animation Post Looks
                    Surface(
                        color = CinemaSlate900,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, CinemaSlate700)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("ANIMATION STYLING OVERLAYS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AnimationAmber, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Hand-Drawn Cel Texture & Paper Grain", fontSize = 11.sp, color = CinemaSlate50)
                                Switch(checked = handDrawnGrain, onCheckedChange = { handDrawnGrain = it })
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Anime Diffuse Bloom & Halation Glow", fontSize = 11.sp, color = CinemaSlate50)
                                Switch(checked = animePostGlow, onCheckedChange = { animePostGlow = it })
                            }
                        }
                    }

                    // Mixed Media Live Action + Animation Compositing
                    Surface(
                        color = CinemaSlate900,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, CinemaSlate700)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("MIXED MEDIA PLATE COMPOSITING", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AnamorphicCyan, fontFamily = FontFamily.Monospace)
                            Text("Composite hand-drawn 2D animation or 3D render passes onto 4K live action camera plates.", fontSize = 10.sp, color = CinemaSlate400, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
        }
    }
}
