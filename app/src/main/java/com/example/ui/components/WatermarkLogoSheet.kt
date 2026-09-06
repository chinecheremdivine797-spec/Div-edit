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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun WatermarkLogoSheet(
    watermarkConfig: WatermarkConfig,
    logoSequenceConfig: LogoSequenceConfig,
    onUpdateWatermark: (WatermarkConfig) -> Unit,
    onUpdateLogoSequence: (LogoSequenceConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var activeTab by remember { mutableStateOf(0) } // 0 = Watermark, 1 = Cinematic Logo Intro/Outro

    var currentWatermark by remember { mutableStateOf(watermarkConfig) }
    var currentLogoSeq by remember { mutableStateOf(logoSequenceConfig) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("watermark_logo_dialog"),
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
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = GoldCinema,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "WATERMARK & CINEMATIC LOGO SYSTEM",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = CinemaSlate50
                            )
                            Text(
                                text = "Non-Destructive & Burn-In Watermarking // Theatrical Logo Reveals",
                                fontSize = 10.sp,
                                color = GoldCinema
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = CinemaSlate400)
                    }
                }

                // Tab Switcher
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = CinemaSlate900,
                    contentColor = GoldCinema
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Watermark Studio", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Cinematic Logo Intro / Outro", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }

                // Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (activeTab == 0) {
                        WatermarkSettingsView(
                            config = currentWatermark,
                            onConfigChange = {
                                currentWatermark = it
                                onUpdateWatermark(it)
                            }
                        )
                    } else {
                        LogoSequenceSettingsView(
                            config = currentLogoSeq,
                            onConfigChange = {
                                currentLogoSeq = it
                                onUpdateLogoSequence(it)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WatermarkSettingsView(
    config: WatermarkConfig,
    onConfigChange: (WatermarkConfig) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Watermark Enable & Mode Selection
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CinemaSlate900, RoundedCornerShape(8.dp))
                .border(1.dp, CinemaSlate700, RoundedCornerShape(8.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Enable Film Watermark", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CinemaSlate50)
                Text("Display studio branding or copyright protection", fontSize = 10.sp, color = CinemaSlate400)
            }
            Switch(
                checked = config.isEnabled,
                onCheckedChange = { onConfigChange(config.copy(isEnabled = it)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = GoldCinema,
                    checkedTrackColor = CinemaSlate800
                )
            )
        }

        // NON-DESTRUCTIVE vs BURN-IN Toggle
        Surface(
            color = CinemaSlate900,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, if (config.isBurnIn) RecordRed else GoldCinema)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "WATERMARK RENDERING MODE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (config.isBurnIn) RecordRed else GoldCinema,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = { onConfigChange(config.copy(isBurnIn = false)) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (!config.isBurnIn) GoldCinema else CinemaSlate800,
                            contentColor = if (!config.isBurnIn) CinemaSlate950 else CinemaSlate200
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("NON-DESTRUCTIVE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    FilledTonalButton(
                        onClick = { onConfigChange(config.copy(isBurnIn = true)) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (config.isBurnIn) RecordRed else CinemaSlate800,
                            contentColor = if (config.isBurnIn) CinemaSlate50 else CinemaSlate200
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("BURN-IN MASTER", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (config.isBurnIn)
                        "Burn-In permanently bakes the watermark into the export pixels (safe for festival/client review)."
                        else "Non-destructive keeps watermark as an editable layer inside project tracks.",
                    fontSize = 10.sp,
                    color = CinemaSlate400
                )
            }
        }

        // Watermark Text / Brand
        OutlinedTextField(
            value = config.textContent,
            onValueChange = { onConfigChange(config.copy(textContent = it)) },
            label = { Text("Brand Name / Monogram / Text Content") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CinemaSlate900,
                unfocusedContainerColor = CinemaSlate900,
                focusedBorderColor = GoldCinema,
                unfocusedBorderColor = CinemaSlate700
            )
        )

        // 9-Box Anchor Position Grid
        Text("Anchor Position", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CinemaSlate200)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val positions = listOf(
                AnchorPosition.TOP_LEFT,
                AnchorPosition.TOP_CENTER,
                AnchorPosition.TOP_RIGHT,
                AnchorPosition.CENTER,
                AnchorPosition.BOTTOM_LEFT,
                AnchorPosition.BOTTOM_CENTER,
                AnchorPosition.BOTTOM_RIGHT
            )
            positions.chunked(4).forEach { rowList ->
                // row representation
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(AnchorPosition.TOP_LEFT, AnchorPosition.TOP_RIGHT, AnchorPosition.BOTTOM_LEFT, AnchorPosition.BOTTOM_RIGHT, AnchorPosition.CENTER).forEach { pos ->
                FilterChip(
                    selected = config.position == pos,
                    onClick = { onConfigChange(config.copy(position = pos)) },
                    label = { Text(pos.label, fontSize = 9.sp) }
                )
            }
        }

        // Scope Selection
        Text("Placement Scope", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CinemaSlate200)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            WatermarkScope.values().forEach { sc ->
                FilterChip(
                    selected = config.scope == sc,
                    onClick = { onConfigChange(config.copy(scope = sc)) },
                    label = { Text(sc.label, fontSize = 9.sp) }
                )
            }
        }

        // Sliders: Size, Opacity, Rotation
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Watermark Size: ${config.sizePercent.toInt()}%", fontSize = 11.sp, color = CinemaSlate200)
            }
            Slider(
                value = config.sizePercent,
                onValueChange = { onConfigChange(config.copy(sizePercent = it)) },
                valueRange = 5f..50f
            )
        }

        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Opacity: ${(config.opacity * 100).toInt()}%", fontSize = 11.sp, color = CinemaSlate200)
            }
            Slider(
                value = config.opacity,
                onValueChange = { onConfigChange(config.copy(opacity = it)) },
                valueRange = 0.1f..1.0f
            )
        }

        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Rotation: ${config.rotationDeg.toInt()}°", fontSize = 11.sp, color = CinemaSlate200)
            }
            Slider(
                value = config.rotationDeg,
                onValueChange = { onConfigChange(config.copy(rotationDeg = it)) },
                valueRange = -45f..45f
            )
        }
    }
}

