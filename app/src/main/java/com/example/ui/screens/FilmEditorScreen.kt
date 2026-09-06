package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
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
import com.example.model.*
import com.example.ui.components.CinematicPreviewMonitor
import com.example.ui.components.MultiTrackTimeline
import com.example.ui.theme.*
import com.example.viewmodel.ActiveStudioSheet
import com.example.viewmodel.FilmStudioViewModel

@Composable
fun FilmEditorScreen(
    viewModel: FilmStudioViewModel,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val project by viewModel.currentProject.collectAsState()
    val playheadMs by viewModel.currentPlayheadMs.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val timelineZoom by viewModel.timelineZoom.collectAsState()
    val isMagnetSnap by viewModel.isMagnetSnap.collectAsState()
    val showSafeGuides by viewModel.showSafeGuides.collectAsState()
    val showScope by viewModel.showAnamorphicScope.collectAsState()
    val selectedClipId by viewModel.selectedClipId.collectAsState()
    val watermarkConfig by viewModel.watermarkConfig.collectAsState()

    val selectedClip = viewModel.getSelectedClip()
    var inspectorTab by remember { mutableStateOf(0) } // 0 = Clip Properties & Transform, 1 = Quick Tools & FX

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaSlate950)
    ) {
        // TOP STUDIO WORKSPACE HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CinemaSlate900)
                .border(BorderStroke(0.5.dp, CinemaSlate700))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Back button & Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackToDashboard,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_back_to_dashboard")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Dashboard",
                        tint = CinemaSlate200,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Column {
                    Text(
                        text = project.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = CinemaSlate50,
                        maxLines = 1
                    )
                    Text(
                        text = "${project.aspectRatio.label} // ${project.fps.fps} FPS // ${project.colorSpace.label.take(8)}",
                        fontSize = 9.sp,
                        color = GoldCinema,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Center: Shortcuts to Magic & Director & Tricks
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Hollywood Magic quick launcher
                FilledTonalButton(
                    onClick = { viewModel.openSheet(ActiveStudioSheet.HOLLYWOOD_MAGIC) },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MagicVioletGlow,
                        contentColor = MagicViolet
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("editor_hollywood_magic_shortcut")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Magic", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // AI VFX Director launcher
                FilledTonalButton(
                    onClick = { viewModel.openSheet(ActiveStudioSheet.AI_VFX_DIRECTOR) },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = AnamorphicCyanGlow,
                        contentColor = AnamorphicCyan
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("editor_ai_director_shortcut")
                ) {
                    Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("AI Director", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // 20+ AI Cinematic Tools launcher
                FilledTonalButton(
                    onClick = { viewModel.openAiToolsStudio() },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = GoldCinemaGlow,
                        contentColor = GoldCinema
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("editor_ai_tools_shortcut")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("20 AI", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Film Tricks launcher
                FilledTonalButton(
                    onClick = { viewModel.openSheet(ActiveStudioSheet.FILM_TRICKS) },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = TrickOrange.copy(alpha = 0.2f),
                        contentColor = TrickOrange
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("editor_film_tricks_shortcut")
                ) {
                    Icon(Icons.Default.MovieFilter, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Tricks", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Right: Prominent Import Media & Export
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Clear and Prominent IMPORT MEDIA Button (Requirement 45)
                Button(
                    onClick = { viewModel.openSheet(ActiveStudioSheet.MEDIA_IMPORT_DIALOG) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AnamorphicCyan,
                        contentColor = CinemaSlate950
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("editor_import_media_prominent_button")
                ) {
                    Icon(Icons.Default.FileOpen, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("IMPORT MEDIA", fontSize = 10.sp, fontWeight = FontWeight.Black)
                }

                Button(
                    onClick = { viewModel.openSheet(ActiveStudioSheet.EXPORT_STUDIO) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldCinema,
                        contentColor = CinemaSlate950
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("editor_export_btn")
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export", fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        // CENTER WORKSPACE (Monitor + Inspector)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.1f)
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Center-Left: Large Cinematic Preview Monitor
            CinematicPreviewMonitor(
                project = project,
                currentPlayheadMs = playheadMs,
                isPlaying = isPlaying,
                selectedClip = selectedClip,
                watermarkConfig = watermarkConfig,
                showSafeGuides = showSafeGuides,
                showAnamorphicScope = showScope,
                timecodeText = viewModel.formatTimecode(playheadMs),
                onTogglePlay = { viewModel.togglePlayPause() },
                onStepFrame = { viewModel.stepFrame(it) },
                onSetInPoint = { viewModel.setInPoint() },
                onSetOutPoint = { viewModel.setOutPoint() },
                onToggleSafeGuides = { viewModel.toggleSafeGuides() },
                onToggleScope = { viewModel.toggleAnamorphicScope() },
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxHeight()
            )

            // Center-Right: Clip Inspector & Properties Panel
            Surface(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
                    .testTag("clip_inspector_panel"),
                shape = RoundedCornerShape(8.dp),
                color = CinemaSlate900,
                border = BorderStroke(1.dp, CinemaSlate700)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Inspector Tab Bar
                    TabRow(
                        selectedTabIndex = inspectorTab,
                        containerColor = CinemaSlate950,
                        contentColor = GoldCinema,
                        modifier = Modifier.height(36.dp)
                    ) {
                        Tab(
                            selected = inspectorTab == 0,
                            onClick = { inspectorTab = 0 },
                            text = { Text("Clip Properties", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = inspectorTab == 1,
                            onClick = { inspectorTab = 1 },
                            text = { Text("Tools & Looks", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        if (selectedClip != null) {
                            if (inspectorTab == 0) {
                                ClipPropertiesInspector(
                                    clip = selectedClip,
                                    onUpdateTransform = { viewModel.updateSelectedClipTransform(it) },
                                    onUpdateSpeed = { viewModel.updateSelectedClipSpeed(it) },
                                    onToggleReverse = { viewModel.toggleSelectedClipReverse() },
                                    onToggleFreeze = { viewModel.toggleSelectedClipFreezeFrame() },
                                    onUpdateOpacity = { viewModel.updateSelectedClipOpacity(it) },
                                    onUpdateVolume = { viewModel.updateSelectedClipVolume(it) },
                                    onOpenMagic = { viewModel.openSheet(ActiveStudioSheet.HOLLYWOOD_MAGIC) }
                                )
                            } else {
                                ToolsAndLooksInspector(
                                    clip = selectedClip,
                                    onReplaceClip = { viewModel.openReplaceMediaDialog(selectedClip) },
                                    onOpenColorStudio = { viewModel.openSheet(ActiveStudioSheet.COLOR_STUDIO) },
                                    onOpenAudioStudio = { viewModel.openSheet(ActiveStudioSheet.AUDIO_STUDIO) },
                                    onOpenWatermark = { viewModel.openSheet(ActiveStudioSheet.WATERMARK_LOGO) },
                                    onOpenFilmTricks = { viewModel.openSheet(ActiveStudioSheet.FILM_TRICKS) },
                                    onOpenAiDirector = { viewModel.openSheet(ActiveStudioSheet.AI_VFX_DIRECTOR) }
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.TouchApp, contentDescription = null, tint = CinemaSlate600, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No clip selected", fontSize = 11.sp, color = CinemaSlate400)
                                Text("Tap any track clip to inspect properties", fontSize = 9.sp, color = CinemaSlate600)
                            }
                        }
                    }
                }
            }
        }

        // BOTTOM WORKSPACE: PROFESSIONAL MULTI-TRACK TIMELINE
        MultiTrackTimeline(
            project = project,
            currentPlayheadMs = playheadMs,
            selectedClipId = selectedClipId,
            timelineZoom = timelineZoom,
            isMagnetSnap = isMagnetSnap,
            onSeekTo = { viewModel.seekTo(it) },
            onSelectClip = { clipId, trackId -> viewModel.selectClip(clipId, trackId) },
            onSplitAtPlayhead = { viewModel.splitClipAtPlayhead() },
            onDeleteSelectedClip = { viewModel.deleteSelectedClip() },
            onDuplicateSelectedClip = { viewModel.duplicateSelectedClip() },
            onToggleReverse = { viewModel.toggleSelectedClipReverse() },
            onToggleFreezeFrame = { viewModel.toggleSelectedClipFreezeFrame() },
            onChangeSpeed = { viewModel.updateSelectedClipSpeed(it) },
            onUndo = { viewModel.undo() },
            onRedo = { viewModel.redo() },
            onZoomChange = { viewModel.setTimelineZoom(it) },
            onToggleMagnetSnap = { viewModel.toggleMagnetSnap() },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f)
        )
    }
}

@Composable
fun ClipPropertiesInspector(
    clip: TimelineClip,
    onUpdateTransform: (TransformState) -> Unit,
    onUpdateSpeed: (Float) -> Unit,
    onToggleReverse: () -> Unit,
    onToggleFreeze: () -> Unit,
    onUpdateOpacity: (Float) -> Unit,
    onUpdateVolume: (Float) -> Unit,
    onOpenMagic: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Clip Title & Badges
        Text(
            text = clip.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = CinemaSlate50
        )

        // Applied Magic or Trick Pill
        if (clip.appliedMagicName != null) {
            Surface(
                color = MagicVioletGlow,
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, MagicViolet),
                onClick = onOpenMagic
            ) {
                Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MagicViolet, modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(clip.appliedMagicName, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MagicViolet)
                }
            }
        }

        // Speed Controller
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Speed: ${clip.speed}x", fontSize = 10.sp, color = CinemaSlate200, fontFamily = FontFamily.Monospace)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(0.5f, 1.0f, 2.0f, 4.0f).forEach { s ->
                    FilterChip(
                        selected = clip.speed == s,
                        onClick = { onUpdateSpeed(s) },
                        label = { Text("${s}x", fontSize = 8.sp) }
                    )
                }
            }
        }

        // Reverse & Freeze Frame toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = clip.isReversed,
                onClick = onToggleReverse,
                label = { Text(if (clip.isReversed) "Reversed" else "Normal", fontSize = 9.sp) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = clip.isFreezeFrame,
                onClick = onToggleFreeze,
                label = { Text(if (clip.isFreezeFrame) "Freeze On" else "Freeze Off", fontSize = 9.sp) },
                modifier = Modifier.weight(1f)
            )
        }

        // Opacity Slider
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Opacity", fontSize = 10.sp, color = CinemaSlate200)
                Text("${(clip.opacity * 100).toInt()}%", fontSize = 10.sp, color = GoldCinema, fontFamily = FontFamily.Monospace)
            }
            Slider(
                value = clip.opacity,
                onValueChange = onUpdateOpacity,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(thumbColor = GoldCinema, activeTrackColor = GoldCinema)
            )
        }

        // Scale & Transform
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Scale", fontSize = 10.sp, color = CinemaSlate200)
                Text("${(clip.transform.scale * 100).toInt()}%", fontSize = 10.sp, color = AnamorphicCyan, fontFamily = FontFamily.Monospace)
            }
            Slider(
                value = clip.transform.scale,
                onValueChange = { onUpdateTransform(clip.transform.copy(scale = it)) },
                valueRange = 0.5f..3.0f,
                colors = SliderDefaults.colors(thumbColor = AnamorphicCyan, activeTrackColor = AnamorphicCyan)
            )
        }

        // Rotation
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Rotation", fontSize = 10.sp, color = CinemaSlate200)
                Text("${clip.transform.rotationDeg.toInt()}°", fontSize = 10.sp, color = CinemaSlate200, fontFamily = FontFamily.Monospace)
            }
            Slider(
                value = clip.transform.rotationDeg,
                onValueChange = { onUpdateTransform(clip.transform.copy(rotationDeg = it)) },
                valueRange = -180f..180f
            )
        }

        // Volume (for audio clips)
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Volume Fader", fontSize = 10.sp, color = CinemaSlate200)
                Text("${(clip.volume * 100).toInt()}%", fontSize = 10.sp, color = AudioEmerald, fontFamily = FontFamily.Monospace)
            }
            Slider(
                value = clip.volume,
                onValueChange = onUpdateVolume,
                valueRange = 0f..2.0f,
                colors = SliderDefaults.colors(thumbColor = AudioEmerald, activeTrackColor = AudioEmerald)
            )
        }
    }
}

