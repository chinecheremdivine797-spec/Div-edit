package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
fun HollywoodMagicDialog(
    processState: HollywoodMagicProcessState,
    onSelectMagic: (HollywoodMagicType) -> Unit,
    onApplyToTimeline: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<HollywoodMagicCategory?>(null) }
    val allMagics = remember { HollywoodMagicType.values().toList() }
    val filteredMagics = remember(selectedCategory) {
        if (selectedCategory == null) allMagics else allMagics.filter { it.category == selectedCategory }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("hollywood_magic_dialog"),
            shape = RoundedCornerShape(12.dp),
            color = CinemaSlate950,
            border = BorderStroke(1.5.dp, MagicViolet)
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
                                .background(MagicVioletGlow, CircleShape)
                                .border(1.dp, MagicViolet, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MagicViolet,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "HOLLYWOOD MAGIC STUDIO",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = CinemaSlate50
                            )
                            Text(
                                text = "One-Click Neural Compositing & Cinematic VFX Architecture",
                                fontSize = 11.sp,
                                color = MagicViolet
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_magic_dialog")) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CinemaSlate400
                        )
                    }
                }

                // 1-Click Workflow Breadcrumbs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CinemaSlate900.copy(alpha = 0.5f))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val steps = listOf("SELECT CLIP", "SELECT MAGIC", "AI PROCESS", "PREVIEW", "APPLY")
                    steps.forEachIndexed { index, step ->
                        val isActive = (index <= (if (processState.isComplete) 4 else if (processState.isProcessing) 2 else 1))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = step,
                                fontSize = 9.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                color = if (isActive) GoldCinema else CinemaSlate400,
                                fontFamily = FontFamily.Monospace
                            )
                            if (index < steps.size - 1) {
                                Text(
                                    text = " → ",
                                    fontSize = 10.sp,
                                    color = CinemaSlate600
                                )
                            }
                        }
                    }
                }

                // Category filter tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All Magic (${allMagics.size})", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MagicViolet,
                            selectedLabelColor = CinemaSlate50
                        )
                    )
                    HollywoodMagicCategory.values().forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.title, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MagicViolet,
                                selectedLabelColor = CinemaSlate50
                            )
                        )
                    }
                }

                // Main Content Body (Left: Grid of 26 Magic Presets; Right: 9-Step Deep Compositing Inspector)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Grid of 26 Magic Effects
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredMagics) { magic ->
                            val isSelected = magic == processState.magicType
                            MagicEffectCard(
                                magic = magic,
                                isSelected = isSelected,
                                onClick = { onSelectMagic(magic) }
                            )
                        }
                    }

                    // Right 9-Step Deep Compositing Pipeline Visualizer
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(CinemaSlate900, RoundedCornerShape(8.dp))
                            .border(1.dp, CinemaSlate700, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DEEP COMPOSITING PIPELINE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldCinema,
                                fontFamily = FontFamily.Monospace
                            )
                            if (processState.isProcessing) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(12.dp),
                                        strokeWidth = 2.dp,
                                        color = MagicViolet
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "GPU Running",
                                        fontSize = 9.sp,
                                        color = MagicViolet,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Target Clip: ${processState.selectedClipTitle}",
                            fontSize = 10.sp,
                            color = CinemaSlate200,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        HorizontalDivider(color = CinemaSlate700, modifier = Modifier.padding(vertical = 6.dp))

                        // 9-Step List
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            processState.steps.forEach { step ->
                                val isCurrent = step.status.contains("Processing")
                                val isDone = step.status == "Complete"

                                Surface(
                                    color = if (isCurrent) MagicVioletGlow else if (isDone) CinemaSlate800 else CinemaSlate950,
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isCurrent) MagicViolet else if (isDone) AnamorphicCyanDark else CinemaSlate700
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(
                                                    if (isDone) AudioEmerald else if (isCurrent) MagicViolet else CinemaSlate700,
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isDone) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = CinemaSlate950,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            } else {
                                                Text(
                                                    text = "${step.stepNumber}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CinemaSlate50
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${step.stepNumber}. ${step.title}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDone || isCurrent) CinemaSlate50 else CinemaSlate400
                                            )
                                            Text(
                                                text = step.description,
                                                fontSize = 9.sp,
                                                color = CinemaSlate400
                                            )
                                        }

                                        Text(
                                            text = step.status,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDone) AudioEmerald else if (isCurrent) MagicViolet else CinemaSlate400,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Controls Summary (Particle Type, Blend Mode)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Particle: ${processState.magicType.particleType}",
                                fontSize = 9.sp,
                                color = CinemaSlate400
                            )
                            Text(
                                text = "Alpha Blend: Screen / Add",
                                fontSize = 9.sp,
                                color = AnamorphicCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Apply Button
                        Button(
                            onClick = onApplyToTimeline,
                            enabled = !processState.isProcessing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MagicViolet,
                                contentColor = CinemaSlate950
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("btn_apply_magic")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "APPLY MAGIC TO TIMELINE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MagicEffectCard(
    magic: HollywoodMagicType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) MagicVioletGlow else CinemaSlate900,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) MagicViolet else CinemaSlate700
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("magic_card_${magic.name}")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(if (isSelected) MagicViolet else CinemaSlate800, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (magic) {
                            HollywoodMagicType.DISAPPEAR -> Icons.Default.VisibilityOff
                            HollywoodMagicType.TELEPORT -> Icons.Default.Bolt
                            HollywoodMagicType.CLONE -> Icons.Default.ContentCopy
                            HollywoodMagicType.LIGHTNING -> Icons.Default.FlashOn
                            HollywoodMagicType.FIRE, HollywoodMagicType.EXPLOSION -> Icons.Default.Whatshot
                            HollywoodMagicType.SUPERHERO_LANDING -> Icons.Default.PanToolAlt
                            HollywoodMagicType.TIME_FREEZE -> Icons.Default.PauseCircle
                            HollywoodMagicType.SUPER_SPEED -> Icons.Default.Speed
                            HollywoodMagicType.PORTAL -> Icons.Default.MotionPhotosOn
                            HollywoodMagicType.INVISIBILITY -> Icons.Default.LensBlur
                            else -> Icons.Default.AutoAwesome
                        },
                        contentDescription = null,
                        tint = if (isSelected) CinemaSlate950 else GoldCinema,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(CinemaSlate950, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = magic.category.title.take(8).uppercase(),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = CinemaSlate400
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = magic.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = CinemaSlate50
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = magic.description,
                fontSize = 10.sp,
                color = CinemaSlate400,
                maxLines = 2
            )
        }
    }
}
