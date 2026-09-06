package com.example.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun MultiTrackTimeline(
    project: FilmProject,
    currentPlayheadMs: Long,
    selectedClipId: String?,
    timelineZoom: Float,
    isMagnetSnap: Boolean,
    onSeekTo: (Long) -> Unit,
    onSelectClip: (String, String) -> Unit,
    onSplitAtPlayhead: () -> Unit,
    onDeleteSelectedClip: () -> Unit,
    onDuplicateSelectedClip: () -> Unit,
    onToggleReverse: () -> Unit,
    onToggleFreezeFrame: () -> Unit,
    onChangeSpeed: (Float) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onZoomChange: (Float) -> Unit,
    onToggleMagnetSnap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalDurationMs = project.durationMs.coerceAtLeast(60_000L)
    // Scale: Pixels per second based on zoom. At 1.0 zoom, 1 second = 30dp
    val pxPerSecond = (24f * timelineZoom).coerceIn(12f, 96f)
    val totalTimelineWidthDp = (totalDurationMs / 1000f * pxPerSecond).dp

    val horizontalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CinemaSlate950)
            .border(1.dp, CinemaSlate700)
    ) {
        // TOP TIMELINE TOOLBAR (Editing Tools)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CinemaSlate900)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Edit actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(
                    onClick = onUndo,
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("btn_undo")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo Action",
                        tint = CinemaSlate200,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onRedo,
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("btn_redo")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Redo,
                        contentDescription = "Redo Action",
                        tint = CinemaSlate200,
                        modifier = Modifier.size(16.dp)
                    )
                }

                VerticalDivider(
                    modifier = Modifier
                        .height(18.dp)
                        .padding(horizontal = 4.dp),
                    color = CinemaSlate700
                )

                // Split at playhead
                FilledTonalButton(
                    onClick = onSplitAtPlayhead,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = CinemaSlate800,
                        contentColor = RecordRed
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("btn_split")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Split", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Duplicate
                IconButton(
                    onClick = onDuplicateSelectedClip,
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("btn_duplicate")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Duplicate Clip",
                        tint = CinemaSlate200,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Delete
                IconButton(
                    onClick = onDeleteSelectedClip,
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("btn_delete")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Clip",
                        tint = RecordRed,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Reverse
                IconButton(
                    onClick = onToggleReverse,
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("btn_reverse")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Reverse Clip",
                        tint = AnamorphicCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Freeze frame
                IconButton(
                    onClick = onToggleFreezeFrame,
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("btn_freeze")
                ) {
                    Icon(
                        imageVector = Icons.Default.AcUnit,
                        contentDescription = "Freeze Frame",
                        tint = AnamorphicCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Right side: Snap & Zoom
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Magnet snap
                IconButton(
                    onClick = onToggleMagnetSnap,
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("btn_magnet")
                ) {
                    Icon(
                        imageVector = Icons.Default.Animation,
                        contentDescription = "Magnet Snap",
                        tint = if (isMagnetSnap) GoldCinema else CinemaSlate400,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Zoom Out
                IconButton(
                    onClick = { onZoomChange(timelineZoom - 0.25f) },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomOut,
                        contentDescription = "Zoom Out",
                        tint = CinemaSlate400,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Text(
                    text = "${(timelineZoom * 100).toInt()}%",
                    fontSize = 10.sp,
                    color = CinemaSlate200,
                    fontFamily = FontFamily.Monospace
                )

                // Zoom In
                IconButton(
                    onClick = { onZoomChange(timelineZoom + 0.25f) },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = "Zoom In",
                        tint = CinemaSlate400,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // MAIN TIMELINE SCROLLABLE AREA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Fixed Left Track Headers (V1, V2, FX, ADJ, TXT, A1, A2, A3)
                Column(
                    modifier = Modifier
                        .width(90.dp)
                        .fillMaxHeight()
                        .background(CinemaSlate900)
                        .border(androidx.compose.foundation.BorderStroke(1.dp, CinemaSlate700))
                ) {
                    // Header space matching ruler
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .background(CinemaSlate950)
                            .border(androidx.compose.foundation.BorderStroke(1.dp, CinemaSlate700)),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "TRACKS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = CinemaSlate400,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }

                    // Track headers
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(project.tracks) { track ->
                            TrackHeaderItem(track = track)
                        }
                    }
                }

                // Scrollable Tracks and Time Ruler
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .horizontalScroll(horizontalScrollState)
                ) {
                    Column(modifier = Modifier.width(totalTimelineWidthDp)) {
                        // Time Ruler Bar
                        TimeRuler(
                            totalDurationMs = totalDurationMs,
                            pxPerSecond = pxPerSecond,
                            markers = project.markers,
                            onSeek = onSeekTo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                        )

                        // Tracks Clip Lanes
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(project.tracks) { track ->
                                TrackClipsLane(
                                    track = track,
                                    pxPerSecond = pxPerSecond,
                                    selectedClipId = selectedClipId,
                                    onSelectClip = { clipId -> onSelectClip(clipId, track.id) },
                                    onSeek = onSeekTo
                                )
                            }
                        }
                    }

                    // Red Playhead Line across entire timeline
                    val playheadOffsetDp = (currentPlayheadMs / 1000f * pxPerSecond).dp
                    Box(
                        modifier = Modifier
                            .offset(x = playheadOffsetDp - 6.dp)
                            .width(12.dp)
                            .fillMaxHeight()
                            .pointerInput(totalDurationMs, pxPerSecond) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val deltaMs = (dragAmount.x / pxPerSecond * 1000).toLong()
                                    onSeekTo((currentPlayheadMs + deltaMs).coerceIn(0L, totalDurationMs))
                                }
                            }
                    ) {
                        // Top diamond scrubber
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .size(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(RecordRed)
                        )
                        // Line extending down
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 12.dp)
                                .width(2.dp)
                                .fillMaxHeight()
                                .background(RecordRed)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrackHeaderItem(track: TimelineTrack) {
    var isMuted by remember { mutableStateOf(track.isMuted) }
    var isLocked by remember { mutableStateOf(track.isLocked) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(CinemaSlate900)
            .border(androidx.compose.foundation.BorderStroke(0.5.dp, CinemaSlate700))
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = track.type.shortTag,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = when (track.type) {
                    TrackType.VIDEO_V1, TrackType.VIDEO_V2 -> AnamorphicCyan
                    TrackType.VFX -> MagicViolet
                    TrackType.OVERLAY -> AnimationAmber
                    TrackType.TITLES -> GoldCinema
                    TrackType.AUDIO_A1, TrackType.AUDIO_A2, TrackType.SFX_A3 -> AudioEmerald
                }
            )
            Text(
                text = track.name.take(10),
                fontSize = 8.sp,
                color = CinemaSlate400,
                maxLines = 1
            )
        }

        Row {
            // Mute
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clickable { isMuted = !isMuted },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = if (isMuted) RecordRed else CinemaSlate400,
                    modifier = Modifier.size(11.dp)
                )
            }
            // Lock
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clickable { isLocked = !isLocked },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = null,
                    tint = if (isLocked) GoldCinema else CinemaSlate400,
                    modifier = Modifier.size(11.dp)
                )
            }
        }
    }
}