@Composable
fun LogoSequenceSettingsView(
    config: LogoSequenceConfig,
    onConfigChange: (LogoSequenceConfig) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Intro Reveal Section
        Surface(
            color = CinemaSlate900,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, GoldCinema)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("CINEMATIC LOGO INTRO", fontSize = 12.sp, fontWeight = FontWeight.Black, color = GoldCinema)
                        Text("Dramatic opening sequence for studio ident", fontSize = 9.sp, color = CinemaSlate400)
                    }
                    Switch(
                        checked = config.isIntroEnabled,
                        onCheckedChange = { onConfigChange(config.copy(isIntroEnabled = it)) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = config.introTitle,
                    onValueChange = { onConfigChange(config.copy(introTitle = it)) },
                    label = { Text("Studio / Production Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = config.introSubtitle,
                    onValueChange = { onConfigChange(config.copy(introSubtitle = it)) },
                    label = { Text("Subtitle / Release Line") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Intro Reveal Style (8 Cinematic Variations):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CinemaSlate200)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    items(LogoRevealStyle.values()) { style ->
                        FilterChip(
                            selected = config.introStyle == style,
                            onClick = { onConfigChange(config.copy(introStyle = style)) },
                            label = { Text(style.label, fontSize = 9.sp) }
                        )
                    }
                }
            }
        }

        // Outro Section
        Surface(
            color = CinemaSlate900,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MagicViolet)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("CINEMATIC LOGO OUTRO", fontSize = 12.sp, fontWeight = FontWeight.Black, color = MagicViolet)
                        Text("Film-ending signature and credit closing", fontSize = 9.sp, color = CinemaSlate400)
                    }
                    Switch(
                        checked = config.isOutroEnabled,
                        onCheckedChange = { onConfigChange(config.copy(isOutroEnabled = it)) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = config.outroTitle,
                    onValueChange = { onConfigChange(config.copy(outroTitle = it)) },
                    label = { Text("Outro Card Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Outro Dissipation Style:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CinemaSlate200)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    items(LogoRevealStyle.values()) { style ->
                        FilterChip(
                            selected = config.outroStyle == style,
                            onClick = { onConfigChange(config.copy(outroStyle = style)) },
                            label = { Text(style.label, fontSize = 9.sp) }
                        )
                    }
                }
            }
        }
    }
}
