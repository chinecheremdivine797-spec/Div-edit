package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AspectRatioPreset
import com.example.model.FilmProject
import com.example.model.TimelineClip
import com.example.model.WatermarkConfig
import com.example.ui.theme.*
import kotlin.random.Random

@Composable
fun CinematicPreviewMonitor(
    project: FilmProject,
    currentPlayheadMs: Long,
    isPlaying: Boolean,
    selectedClip: TimelineClip?,
    watermarkConfig: WatermarkConfig,
    showSafeGuides: Boolean,
    showAnamorphicScope: Boolean,
    timecodeText: String,
    onTogglePlay: () -> Unit,
    onStepFrame: (Boolean) -> Unit,
    onSetInPoint: () -> Unit,
    onSetOutPoint: () -> Unit,
    onToggleSafeGuides: () -> Unit,
    onToggleScope: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse animation for VFX elements
    val infiniteTransition = rememberInfiniteTransition(label = "vfx_anim")
    val vfxPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vfx_pulse"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CinemaSlate900, RoundedCornerShape(8.dp))
            .border(1.dp, CinemaSlate700, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        // Monitor Top Info Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(if (isPlaying) RecordRed else GoldCinema, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isPlaying) "PLAYBACK [LIVE]" else "PAUSED [REC: ${project.colorSpace.label.take(8)}]",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPlaying) RecordRed else GoldCinema,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${project.fps.fps} FPS // ${project.aspectRatio.label}",
                    fontSize = 10.sp,
                    color = CinemaSlate400,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Big SMPTE Timecode
            Surface(
                color = CinemaSlate950,
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CinemaSlate700)
            ) {
                Text(
                    text = timecodeText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AnamorphicCyan,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .testTag("preview_timecode")
                )
            }
        }

        // Viewport Box with Cinematic Letterbox and VFX Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black)
                .testTag("cinematic_viewport"),
            contentAlignment = Alignment.Center
        ) {
            // Scene Render Simulation Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw cinematic background gradient / footage simulation
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            Color(0xFF1E293B),
                            Color(0xFF0F172A)
                        )
                    )
                )

                // Simulated scene content (cinematic cityscape / hero silhouette)
                drawRect(
                    color = Color(0xFF0B0F19),
                    topLeft = Offset(0f, canvasHeight * 0.55f),
                    size = Size(canvasWidth, canvasHeight * 0.45f)
                )

                // Neon building silhouettes
                drawRect(
                    color = Color(0xFF151D2E),
                    topLeft = Offset(canvasWidth * 0.15f, canvasHeight * 0.35f),
                    size = Size(canvasWidth * 0.18f, canvasHeight * 0.4f)
                )
                drawRect(
                    color = Color(0xFF111827),
                    topLeft = Offset(canvasWidth * 0.65f, canvasHeight * 0.28f),
                    size = Size(canvasWidth * 0.22f, canvasHeight * 0.5f)
                )

                // Neon glow lines
                drawLine(
                    color = AnamorphicCyan.copy(alpha = 0.8f),
                    start = Offset(canvasWidth * 0.16f, canvasHeight * 0.38f),
                    end = Offset(canvasWidth * 0.32f, canvasHeight * 0.38f),
                    strokeWidth = 2.5f
                )
                drawLine(
                    color = MagicViolet.copy(alpha = 0.8f),
                    start = Offset(canvasWidth * 0.67f, canvasHeight * 0.32f),
                    end = Offset(canvasWidth * 0.85f, canvasHeight * 0.32f),
                    strokeWidth = 2.5f
                )

                // Active VFX rendering overlay based on selected clip effects
                val hasMagic = selectedClip?.appliedMagicId != null || selectedClip?.appliedMagicName != null
                val hasTrick = selectedClip?.appliedFilmTrickId != null || selectedClip?.appliedFilmTrickName != null

                if (hasMagic) {
                    // Draw magical aura / portal / lightning arcs
                    val centerX = canvasWidth * 0.5f
                    val centerY = canvasHeight * 0.5f
                    val radius = (45f + vfxPhase * 25f).dp.toPx()

                    // Glowing portal ring
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MagicViolet.copy(alpha = 0.7f),
                                AnamorphicCyan.copy(alpha = 0.4f),
                                Color.Transparent
                            ),
                            center = Offset(centerX, centerY),
                            radius = radius * 1.5f
                        ),
                        radius = radius * 1.5f,
                        center = Offset(centerX, centerY)
                    )

                    drawCircle(
                        color = GoldCinema,
                        radius = radius,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Lightning arcs
                    for (i in 0..5) {
                        val angle = (i * 60 + vfxPhase * 40).toDouble()
                        val rad = Math.toRadians(angle)
                        val startX = centerX + (radius * Math.cos(rad)).toFloat()
                        val startY = centerY + (radius * Math.sin(rad)).toFloat()
                        val endX = startX + (Random.nextFloat() * 40f - 20f)
                        val endY = startY + (Random.nextFloat() * 40f - 20f)
                        drawLine(
                            color = AnamorphicCyan,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 2f
                        )
                    }
                }

                if (hasTrick) {
                    // Vignette & Light leak
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x66FFB834),
                                Color(0xAA000000)
                            ),
                            center = Offset(canvasWidth * 0.5f, canvasHeight * 0.5f),
                            radius = canvasWidth * 0.65f
                        )
                    )
                }

                // Anamorphic 2.39:1 Letterbox Scope Bars
                if (showAnamorphicScope || project.aspectRatio == AspectRatioPreset.ANAMORPHIC_2_39) {
                    val scopeBarHeight = canvasHeight * 0.14f
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(0f, 0f),
                        size = Size(canvasWidth, scopeBarHeight)
                    )
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(0f, canvasHeight - scopeBarHeight),
                        size = Size(canvasWidth, scopeBarHeight)
                    )
                }

                // Safe Title & Action Area Guides
                if (showSafeGuides) {
                    // 90% Action Safe
                    val aMarginX = canvasWidth * 0.05f
                    val aMarginY = canvasHeight * 0.05f
                    drawRect(
                        color = CinemaSlate400.copy(alpha = 0.5f),
                        topLeft = Offset(aMarginX, aMarginY),
                        size = Size(canvasWidth - aMarginX * 2, canvasHeight - aMarginY * 2),
                        style = Stroke(width = 1f)
                    )
                    // 80% Title Safe
                    val tMarginX = canvasWidth * 0.10f
                    val tMarginY = canvasHeight * 0.10f
                    drawRect(
                        color = AnamorphicCyan.copy(alpha = 0.6f),
                        topLeft = Offset(tMarginX, tMarginY),
                        size = Size(canvasWidth - tMarginX * 2, canvasHeight - tMarginY * 2),
                        style = Stroke(width = 1f)
                    )
                    // Center crosshair
                    drawLine(
                        color = GoldCinema.copy(alpha = 0.7f),
                        start = Offset(canvasWidth * 0.5f - 12f, canvasHeight * 0.5f),
                        end = Offset(canvasWidth * 0.5f + 12f, canvasHeight * 0.5f),
                        strokeWidth = 1.5f
                    )
                    drawLine(
                        color = GoldCinema.copy(alpha = 0.7f),
                        start = Offset(canvasWidth * 0.5f, canvasHeight * 0.5f - 12f),
                        end = Offset(canvasWidth * 0.5f, canvasHeight * 0.5f + 12f),
                        strokeWidth = 1.5f
                    )
                }
            }

            // Watermark Overlay Rendering
            if (watermarkConfig.isEnabled) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = when (watermarkConfig.position) {
                        com.example.model.AnchorPosition.TOP_LEFT -> Alignment.TopStart
                        com.example.model.AnchorPosition.TOP_CENTER -> Alignment.TopCenter
                        com.example.model.AnchorPosition.TOP_RIGHT -> Alignment.TopEnd
                        com.example.model.AnchorPosition.CENTER -> Alignment.Center
                        com.example.model.AnchorPosition.BOTTOM_LEFT -> Alignment.BottomStart
                        com.example.model.AnchorPosition.BOTTOM_CENTER -> Alignment.BottomCenter
                        com.example.model.AnchorPosition.BOTTOM_RIGHT,
                        com.example.model.AnchorPosition.CUSTOM -> Alignment.BottomEnd
                    }
                ) {
                    Surface(
                        color = if (watermarkConfig.isBurnIn) Color.Black.copy(alpha = 0.85f * watermarkConfig.opacity)
                                else CinemaSlate800.copy(alpha = 0.6f * watermarkConfig.opacity),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (watermarkConfig.isBurnIn) RecordRed.copy(alpha = 0.8f) else GoldCinema.copy(alpha = 0.6f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MovieFilter,
                                contentDescription = null,
                                tint = GoldCinema,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = watermarkConfig.textContent,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CinemaSlate50.copy(alpha = watermarkConfig.opacity),
                                fontFamily = FontFamily.Monospace
                            )
                            if (watermarkConfig.isBurnIn) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "[BURN-IN]",
                                    fontSize = 7.sp,
                                    color = RecordRed,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }

            // Active Effect HUD Badge (Upper Left of viewport)
            if (selectedClip?.appliedMagicName != null || selectedClip?.appliedFilmTrickName != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Surface(
                        color = CinemaSlate950.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MagicViolet)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MagicViolet,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = selectedClip.appliedMagicName ?: selectedClip.appliedFilmTrickName ?: "VFX Active",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MagicViolet
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Transport & Monitor Controls Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Guide / Scope Toggles
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleSafeGuides,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_safe_guides")
                ) {
                    Icon(
                        imageVector = Icons.Default.Grid4x4,
                        contentDescription = "Safe Title Guides",
                        tint = if (showSafeGuides) GoldCinema else CinemaSlate400,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onToggleScope,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_scope_bars")
                ) {
                    Icon(
                        imageVector = Icons.Default.AspectRatio,
                        contentDescription = "Anamorphic Scope Letterbox",
                        tint = if (showAnamorphicScope) AnamorphicCyan else CinemaSlate400,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onSetInPoint,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_mark_in")
                ) {
                    Icon(
                        imageVector = Icons.Default.VerticalAlignTop,
                        contentDescription = "Mark In Point",
                        tint = CinemaSlate200,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onSetOutPoint,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_mark_out")
                ) {
                    Icon(
                        imageVector = Icons.Default.VerticalAlignBottom,
                        contentDescription = "Mark Out Point",
                        tint = CinemaSlate200,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Central Playback Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { onStepFrame(false) },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_step_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Step 1 Frame Back",
                        tint = CinemaSlate200,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Play / Pause Master Button
                FilledIconButton(
                    onClick = onTogglePlay,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isPlaying) RecordRed else GoldCinema,
                        contentColor = CinemaSlate950
                    ),
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("btn_play_pause")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause Playback" else "Play Timeline",
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { onStepFrame(true) },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_step_forward")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Step 1 Frame Forward",
                        tint = CinemaSlate200,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Right Status (Duration)
            Text(
                text = "TOTAL: ${String.format("%02d:%02d", (project.durationMs / 60000), ((project.durationMs % 60000) / 1000))}",
                fontSize = 10.sp,
                color = CinemaSlate400,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(end = 4.dp)
            )
        }
    }
}
