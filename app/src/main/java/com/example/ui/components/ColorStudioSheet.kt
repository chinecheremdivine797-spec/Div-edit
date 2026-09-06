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
import com.example.model.ColorGrade
import com.example.model.FilmGrainType
import com.example.ui.theme.*

@Composable
fun ColorStudioSheet(
    currentColorGrade: ColorGrade,
    onUpdateColorGrade: (ColorGrade) -> Unit,
    onDismiss: () -> Unit
) {
    var grade by remember { mutableStateOf(currentColorGrade) }

    val luts = listOf(
        "Teal & Orange Hollywood",
        "Blade Runner 2049 Amber",
        "Film Noir 1940s Monochrome",
        "Matrix Cyber Green",
        "Kodak 5219 Vintage 35mm",
        "Bleach Bypass Cold Steel",
        "Technicolor 3-Strip Gold"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("color_studio_dialog"),
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
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = GoldCinema,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "COLOR STUDIO & CINEMATIC LOOKS",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = CinemaSlate50
                            )
                            Text(
                                text = "3-Way Primary Wheels // Hollywood LUTs // 35mm Emulsion",
                                fontSize = 10.sp,
                                color = GoldCinema
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = CinemaSlate400)
                    }
                }

                // Scrollable Color Controls
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Hollywood LUT Presets
                    Text("HOLLYWOOD FILM LUT PRESETS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldCinema, fontFamily = FontFamily.Monospace)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(luts) { lut ->
                            FilterChip(
                                selected = grade.lutPreset == lut,
                                onClick = {
                                    grade = grade.copy(lutPreset = lut)
                                    onUpdateColorGrade(grade)
                                },
                                label = { Text(lut, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldCinema,
                                    selectedLabelColor = CinemaSlate950
                                )
                            )
                        }
                    }

                    // 3-Way Primary Color Wheels simulation (Lift / Gamma / Gain)
                    Text("3-WAY PRIMARY COLOR BALANCES (LIFT / GAMMA / GAIN)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AnamorphicCyan, fontFamily = FontFamily.Monospace)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ColorWheelMini(title = "LIFT (Shadows)", colorTint = Color(0xFF1E3799))
                        ColorWheelMini(title = "GAMMA (Mids)", colorTint = Color(0xFFE58E26))
                        ColorWheelMini(title = "GAIN (Highlights)", colorTint = Color(0xFFF6B93B))
                    }

                    // Sliders for all image adjustments
                    Text("PRIMARY TONAL & SPECTRAL ADJUSTMENTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CinemaSlate200, fontFamily = FontFamily.Monospace)

                    GradeSlider(
                        label = "Temperature",
                        value = grade.temperature,
                        range = -100f..100f,
                        unit = "K",
                        onValueChange = { grade = grade.copy(temperature = it); onUpdateColorGrade(grade) }
                    )
                    GradeSlider(
                        label = "Tint (Green / Magenta)",
                        value = grade.tint,
                        range = -100f..100f,
                        unit = "",
                        onValueChange = { grade = grade.copy(tint = it); onUpdateColorGrade(grade) }
                    )
                    GradeSlider(
                        label = "Exposure",
                        value = grade.exposure,
                        range = -3f..3f,
                        unit = "EV",
                        onValueChange = { grade = grade.copy(exposure = it); onUpdateColorGrade(grade) }
                    )
                    GradeSlider(
                        label = "Contrast",
                        value = grade.contrast,
                        range = -100f..100f,
                        unit = "%",
                        onValueChange = { grade = grade.copy(contrast = it); onUpdateColorGrade(grade) }
                    )
                    GradeSlider(
                        label = "Highlights",
                        value = grade.highlights,
                        range = -100f..100f,
                        unit = "%",
                        onValueChange = { grade = grade.copy(highlights = it); onUpdateColorGrade(grade) }
                    )
                    GradeSlider(
                        label = "Shadows",
                        value = grade.shadows,
                        range = -100f..100f,
                        unit = "%",
                        onValueChange = { grade = grade.copy(shadows = it); onUpdateColorGrade(grade) }
                    )
                    GradeSlider(
                        label = "Saturation",
                        value = grade.saturation,
                        range = -100f..100f,
                        unit = "%",
                        onValueChange = { grade = grade.copy(saturation = it); onUpdateColorGrade(grade) }
                    )
                    GradeSlider(
                        label = "Vibrance",
                        value = grade.vibrance,
                        range = -100f..100f,
                        unit = "%",
                        onValueChange = { grade = grade.copy(vibrance = it); onUpdateColorGrade(grade) }
                    )
                    GradeSlider(
                        label = "Sharpen",
                        value = grade.sharpen,
                        range = 0f..100f,
                        unit = "%",
                        onValueChange = { grade = grade.copy(sharpen = it); onUpdateColorGrade(grade) }
                    )
                    GradeSlider(
                        label = "Vignette",
                        value = grade.vignette,
                        range = 0f..100f,
                        unit = "%",
                        onValueChange = { grade = grade.copy(vignette = it); onUpdateColorGrade(grade) }
                    )

                    // Film Grain Emulation
                    Text("35MM PHOTOCHEMICAL FILM GRAIN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldCinema, fontFamily = FontFamily.Monospace)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilmGrainType.values().forEach { grain ->
                            FilterChip(
                                selected = grade.filmGrain == grain,
                                onClick = {
                                    grade = grade.copy(filmGrain = grain)
                                    onUpdateColorGrade(grade)
                                },
                                label = { Text(grain.label, fontSize = 9.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldCinema,
                                    selectedLabelColor = CinemaSlate950
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorWheelMini(title: String, colorTint: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(CinemaSlate900, RoundedCornerShape(8.dp))
            .border(1.dp, CinemaSlate700, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CinemaSlate200)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(colorTint.copy(alpha = 0.3f), CircleShape)
                .border(1.5.dp, colorTint, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color.White, CircleShape)
            )
        }
    }
}

@Composable
fun GradeSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    unit: String,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 11.sp, color = CinemaSlate200)
            Text(
                text = "${if (value > 0 && unit != "%") "+" else ""}${String.format("%.1f", value)}$unit",
                fontSize = 11.sp,
                color = GoldCinema,
                fontFamily = FontFamily.Monospace
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = GoldCinema,
                activeTrackColor = GoldCinema,
                inactiveTrackColor = CinemaSlate800
            )
        )
    }
}