@Composable
fun TimeRuler(
    totalDurationMs: Long,
    pxPerSecond: Float,
    markers: List<FilmMarker>,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .background(CinemaSlate950)
            .border(androidx.compose.foundation.BorderStroke(0.5.dp, CinemaSlate700))
            .pointerInput(totalDurationMs, pxPerSecond) {
                detectTapGestures { offset ->
                    val seekMs = (offset.x / pxPerSecond * 1000).toLong()
                    onSeek(seekMs.coerceIn(0L, totalDurationMs))
                }
            }
    ) {
        val totalSecs = (totalDurationMs / 1000).toInt()
        val stepSec = if (pxPerSecond < 30f) 5 else 1

        for (sec in 0..totalSecs step stepSec) {
            val x = sec * pxPerSecond
            val isMajor = sec % 5 == 0
            val tickHeight = if (isMajor) size.height * 0.5f else size.height * 0.25f

            drawLine(
                color = if (isMajor) CinemaSlate400 else CinemaSlate600,
                start = Offset(x, size.height - tickHeight),
                end = Offset(x, size.height),
                strokeWidth = if (isMajor) 1.5f else 1f
            )
        }

        // Draw Markers
        markers.forEach { marker ->
            val mx = (marker.timeMs / 1000f) * pxPerSecond
            drawCircle(
                color = Color(marker.color),
                radius = 4f,
                center = Offset(mx, size.height * 0.4f)
            )
        }
    }
}

@Composable
fun TrackClipsLane(
    track: TimelineTrack,
    pxPerSecond: Float,
    selectedClipId: String?,
    onSelectClip: (String) -> Unit,
    onSeek: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(CinemaSlate800.copy(alpha = 0.4f))
            .border(androidx.compose.foundation.BorderStroke(0.5.dp, CinemaSlate700))
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val seekMs = (offset.x / pxPerSecond * 1000).toLong()
                    onSeek(seekMs)
                }
            }
    ) {
        track.clips.forEach { clip ->
            val startDp = (clip.startMs / 1000f * pxPerSecond).dp
            val clipWidthDp = (clip.durationMs / 1000f * pxPerSecond).dp
            val isSelected = clip.id == selectedClipId

            Surface(
                modifier = Modifier
                    .offset(x = startDp)
                    .width(clipWidthDp)
                    .fillMaxHeight()
                    .padding(vertical = 3.dp, horizontal = 1.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onSelectClip(clip.id) }
                    .testTag("clip_${clip.id}"),
                color = Color(clip.clipColor).copy(alpha = if (isSelected) 0.95f else 0.75f),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, GoldCinema)
                         else androidx.compose.foundation.BorderStroke(0.5.dp, CinemaSlate600),
                shape = RoundedCornerShape(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = clip.title,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CinemaSlate50,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (clip.appliedMagicName != null) {
                                Box(
                                    modifier = Modifier
                                        .background(MagicViolet, RoundedCornerShape(2.dp))
                                        .padding(horizontal = 3.dp)
                                ) {
                                    Text("MAGIC", fontSize = 7.sp, fontWeight = FontWeight.Black, color = Color.White)
                                }
                                Spacer(modifier = Modifier.width(3.dp))
                            }
                            if (clip.appliedFilmTrickName != null) {
                                Box(
                                    modifier = Modifier
                                        .background(TrickOrange, RoundedCornerShape(2.dp))
                                        .padding(horizontal = 3.dp)
                                ) {
                                    Text("TRICK", fontSize = 7.sp, fontWeight = FontWeight.Black, color = Color.White)
                                }
                                Spacer(modifier = Modifier.width(3.dp))
                            }
                            Text(
                                text = "${clip.durationMs / 1000f}s" + (if (clip.speed != 1.0f) " (${clip.speed}x)" else ""),
                                fontSize = 8.sp,
                                color = CinemaSlate200
                            )
                        }
                    }

                    // Audio Waveform or Film Cut icons
                    if (track.type == TrackType.AUDIO_A1 || track.type == TrackType.AUDIO_A2 || track.type == TrackType.SFX_A3) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = CinemaSlate50.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
