package com.example.ui.components

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
import com.example.model.AspectRatioPreset
import com.example.model.ColorSpaceOption
import com.example.model.FpsOption
import com.example.ui.theme.*

@Composable
fun NewFilmDialog(
    onCreateFilm: (title: String, director: String, aspect: AspectRatioPreset, fps: FpsOption, colorSpace: ColorSpaceOption) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var director by remember { mutableStateOf("") }
    var selectedAspect by remember { mutableStateOf(AspectRatioPreset.ANAMORPHIC_2_39) }
    var selectedFps by remember { mutableStateOf(FpsOption.FPS_24) }
    var selectedColorSpace by remember { mutableStateOf(ColorSpaceOption.DCI_P3) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .testTag("new_film_dialog"),
            shape = RoundedCornerShape(12.dp),
            color = CinemaSlate950,
            border = BorderStroke(1.5.dp, GoldCinema)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(GoldCinemaGlow, CircleShape)
                                .border(1.dp, GoldCinema, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MovieCreation, contentDescription = null, tint = GoldCinema, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("CREATE NEW CINEMA MASTER", fontSize = 15.sp, fontWeight = FontWeight.Black, color = CinemaSlate50)
                            Text("Configure film canvas, theatrical aspect, and grading pipeline", fontSize = 10.sp, color = GoldCinema)
                        }
                    }

                    // Film Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Film Project Title") },
                        placeholder = { Text("e.g. THE CHRONOSPHERE PROTOCOL") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_new_film_title"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CinemaSlate900,
                            unfocusedContainerColor = CinemaSlate900,
                            focusedBorderColor = GoldCinema,
                            unfocusedBorderColor = CinemaSlate700
                        )
                    )

                    // Director Name
                    OutlinedTextField(
                        value = director,
                        onValueChange = { director = it },
                        label = { Text("Director / Studio") },
                        placeholder = { Text("e.g. Christopher M.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_new_film_director"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CinemaSlate900,
                            unfocusedContainerColor = CinemaSlate900,
                            focusedBorderColor = GoldCinema,
                            unfocusedBorderColor = CinemaSlate700
                        )
                    )

                    // Aspect Ratio Preset
                    Text("CINEMATIC ASPECT RATIO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldCinema, fontFamily = FontFamily.Monospace)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AspectRatioPreset.values().forEach { aspect ->
                            FilterChip(
                                selected = selectedAspect == aspect,
                                onClick = { selectedAspect = aspect },
                                label = { Text(aspect.label, fontSize = 9.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldCinema,
                                    selectedLabelColor = CinemaSlate950
                                )
                            )
                        }
                    }

                    // Frame Rate
                    Text("PROJECT TIMEBASE (FPS)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AnamorphicCyan, fontFamily = FontFamily.Monospace)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FpsOption.values().forEach { fps ->
                            FilterChip(
                                selected = selectedFps == fps,
                                onClick = { selectedFps = fps },
                                label = { Text(fps.label, fontSize = 9.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AnamorphicCyan,
                                    selectedLabelColor = CinemaSlate950
                                )
                            )
                        }
                    }

                    // Color Space
                    Text("COLOR MANAGEMENT ARCHITECTURE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CinemaSlate200, fontFamily = FontFamily.Monospace)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ColorSpaceOption.values().forEach { cs ->
                            FilterChip(
                                selected = selectedColorSpace == cs,
                                onClick = { selectedColorSpace = cs },
                                label = { Text(cs.label, fontSize = 9.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MagicViolet,
                                    selectedLabelColor = CinemaSlate950
                                )
                            )
                        }
                    }
                }

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = CinemaSlate400)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onCreateFilm(title, director, selectedAspect, selectedFps, selectedColorSpace)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldCinema,
                            contentColor = CinemaSlate950
                        ),
                        modifier = Modifier.testTag("btn_confirm_new_film")
                    ) {
                        Text("INITIALIZE FILM WORKSPACE", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