@Composable
fun ToolsAndLooksInspector(
    clip: TimelineClip,
    onReplaceClip: () -> Unit,
    onOpenColorStudio: () -> Unit,
    onOpenAudioStudio: () -> Unit,
    onOpenWatermark: () -> Unit,
    onOpenFilmTricks: () -> Unit,
    onOpenAiDirector: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("CINEMATIC SUITES FOR '${clip.title.take(16)}'", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldCinema, fontFamily = FontFamily.Monospace)

        // Replace Media Card (Requirement 45)
        Surface(
            onClick = onReplaceClip,
            shape = RoundedCornerShape(6.dp),
            color = CinemaSlate950,
            border = BorderStroke(1.dp, AnamorphicCyan.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("inspector_replace_media_button")
        ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = AnamorphicCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Replace Clip Media", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CinemaSlate50)
                    Text("Swap source footage while keeping cuts & effects", fontSize = 9.sp, color = AnamorphicCyan)
                }
            }
        }

        Surface(
            onClick = onOpenColorStudio,
            shape = RoundedCornerShape(6.dp),
            color = CinemaSlate950,
            border = BorderStroke(1.dp, GoldCinemaDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Palette, contentDescription = null, tint = GoldCinema, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Open Color Studio", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CinemaSlate50)
                    Text("3-Way Wheels & Hollywood LUTs", fontSize = 9.sp, color = CinemaSlate400)
                }
            }
        }

        Surface(
            onClick = onOpenFilmTricks,
            shape = RoundedCornerShape(6.dp),
            color = CinemaSlate950,
            border = BorderStroke(1.dp, TrickOrange.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MovieFilter, contentDescription = null, tint = TrickOrange, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Film Tricks Library", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CinemaSlate50)
                    Text("24 Reusable Cinematic Techniques", fontSize = 9.sp, color = CinemaSlate400)
                }
            }
        }

        Surface(
            onClick = onOpenAiDirector,
            shape = RoundedCornerShape(6.dp),
            color = CinemaSlate950,
            border = BorderStroke(1.dp, AnamorphicCyanDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = AnamorphicCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("AI VFX Director", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CinemaSlate50)
                    Text("Natural Language VFX Orchestration", fontSize = 9.sp, color = CinemaSlate400)
                }
            }
        }

        Surface(
            onClick = onOpenWatermark,
            shape = RoundedCornerShape(6.dp),
            color = CinemaSlate950,
            border = BorderStroke(1.dp, CinemaSlate700),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = GoldCinema, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Watermark & Logo Intro", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CinemaSlate50)
                    Text("Non-Destructive & Burn-In Branding", fontSize = 9.sp, color = CinemaSlate400)
                }
            }
        }

        Surface(
            onClick = onOpenAudioStudio,
            shape = RoundedCornerShape(6.dp),
            color = CinemaSlate950,
            border = BorderStroke(1.dp, AudioEmerald.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = AudioEmerald, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Audio Studio & Foley", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CinemaSlate50)
                    Text("Multitrack Faders & 96kHz Hits", fontSize = 9.sp, color = CinemaSlate400)
                }
            }
        }
    }
}
