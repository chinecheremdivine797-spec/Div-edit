package com.example.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.GoogleAuthManager
import com.example.data.SampleFilmRepository
import com.example.data.UserStorageManager
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

enum class StudioScreen {
    DASHBOARD,
    EDITOR
}

enum class ActiveStudioSheet {
    NONE,
    HOLLYWOOD_MAGIC,
    FILM_TRICKS,
    AI_VFX_DIRECTOR,
    AI_TOOLS_STUDIO,
    COLOR_STUDIO,
    AUDIO_STUDIO,
    ANIMATION_BUILDER,
    WATERMARK_LOGO,
    EXPORT_STUDIO,
    NEW_FILM_DIALOG,
    MEDIA_IMPORT_DIALOG,
    ACCOUNT_PROFILE_DIALOG,
    MEDIA_PREVIEW_DIALOG,
    RENAME_MEDIA_DIALOG,
    MY_FILMS_DIALOG
}

enum class MediaSortOrder(val label: String) {
    NAME_ASC("Name (A-Z)"),
    NAME_DESC("Name (Z-A)"),
    DATE_ADDED("Recently Added"),
    DURATION("Duration"),
    TYPE("Asset Type"),
    FILE_SIZE("File Size")
}

data class AiDirectorMessage(
    val id: String = UUID.randomUUID().toString(),
    val isFromUser: Boolean,
    val text: String,
    val timestamp: String = "Now",
    val appliedMagicType: HollywoodMagicType? = null,
    val technicalNote: String? = null
)

data class AiToolExecutionState(
    val isProcessing: Boolean = false,
    val progress: Int = 0,
    val stepIndex: Int = 0,
    val isComplete: Boolean = false,
    val logMessage: String = "Ready"
)

data class ExportProgressState(
    val isExporting: Boolean = false,
    val progressPercent: Int = 0,
    val currentFrame: Int = 0,
    val totalFrames: Int = 1440,
    val currentPass: String = "Initializing Neural Compositing Engine...",
    val exportCompleted: Boolean = false,
    val outputFileName: String = "MASTER_CUT_4K_PRORES422HQ.mov"
)

class FilmStudioViewModel(application: Application) : AndroidViewModel(application) {

    private val authManager = GoogleAuthManager(application)
    private val userStorageManager = UserStorageManager()

    // Authentication
    val currentUser: StateFlow<UserAccount?> = authManager.currentUser
    val authError: StateFlow<String?> = authManager.authError

    // User Project Storage
    private val _myFilms = MutableStateFlow<List<FilmProject>>(emptyList())
    val myFilms: StateFlow<List<FilmProject>> = _myFilms.asStateFlow()

    private val _drafts = MutableStateFlow<List<FilmProject>>(emptyList())
    val drafts: StateFlow<List<FilmProject>> = _drafts.asStateFlow()

    private val _exportedProjects = MutableStateFlow<List<FilmProject>>(emptyList())
    val exportedProjects: StateFlow<List<FilmProject>> = _exportedProjects.asStateFlow()

    private val _screen = MutableStateFlow(StudioScreen.DASHBOARD)
    val screen: StateFlow<StudioScreen> = _screen.asStateFlow()

    private val _activeSheet = MutableStateFlow(ActiveStudioSheet.NONE)
    val activeSheet: StateFlow<ActiveStudioSheet> = _activeSheet.asStateFlow()

    private val _currentProject = MutableStateFlow(SampleFilmRepository.createDefaultProject())
    val currentProject: StateFlow<FilmProject> = _currentProject.asStateFlow()

    private val _recentProjects = MutableStateFlow(SampleFilmRepository.sampleRecentProjects)
    val recentProjects: StateFlow<List<FilmProject>> = _recentProjects.asStateFlow()

    private val _mediaAssets = MutableStateFlow(SampleFilmRepository.sampleMediaAssets)
    val mediaAssets: StateFlow<List<MediaAsset>> = _mediaAssets.asStateFlow()

    // Media Library Filters & Operations
    private val _mediaSearchQuery = MutableStateFlow("")
    val mediaSearchQuery: StateFlow<String> = _mediaSearchQuery.asStateFlow()

    private val _selectedFolderCategory = MutableStateFlow<String?>(null)
    val selectedFolderCategory: StateFlow<String?> = _selectedFolderCategory.asStateFlow()

    private val _mediaSortOrder = MutableStateFlow(MediaSortOrder.NAME_ASC)
    val mediaSortOrder: StateFlow<MediaSortOrder> = _mediaSortOrder.asStateFlow()

    private val _selectedMediaForPreview = MutableStateFlow<MediaAsset?>(null)
    val selectedMediaForPreview: StateFlow<MediaAsset?> = _selectedMediaForPreview.asStateFlow()

    private val _selectedMediaForRename = MutableStateFlow<MediaAsset?>(null)
    val selectedMediaForRename: StateFlow<MediaAsset?> = _selectedMediaForRename.asStateFlow()

    private val _selectedClipForReplace = MutableStateFlow<TimelineClip?>(null)
    val selectedClipForReplace: StateFlow<TimelineClip?> = _selectedClipForReplace.asStateFlow()

    init {
        // Load initial user session
        currentUser.value?.userId?.let { uid ->
            loadUserSession(uid)
        }
    }

    private val _filmTricks = MutableStateFlow(SampleFilmRepository.sampleFilmTricks)
    val filmTricks: StateFlow<List<FilmTrickPreset>> = _filmTricks.asStateFlow()

    // Playback state
    private val _currentPlayheadMs = MutableStateFlow(16_800L) // Cue up on dramatic teleport clip
    val currentPlayheadMs: StateFlow<Long> = _currentPlayheadMs.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _timelineZoom = MutableStateFlow(1.0f) // 0.5f to 4.0f
    val timelineZoom: StateFlow<Float> = _timelineZoom.asStateFlow()

    private val _showSafeGuides = MutableStateFlow(false)
    val showSafeGuides: StateFlow<Boolean> = _showSafeGuides.asStateFlow()

    private val _showAnamorphicScope = MutableStateFlow(true)
    val showAnamorphicScope: StateFlow<Boolean> = _showAnamorphicScope.asStateFlow()

    private val _isMagnetSnap = MutableStateFlow(true)
    val isMagnetSnap: StateFlow<Boolean> = _isMagnetSnap.asStateFlow()

    private val _inPointMs = MutableStateFlow<Long?>(null)
    val inPointMs: StateFlow<Long?> = _inPointMs.asStateFlow()

    private val _outPointMs = MutableStateFlow<Long?>(null)
    val outPointMs: StateFlow<Long?> = _outPointMs.asStateFlow()

    // Selection
    private val _selectedClipId = MutableStateFlow<String?>("clip_teleport_burst")
    val selectedClipId: StateFlow<String?> = _selectedClipId.asStateFlow()

    private val _selectedTrackId = MutableStateFlow<String?>("track_vfx")
    val selectedTrackId: StateFlow<String?> = _selectedTrackId.asStateFlow()

    // Watermark & Logo config
    private val _watermarkConfig = MutableStateFlow(WatermarkConfig())
    val watermarkConfig: StateFlow<WatermarkConfig> = _watermarkConfig.asStateFlow()

    private val _logoSequenceConfig = MutableStateFlow(LogoSequenceConfig())
    val logoSequenceConfig: StateFlow<LogoSequenceConfig> = _logoSequenceConfig.asStateFlow()

    // Hollywood Magic Process State
    private val _magicProcessState = MutableStateFlow(
        HollywoodMagicProcessState(
            magicType = HollywoodMagicType.DISAPPEAR,
            selectedClipTitle = "Roof_Combat_Dual.mov"
        )
    )
    val magicProcessState: StateFlow<HollywoodMagicProcessState> = _magicProcessState.asStateFlow()

    // AI VFX Director Messages
    private val _aiMessages = MutableStateFlow(
        listOf(
            AiDirectorMessage(
                isFromUser = false,
                text = "Welcome to AI VFX Director. I can interpret cinematic intent, build multi-pass compositing pipelines, configure keyframed volumetric emitters, or execute Hollywood Magic film tricks. What would you like to direct in this scene?",
                technicalNote = "DIV EDIT AI Real VFX Engine Online (Optical Flow / Neural Matting / 35mm Grain)"
            )
        )
    )
    val aiMessages: StateFlow<List<AiDirectorMessage>> = _aiMessages.asStateFlow()

    // Export State
    private val _exportState = MutableStateFlow(ExportProgressState())
    val exportState: StateFlow<ExportProgressState> = _exportState.asStateFlow()

    // 20+ AI Cinematic Tools Suite
    val aiTools: List<AiToolSpec> = AiToolsRegistry.tools

    private val _selectedAiTool = MutableStateFlow<AiToolSpec>(AiToolsRegistry.tools.first())
    val selectedAiTool: StateFlow<AiToolSpec> = _selectedAiTool.asStateFlow()

    private val _aiToolExecutionState = MutableStateFlow(AiToolExecutionState())
    val aiToolExecutionState: StateFlow<AiToolExecutionState> = _aiToolExecutionState.asStateFlow()

    private val _aiToolParamValues = MutableStateFlow<Map<String, Float>>(
        AiToolsRegistry.tools.first().defaultParameters.associate { it.id to it.value }
    )
    val aiToolParamValues: StateFlow<Map<String, Float>> = _aiToolParamValues.asStateFlow()

    // Notification toast
    private val _systemStatusText = MutableStateFlow("DIV EDIT AI Ready // Turn Footage Into Cinema.")
    val systemStatusText: StateFlow<String> = _systemStatusText.asStateFlow()

    // Undo / Redo history
    private val undoStack = mutableListOf<FilmProject>()
    private val redoStack = mutableListOf<FilmProject>()

    private var playbackJob: Job? = null

    fun setScreen(screen: StudioScreen) {
        _screen.value = screen
    }

    fun openSheet(sheet: ActiveStudioSheet) {
        _activeSheet.value = sheet
    }

    fun closeSheet() {
        _activeSheet.value = ActiveStudioSheet.NONE
    }

    fun selectProject(project: FilmProject) {
        saveUndoState()
        _currentProject.value = project
        _currentPlayheadMs.value = 0L
        _selectedClipId.value = project.tracks.firstOrNull()?.clips?.firstOrNull()?.id
        _screen.value = StudioScreen.EDITOR
        showStatus("Loaded Film: ${project.title}")
    }

    fun createNewProject(
        title: String,
        director: String,
        aspectRatio: AspectRatioPreset,
        fps: FpsOption,
        colorSpace: ColorSpaceOption
    ) {
        saveUndoState()
        val newProj = FilmProject(
            id = "proj_${System.currentTimeMillis()}",
            title = title.ifBlank { "UNTITLED CINEMA MASTER" },
            director = director.ifBlank { "Director" },
            aspectRatio = aspectRatio,
            fps = fps,
            colorSpace = colorSpace,
            durationMs = 60_000L,
            tracks = listOf(
                TimelineTrack("tr_v1", TrackType.VIDEO_V1, "V1: Live Action Master"),
                TimelineTrack("tr_vfx", TrackType.VFX, "FX: Hollywood Magic"),
                TimelineTrack("tr_adj", TrackType.OVERLAY, "ADJ: 35mm Color Grade"),
                TimelineTrack("tr_txt", TrackType.TITLES, "TXT: Titles & Lower Thirds"),
                TimelineTrack("tr_a1", TrackType.AUDIO_A1, "A1: Dialog Master"),
                TimelineTrack("tr_a2", TrackType.AUDIO_A2, "A2: Score Track"),
                TimelineTrack("tr_a3", TrackType.SFX_A3, "A3: Foley & Hits")
            )
        )
        _currentProject.value = newProj
        _recentProjects.value = listOf(newProj) + _recentProjects.value
        _screen.value = StudioScreen.EDITOR
        closeSheet()
        showStatus("Created new film project: ${newProj.title}")
    }

    // Playback control
    fun togglePlayPause() {
        if (_isPlaying.value) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    fun startPlayback() {
        _isPlaying.value = true
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val stepMs = 40L // ~25 FPS ticker
            while (_isPlaying.value) {
                delay(stepMs)
                val totalDuration = _currentProject.value.durationMs
                val next = _currentPlayheadMs.value + stepMs
                if (next >= totalDuration) {
                    _currentPlayheadMs.value = 0L
                } else {
                    _currentPlayheadMs.value = next
                }
            }
        }
    }

    fun pausePlayback() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
    }

    fun seekTo(timeMs: Long) {
        val bounded = timeMs.coerceIn(0L, _currentProject.value.durationMs)
        _currentPlayheadMs.value = bounded
    }

    fun stepFrame(forward: Boolean) {
        val frameDuration = (1000L / _currentProject.value.fps.fps)
        val target = if (forward) _currentPlayheadMs.value + frameDuration else _currentPlayheadMs.value - frameDuration
        seekTo(target)
    }

    fun setInPoint() {
        _inPointMs.value = _currentPlayheadMs.value
        showStatus("Mark In set at ${formatTimecode(_currentPlayheadMs.value)}")
    }

    fun setOutPoint() {
        _outPointMs.value = _currentPlayheadMs.value
        showStatus("Mark Out set at ${formatTimecode(_currentPlayheadMs.value)}")
    }

    fun clearInOutPoints() {
        _inPointMs.value = null
        _outPointMs.value = null
        showStatus("Cleared In / Out markers")
    }

    fun setTimelineZoom(zoom: Float) {
        _timelineZoom.value = zoom.coerceIn(0.4f, 4.0f)
    }

    fun toggleSafeGuides() {
        _showSafeGuides.value = !_showSafeGuides.value
    }

    fun toggleAnamorphicScope() {
        _showAnamorphicScope.value = !_showAnamorphicScope.value
    }

    fun toggleMagnetSnap() {
        _isMagnetSnap.value = !_isMagnetSnap.value
    }

    // Selection
    fun selectClip(clipId: String, trackId: String) {
        _selectedClipId.value = clipId
        _selectedTrackId.value = trackId
    }

    fun getSelectedClip(): TimelineClip? {
        val clipId = _selectedClipId.value ?: return null
        for (track in _currentProject.value.tracks) {
            val found = track.clips.find { it.id == clipId }
            if (found != null) return found
        }
        return null
    }

    // Edit operations
    private fun saveUndoState() {
        undoStack.add(_currentProject.value)
        redoStack.clear()
        if (undoStack.size > 25) {
            undoStack.removeAt(0)
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.add(_currentProject.value)
            val previous = undoStack.removeAt(undoStack.lastIndex)
            _currentProject.value = previous
            showStatus("Undo successful")
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.add(_currentProject.value)
            val next = redoStack.removeAt(redoStack.lastIndex)
            _currentProject.value = next
            showStatus("Redo successful")
        }
    }

    fun splitClipAtPlayhead() {
        val clip = getSelectedClip() ?: run {
            showStatus("Select a clip to split at playhead")
            return
        }
        val playhead = _currentPlayheadMs.value
        if (playhead <= clip.startMs || playhead >= clip.endMs) {
            showStatus("Playhead must be inside selected clip to split")
            return
        }

        saveUndoState()
        val firstHalf = clip.copy(
            id = "${clip.id}_part1",
            endMs = playhead
        )
        val secondHalf = clip.copy(
            id = "${clip.id}_part2",
            startMs = playhead,
            title = "${clip.title} (Split)"
        )

        val updatedTracks = _currentProject.value.tracks.map { track ->
            if (track.id == clip.trackId) {
                val newClips = track.clips.flatMap { c ->
                    if (c.id == clip.id) listOf(firstHalf, secondHalf) else listOf(c)
                }
                track.copy(clips = newClips)
            } else track
        }
        _currentProject.value = _currentProject.value.copy(tracks = updatedTracks)
        _selectedClipId.value = secondHalf.id
        showStatus("Split clip '${clip.title}' at ${formatTimecode(playhead)}")
    }

    fun deleteSelectedClip() {
        val clip = getSelectedClip() ?: return
        saveUndoState()
        val updatedTracks = _currentProject.value.tracks.map { track ->
            if (track.id == clip.trackId) {
                track.copy(clips = track.clips.filter { it.id != clip.id })
            } else track
        }
        _currentProject.value = _currentProject.value.copy(tracks = updatedTracks)
        _selectedClipId.value = null
        showStatus("Deleted clip '${clip.title}'")
    }

    fun duplicateSelectedClip() {
        val clip = getSelectedClip() ?: return
        saveUndoState()
        val duplicate = clip.copy(
            id = "clip_${System.currentTimeMillis()}",
            startMs = clip.endMs + 500L,
            endMs = clip.endMs + 500L + clip.durationMs,
            title = "${clip.title} (Copy)"
        )
        val updatedTracks = _currentProject.value.tracks.map { track ->
            if (track.id == clip.trackId) {
                track.copy(clips = track.clips + duplicate)
            } else track
        }
        _currentProject.value = _currentProject.value.copy(tracks = updatedTracks)
        _selectedClipId.value = duplicate.id
        showStatus("Duplicated clip '${clip.title}'")
    }

    fun trimClip(clipId: String, newStartMs: Long, newEndMs: Long) {
        if (newEndMs <= newStartMs + 200L) return
        saveUndoState()
        val updatedTracks = _currentProject.value.tracks.map { track ->
            val updatedClips = track.clips.map { c ->
                if (c.id == clipId) {
                    c.copy(startMs = newStartMs, endMs = newEndMs)
                } else c
            }
            track.copy(clips = updatedClips)
        }
        _currentProject.value = _currentProject.value.copy(tracks = updatedTracks)
    }

    fun updateSelectedClipTransform(transform: TransformState) {
        val clip = getSelectedClip() ?: return
        updateClip(clip.copy(transform = transform))
    }

    fun updateSelectedClipColorGrade(colorGrade: ColorGrade) {
        val clip = getSelectedClip() ?: return
        updateClip(clip.copy(colorGrade = colorGrade))
    }

    fun updateSelectedClipSpeed(speed: Float) {
        val clip = getSelectedClip() ?: return
        val currentDuration = clip.endMs - clip.startMs
        val newDuration = (currentDuration / (speed / clip.speed)).toLong().coerceAtLeast(300L)
        updateClip(clip.copy(speed = speed, endMs = clip.startMs + newDuration))
        showStatus("Speed set to ${speed}x")
    }

    fun toggleSelectedClipReverse() {
        val clip = getSelectedClip() ?: return
        val reversed = !clip.isReversed
        updateClip(clip.copy(isReversed = reversed))
        showStatus(if (reversed) "Clip Reversed (Play backward)" else "Clip Normal Direction")
    }

    fun toggleSelectedClipFreezeFrame() {
        val clip = getSelectedClip() ?: return
        val freeze = !clip.isFreezeFrame
        updateClip(clip.copy(isFreezeFrame = freeze))
        showStatus(if (freeze) "Freeze Frame Enabled" else "Freeze Frame Disabled")
    }

    fun updateSelectedClipBlendMode(blendMode: BlendModeType) {
        val clip = getSelectedClip() ?: return
        updateClip(clip.copy(blendMode = blendMode))
        showStatus("Blend Mode: ${blendMode.label}")
    }

    fun updateSelectedClipOpacity(opacity: Float) {
        val clip = getSelectedClip() ?: return
        updateClip(clip.copy(opacity = opacity.coerceIn(0f, 1f)))
    }

    fun updateSelectedClipVolume(vol: Float) {
        val clip = getSelectedClip() ?: return
        updateClip(clip.copy(volume = vol.coerceIn(0f, 2f)))
    }

    private fun updateClip(updatedClip: TimelineClip) {
        val updatedTracks = _currentProject.value.tracks.map { track ->
            if (track.id == updatedClip.trackId) {
                track.copy(clips = track.clips.map { if (it.id == updatedClip.id) updatedClip else it })
            } else track
        }
        _currentProject.value = _currentProject.value.copy(tracks = updatedTracks)
    }

    // Hollywood Magic 1-Click Process
    fun startHollywoodMagicProcess(magicType: HollywoodMagicType) {
        val selectedClip = getSelectedClip() ?: _currentProject.value.tracks.firstOrNull()?.clips?.firstOrNull()
        val clipTitle = selectedClip?.title ?: "Master_Scene_Plate.mov"
        
        _magicProcessState.value = HollywoodMagicProcessState(
            magicType = magicType,
            selectedClipTitle = clipTitle,
            isProcessing = true,
            isComplete = false,
            currentStepIndex = 0
        )
        openSheet(ActiveStudioSheet.HOLLYWOOD_MAGIC)

        // Run the 9-step deep compositing simulation
        viewModelScope.launch {
            val totalSteps = _magicProcessState.value.steps.size
            for (i in 0 until totalSteps) {
                val updatedSteps = _magicProcessState.value.steps.mapIndexed { index, step ->
                    when {
                        index < i -> step.copy(status = "Complete")
                        index == i -> step.copy(status = "Processing...")
                        else -> step.copy(status = "Pending")
                    }
                }
                _magicProcessState.value = _magicProcessState.value.copy(
                    steps = updatedSteps,
                    currentStepIndex = i
                )
                delay(320L)
            }
            // Mark all complete
            val finalSteps = _magicProcessState.value.steps.map { it.copy(status = "Complete") }
            _magicProcessState.value = _magicProcessState.value.copy(
                steps = finalSteps,
                isProcessing = false,
                isComplete = true
            )
            showStatus("Hollywood Magic '${magicType.title}' 9-Step Compositing Complete")
        }
    }

    fun applyMagicToTimeline() {
        val magicState = _magicProcessState.value
        val clip = getSelectedClip()
        saveUndoState()

        if (clip != null) {
            updateClip(
                clip.copy(
                    appliedMagicId = magicState.magicType.name,
                    appliedMagicName = "Hollywood Magic: ${magicState.magicType.title}",
                    clipColor = 0xFFA55EEA
                )
            )
        } else {
            // Add as a new VFX track clip
            val newVfxClip = TimelineClip(
                id = "magic_${System.currentTimeMillis()}",
                trackId = "track_vfx",
                assetId = "asset_vfx_1",
                title = "MAGIC: ${magicState.magicType.title.uppercase()}",
                startMs = _currentPlayheadMs.value,
                endMs = _currentPlayheadMs.value + 4000L,
                blendMode = magicState.blendMode,
                clipColor = 0xFFA55EEA,
                appliedMagicId = magicState.magicType.name,
                appliedMagicName = "Hollywood Magic: ${magicState.magicType.title}"
            )
            val updatedTracks = _currentProject.value.tracks.map { track ->
                if (track.type == TrackType.VFX) {
                    track.copy(clips = track.clips + newVfxClip)
                } else track
            }
            _currentProject.value = _currentProject.value.copy(tracks = updatedTracks)
        }

        closeSheet()
        showStatus("Applied Hollywood Magic: ${magicState.magicType.title} to timeline")
    }

    // 20+ AI Cinematic Tools Suite Methods
    fun openAiToolsStudio(tool: AiToolSpec? = null) {
        if (tool != null) {
            selectAiTool(tool)
        }
        openSheet(ActiveStudioSheet.AI_TOOLS_STUDIO)
    }

    fun selectAiTool(tool: AiToolSpec) {
        _selectedAiTool.value = tool
        _aiToolParamValues.value = tool.defaultParameters.associate { it.id to it.value }
        _aiToolExecutionState.value = AiToolExecutionState(
            isProcessing = false,
            progress = 0,
            stepIndex = 0,
            isComplete = false,
            logMessage = "Ready to initialize ${tool.shortName}"
        )
    }

    fun updateAiToolParam(paramId: String, value: Float) {
        val current = _aiToolParamValues.value.toMutableMap()
        current[paramId] = value
        _aiToolParamValues.value = current
    }

    fun startAiToolProcess(tool: AiToolSpec) {
        _aiToolExecutionState.value = AiToolExecutionState(
            isProcessing = true,
            progress = 10,
            stepIndex = 0,
            isComplete = false,
            logMessage = "Initializing Phase 1: ${tool.processingSteps.firstOrNull() ?: "Preparing..."}"
        )

        viewModelScope.launch {
            val totalSteps = tool.processingSteps.size.coerceAtLeast(1)
            for (i in 0 until totalSteps) {
                delay(380L)
                val stepProgress = ((i + 1) * 100 / totalSteps).coerceAtMost(95)
                val stepDescription = tool.processingSteps.getOrNull(i) ?: "Processing neural layers..."
                _aiToolExecutionState.value = AiToolExecutionState(
                    isProcessing = true,
                    progress = stepProgress,
                    stepIndex = i + 1,
                    isComplete = false,
                    logMessage = "Phase ${i + 1}/$totalSteps: $stepDescription"
                )
            }
            delay(250L)
            _aiToolExecutionState.value = AiToolExecutionState(
                isProcessing = false,
                progress = 100,
                stepIndex = totalSteps,
                isComplete = true,
                logMessage = "Neural pass complete. Ready to apply to timeline."
            )
            showStatus("AI Engine #${tool.number} '${tool.shortName}' complete")
        }
    }

    fun applyAiToolToTimeline(tool: AiToolSpec) {
        saveUndoState()
        val clip = getSelectedClip() ?: _currentProject.value.tracks.firstOrNull()?.clips?.firstOrNull()
        val playhead = _currentPlayheadMs.value

        when {
            tool.outputEffectType.startsWith("COLOR_GRADE") -> {
                if (clip != null) {
                    val updatedGrade = when (tool.id) {
                        "ai_face_relight" -> clip.colorGrade.copy(exposure = 0.35f, contrast = 18f, temperature = 20f)
                        "ai_color_match_frame" -> clip.colorGrade.copy(contrast = 24f, saturation = 15f, lutPreset = "Hollywood Teal & Orange")
                        "ai_vintage_colorizer" -> clip.colorGrade.copy(saturation = 35f, tint = 20f, lutPreset = "35mm Vintage Print", filmGrain = FilmGrainType.GRAIN_35MM)
                        "ai_deaging_beauty_retouch" -> clip.colorGrade.copy(temperature = 10f, vignette = 15f)
                        else -> clip.colorGrade.copy(filmGrain = FilmGrainType.GRAIN_16MM)
                    }
                    updateClip(
                        clip.copy(
                            colorGrade = updatedGrade,
                            appliedFilmTrickName = "AI: ${tool.shortName}",
                            clipColor = tool.accentColor
                        )
                    )
                    showStatus("Applied ${tool.name} grade to '${clip.title}'")
                } else {
                    showStatus("Applied ${tool.name} neural grade across master project")
                }
            }
            tool.outputEffectType.startsWith("AUDIO_TRACK") -> {
                val newAudioClip = TimelineClip(
                    id = "ai_audio_${System.currentTimeMillis()}",
                    trackId = "track_audio_1",
                    assetId = "asset_audio_dialogue",
                    title = "AI: ${tool.shortName.uppercase()}",
                    startMs = playhead,
                    endMs = playhead + 5000L,
                    volume = 1.0f,
                    clipColor = tool.accentColor
                )
                val updatedTracks = _currentProject.value.tracks.map { track ->
                    if (track.type == TrackType.AUDIO_A1 || track.type == TrackType.AUDIO_A2 || track.type == TrackType.SFX_A3) {
                        track.copy(clips = track.clips + newAudioClip)
                    } else track
                }
                _currentProject.value = _currentProject.value.copy(tracks = updatedTracks)
                showStatus("Injected ${tool.name} audio stem at ${formatTimecode(playhead)}")
            }
            tool.outputEffectType == "CLIP_OPTICAL_RETIME" -> {
                if (clip != null) {
                    val currentDur = clip.endMs - clip.startMs
                    val newDur = (currentDur * 2.5f).toLong()
                    updateClip(
                        clip.copy(
                            speed = 0.4f,
                            endMs = clip.startMs + newDur,
                            appliedFilmTrickName = "AI Optical Slow-Mo 960fps",
                            clipColor = tool.accentColor
                        )
                    )
                    showStatus("Retimed '${clip.title}' with 960fps optical flow")
                } else {
                    showStatus("Select a clip to apply Optical Flow Slow-Mo")
                }
            }
            tool.outputEffectType.startsWith("TRANSFORM") -> {
                if (clip != null) {
                    val updatedTransform = when (tool.id) {
                        "ai_camera_shake_inertia" -> clip.transform.copy(rotationDeg = 1.5f, scale = 1.06f)
                        "ai_auto_framing" -> clip.transform.copy(scale = 1.15f)
                        else -> clip.transform
                    }
                    updateClip(
                        clip.copy(
                            transform = updatedTransform,
                            appliedFilmTrickName = "AI: ${tool.shortName}",
                            clipColor = tool.accentColor
                        )
                    )
                    showStatus("Applied ${tool.name} kinetic transform to '${clip.title}'")
                } else {
                    showStatus("Select a clip to apply ${tool.shortName}")
                }
            }
            else -> {
                // VFX or Master Layer
                val newVfxClip = TimelineClip(
                    id = "ai_layer_${System.currentTimeMillis()}",
                    trackId = "track_vfx",
                    assetId = "asset_vfx_1",
                    title = "AI #${tool.number}: ${tool.shortName.uppercase()}",
                    startMs = playhead,
                    endMs = playhead + 4500L,
                    blendMode = BlendModeType.SCREEN,
                    clipColor = tool.accentColor,
                    appliedMagicName = "AI: ${tool.name}"
                )
                val updatedTracks = _currentProject.value.tracks.map { track ->
                    if (track.type == TrackType.VFX) {
                        track.copy(clips = track.clips + newVfxClip)
                    } else track
                }
                _currentProject.value = _currentProject.value.copy(tracks = updatedTracks)
                showStatus("Composited AI Engine #${tool.number} '${tool.shortName}' onto timeline")
            }
        }
        closeSheet()
    }

    // Film Tricks
    fun applyFilmTrick(preset: FilmTrickPreset) {
        val clip = getSelectedClip()
        saveUndoState()
        if (clip != null) {
            updateClip(
                clip.copy(
                    appliedFilmTrickId = preset.id,
                    appliedFilmTrickName = preset.name,
                    colorGrade = clip.colorGrade.copy(
                        vignette = (clip.colorGrade.vignette + 30f).coerceAtMost(100f),
                        filmGrain = FilmGrainType.GRAIN_35MM
                    )
                )
            )
            showStatus("Applied Film Trick: ${preset.name} to '${clip.title}'")
        } else {
            showStatus("Select a clip to apply Film Trick: ${preset.name}")
        }
        closeSheet()
    }

    fun toggleFilmTrickFavorite(presetId: String) {
        _filmTricks.value = _filmTricks.value.map {
            if (it.id == presetId) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    fun addCustomFilmTrick(custom: CustomFilmTrick) {
        val newPreset = FilmTrickPreset(
            id = "custom_${System.currentTimeMillis()}",
            name = custom.name.ifBlank { "Custom Film Trick" },
            description = "Custom composite (${custom.maskType}, ${custom.particleType}, ${custom.audioCue})",
            category = custom.baseCategory,
            durationMs = 2500L,
            intensity = 0.9f,
            isFavorite = true,
            isCustom = true,
            tagColor = 0xFFFFB834
        )
        _filmTricks.value = listOf(newPreset) + _filmTricks.value
        showStatus("Saved custom Film Trick preset '${newPreset.name}' to studio library")
    }

    // AI VFX Director
    fun sendAiDirectorPrompt(prompt: String) {
        if (prompt.isBlank()) return
        val userMsg = AiDirectorMessage(
            isFromUser = true,
            text = prompt
        )
        _aiMessages.value = _aiMessages.value + userMsg

        viewModelScope.launch {
            delay(600L) // AI thinking delay
            val interpretation = interpretVfxPrompt(prompt)
            _aiMessages.value = _aiMessages.value + interpretation
            
            // If interpretation resolved to a Hollywood Magic effect, automatically configure it
            interpretation.appliedMagicType?.let { magicType ->
                val clip = getSelectedClip()
                if (clip != null) {
                    updateClip(
                        clip.copy(
                            appliedMagicId = magicType.name,
                            appliedMagicName = "AI VFX: ${magicType.title}",
                            clipColor = 0xFFA55EEA
                        )
                    )
                    showStatus("AI VFX Director applied ${magicType.title} to selected clip")
                }
            }
        }
    }

    private fun interpretVfxPrompt(prompt: String): AiDirectorMessage {
        val lower = prompt.lowercase()
        return when {
            lower.contains("relight") || lower.contains("gaffer") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured AI Engine #01 'Face Relight': (1) Estimated 3D facial mesh surface normals. (2) Cast warm key light at 45° with 40% fill ratio. (3) Added specular eye catch-light reflection.",
                    technicalNote = "BRDF Shader: 3D normal vector smoothing set to 80%. Dynamic skin specular enabled."
                )
            }
            lower.contains("voice clone") || lower.contains("adr") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured AI Engine #02 'Voice Clone ADR': (1) Extracted actor vocal spectral profile. (2) Synthesized replacement dialogue with neural vocoder. (3) Quantized phonemes to match mouth markers.",
                    technicalNote = "ADR Track: 95% timbre match accuracy. Room impulse response applied."
                )
            }
            lower.contains("sky") || lower.contains("atmosphere") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured AI Engine #03 'Sky Replacement': (1) Segmented complex horizon with tree line sub-pixel feather. (2) Projected 360° HDR sunset sky dome. (3) Cast warm ambient spill onto foreground geometry.",
                    technicalNote = "Atmosphere: Volumetric horizon depth fog set to 50% density with anamorphic sun flare."
                )
            }
            lower.contains("rotoscope") || lower.contains("hair") || lower.contains("matte") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured AI Engine #04 'Smart Rotoscope': (1) Isolated subject with sub-pixel hair strand matte. (2) Locked temporal anti-flicker boundary. (3) Un-mixed background color spill from actor edges.",
                    technicalNote = "Green-Screen Free: Defringe radius set to 0.70. Sub-pixel alpha mask active."
                )
            }
            lower.contains("slow-mo") || lower.contains("slow motion") || lower.contains("optical flow") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured AI Engine #16 'Optical Slow-Mo': (1) Calculated bi-directional optical flow vectors between frames. (2) Synthesized intermediate frames up to 960fps. (3) Rendered 180° virtual shutter motion blur.",
                    technicalNote = "Optical Retime: Occlusion repair active to prevent edge warping."
                )
            }
            lower.contains("denoise") || lower.contains("noise") || lower.contains("sensor") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured AI Engine #05 'Sensor Denoise': (1) Decomposed spatial-temporal frequency bands. (2) Eliminated digital chroma speckles & 8-bit banding. (3) Retained natural 35mm film grain texture.",
                    technicalNote = "Clean Sensor: 90% chroma smoothing with 85% luminance edge preservation."
                )
            }
            lower.contains("color match") || lower.contains("reference") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured AI Engine #06 'Color Match': (1) Sampled reference still luminance & chrominance 3D distribution. (2) Segmented skin tones for protection. (3) Applied 64x64x64 3D LUT transfer matrix.",
                    technicalNote = "Grade Transfer: Black level roll-off locked to reference film curve."
                )
            }
            lower.contains("depth") || lower.contains("bokeh") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured AI Engine #07 'Depth & Bokeh': (1) Inferred continuous 16-bit monocular depth map. (2) Set focal plane distance to foreground talent. (3) Rendered f/1.2 anamorphic oval bokeh highlights.",
                    technicalNote = "Z-Depth: Cat-eye swirl vignette active on outer edge speculars."
                )
            }
            lower.contains("beat") || lower.contains("music") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured AI Engine #08 'Beat Sync Cut': (1) Detected audio downbeats and drop transients. (2) Sliced timeline cuts to musical rhythm grid. (3) Synchronized zoom snaps on kick drum hits.",
                    technicalNote = "Rhythm Editor: Cut density quantized to quarter-note tempo markers."
                )
            }
            lower.contains("foley") || lower.contains("footstep") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured AI Engine #09 'Foley Synthesizer': (1) Tracked physical shoe contact points on screen. (2) Classified surface collision mechanics. (3) Synthesized custom spatial stereo impact cues.",
                    technicalNote = "Generative Audio: High-impact bass thud with concrete acoustic resonance."
                )
            }
            lower.contains("clean voice") || lower.contains("isolate") || lower.contains("reverb") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured AI Engine #10 'Voice Clean': (1) Decomposed audio into speech and ambient stems. (2) Stripped howling wind and street rumble. (3) Eliminated room reverb decay reflections.",
                    technicalNote = "Dialogue Isolation: 95% vocal extraction with sibilance de-esser."
                )
            }
            lower.contains("erase") || lower.contains("inpaint") || lower.contains("boom") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured AI Engine #11 'Object Eraser': (1) Tracked target bounding mask across clip frames. (2) Synthesized temporal background clean plate. (3) Blended authentic grain over inpaint repair.",
                    technicalNote = "Clean Plate: Temporal lock active. Wire and boom mic vanished seamlessly."
                )
            }
            lower.contains("auto-frame") || lower.contains("vertical") || lower.contains("smart crop") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured AI Engine #12 'Smart Auto-Framing': (1) Tracked character action center and eye gaze. (2) Re-framed 2.39:1 master into 9:16 vertical cinema crop. (3) Applied smooth virtual camera pan damping.",
                    technicalNote = "Aspect Director: Speaker priority bias at 90% with safe headroom margin."
                )
            }
            lower.contains("upscale") || lower.contains("8k") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured AI Engine #14 '8K Super-Resolution': (1) Generative super-resolution neural pass. (2) Re-synthesized high-frequency fabric and skin textures. (3) Suppressed edge ringing halos.",
                    technicalNote = "Theatrical Master: Ready for 8K DCI projection delivery."
                )
            }
            lower.contains("subtitle") || lower.contains("caption") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured AI Engine #15 'Kinetic Captions': (1) Frame-accurate speech recognition executed. (2) Generated kinetic typography overlay. (3) Applied bounce spring physics on active words.",
                    technicalNote = "Karaoke Engine: Active word pop glow enabled with dark pill backdrop."
                )
            }
            lower.contains("flare") || lower.contains("streak") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured AI Engine #18 'Anamorphic Flares': (1) Located specular highlight points above threshold. (2) Raytraced cylindrical anamorphic blue streak rays. (3) Layered multi-blade aperture iris ghosts.",
                    technicalNote = "Optical Physics: 90% streak length with cyan/gold chromatic dispersion."
                )
            }
            lower.contains("disappear") || lower.contains("vanish") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured 'DISAPPEAR' sequence: (1) Running neural segmentation on target actor. (2) Sub-pixel temporal tracking. (3) Generating 24-frame clean plate inpainting. (4) Comping optical distortion and ember dissipation.",
                    appliedMagicType = HollywoodMagicType.DISAPPEAR,
                    technicalNote = "Limitation Check: In high-motion blur backgrounds, temporal inpainting requires 2 clean reference frames."
                )
            }
            lower.contains("teleport") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured 'TELEPORT' sequence: (1) Spatial refraction bubble configured at playhead. (2) Cyan quantum capacitor discharge. (3) Sub-bass hit cue aligned on Track A3. (4) Re-entry landing flare 1.2s downstream.",
                    appliedMagicType = HollywoodMagicType.TELEPORT,
                    technicalNote = "Audio Cue synchronized: Spatial_Teleport_SubImpact.wav added to SFX track."
                )
            }
            lower.contains("explode") || lower.contains("explosion") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured 'EXPLOSION' VFX pass: (1) Hollywood pyro blast keyed with Additive blend mode. (2) Dynamic camera shake + impact hit applied to master camera. (3) Cast lighting highlights mapped onto actor shoulders.",
                    appliedMagicType = HollywoodMagicType.EXPLOSION,
                    technicalNote = "Physics Note: Debris particle velocity bounded to 45m/s with simulated air drag."
                )
            }
            lower.contains("smoke") || lower.contains("materialize") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured 'MATERIALIZE FROM SMOKE': (1) Volumetric fluid smoke plume layered on V2. (2) Actor luminance mask ramped from 0% to 100%. (3) Atmospheric light wrap matching ambient environment.",
                    appliedMagicType = HollywoodMagicType.MATERIALIZE,
                    technicalNote = "Volumetric smoke density set to 0.85 with turbulent buoyancy."
                )
            }
            lower.contains("superhero landing") || lower.contains("landing") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured 'SUPERHERO LANDING': (1) Three-point landing frame freeze + speed ramp. (2) Radial crater dust ring blast on floor plane. (3) Dual frame camera punch with -12% zoom snap.",
                    appliedMagicType = HollywoodMagicType.SUPERHERO_LANDING,
                    technicalNote = "Impact frame synchronized with 0.3s camera shake decay."
                )
            }
            lower.contains("lightning") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured 'LIGHTNING' energy: (1) Fractal high-voltage electrical arcs positioned along subject spine and hands. (2) Specular exposure bursts on environment walls. (3) Cyan-violet electrical halation.",
                    appliedMagicType = HollywoodMagicType.LIGHTNING,
                    technicalNote = "Frequency set to 14 branch arcs per frame with random seed jitter."
                )
            }
            lower.contains("invisible") || lower.contains("invisibility") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured 'INVISIBILITY': (1) Refractive optical camouflage mask keyed over subject. (2) Normal map air ripple tracking subject motion. (3) Background pass magnified +2% inside subject silhouette.",
                    appliedMagicType = HollywoodMagicType.INVISIBILITY,
                    technicalNote = "Full invisibility active. Environmental shadow retention enabled."
                )
            }
            lower.contains("magic transformation") || lower.contains("transformation") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured 'MAGIC TRANSFORMATION': (1) Prismatic crystalline cocoon envelops character. (2) Fluid mesh warping between source and target keyframes. (3) Golden anamorphic flare climax.",
                    appliedMagicType = HollywoodMagicType.MAGIC_TRANSFORMATION,
                    technicalNote = "Transition curve set to Ease-In-Out Quintic over 2.8 seconds."
                )
            }
            lower.contains("hollywood action") || lower.contains("action") -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Configured 'HOLLYWOOD ACTION FINISH': (1) Master shutter ramped to 45 degrees for crisp combat punches. (2) 2.39:1 Scope letterbox enabled. (3) 35mm Kodak 5219 grain with crushed tungsten blacks. (4) Anamorphic teal & orange LUT engaged.",
                    appliedMagicType = HollywoodMagicType.SUPER_SPEED,
                    technicalNote = "Grading: Rec.709 -> DCI-P3 Color transform applied."
                )
            }
            else -> {
                AiDirectorMessage(
                    isFromUser = false,
                    text = "Understood cinematic command: '$prompt'. Interpreting scene geometry, detecting primary subjects, and creating a multi-layer composite node graph. Applied cinematic color halation and motion vectors.",
                    appliedMagicType = HollywoodMagicType.ENERGY,
                    technicalNote = "DIV EDIT AI Engine: Generated custom multi-track VFX overlay."
                )
            }
        }
    }

    // Watermark & Logo Intro/Outro
    fun updateWatermark(config: WatermarkConfig) {
        _watermarkConfig.value = config
        showStatus(if (config.isBurnIn) "Watermark configured: Burn-In Mode (Permanent Master)" else "Watermark configured: Non-Destructive Overlay")
    }

    fun updateLogoSequence(config: LogoSequenceConfig) {
        _logoSequenceConfig.value = config
        showStatus("Updated Cinematic Logo Intro/Outro sequence")
    }

    // Media Import
    fun importMediaAsset(asset: MediaAsset) {
        _mediaAssets.value = listOf(asset) + _mediaAssets.value
        // Add to active timeline track if appropriate
        val targetTrackType = when (asset.type) {
            AssetType.AUDIO_SCORE, AssetType.VOICE_OVER, AssetType.FOLEY_SFX -> TrackType.AUDIO_A1
            AssetType.VFX_ASSET -> TrackType.VFX
            AssetType.LOGO -> TrackType.TITLES
            else -> TrackType.VIDEO_V1
        }
        val targetTrack = _currentProject.value.tracks.find { it.type == targetTrackType }
            ?: _currentProject.value.tracks.first()

        val lastEnd = targetTrack.clips.maxOfOrNull { it.endMs } ?: 0L
        val newClip = TimelineClip(
            id = "clip_${System.currentTimeMillis()}",
            trackId = targetTrack.id,
            assetId = asset.id,
            title = asset.name,
            startMs = lastEnd,
            endMs = lastEnd + asset.durationMs.coerceAtMost(20_000L),
            clipColor = asset.colorTag
        )

        val updatedTracks = _currentProject.value.tracks.map { tr ->
            if (tr.id == targetTrack.id) tr.copy(clips = tr.clips + newClip) else tr
        }
        _currentProject.value = _currentProject.value.copy(tracks = updatedTracks)
        closeSheet()
        showStatus("Imported '${asset.name}' to ${targetTrack.name}")
    }

    // Export Master Rendering Simulator
    fun startExportMaster(codec: String, resolution: String, bitrate: String, burnInWatermark: Boolean) {
        _exportState.value = ExportProgressState(
            isExporting = true,
            progressPercent = 0,
            currentFrame = 0,
            totalFrames = 1440,
            currentPass = "Initializing Hardware ProRes / H.265 Cinema Pipeline...",
            exportCompleted = false,
            outputFileName = "${_currentProject.value.title.replace(" ", "_")}_${resolution.replace(" ", "")}_$codec.mov"
        )
        openSheet(ActiveStudioSheet.EXPORT_STUDIO)

        viewModelScope.launch {
            val passes = listOf(
                "Pass 1/4: Multi-Track Video & Audio De-interlacing & Conforming",
                "Pass 2/4: Executing Hollywood Magic GPU Particles & Alpha Compositing",
                "Pass 3/4: 35mm Film Grain Emulation & 3-Way DCI-P3 Color Grading",
                if (burnInWatermark) "Pass 4/4: Burning In Transparent Studio Watermark & SMPTE Timecode..." else "Pass 4/4: Multiplexing 96kHz Master Audio & Encapsulating QuickTime Stream"
            )

            val total = 1440
            var current = 0
            while (current < total) {
                delay(60L)
                current += 60
                val percent = ((current.toFloat() / total.toFloat()) * 100).toInt().coerceAtMost(100)
                val passIndex = (percent / 26).coerceIn(0, 3)
                _exportState.value = _exportState.value.copy(
                    progressPercent = percent,
                    currentFrame = current.coerceAtMost(total),
                    currentPass = passes[passIndex]
                )
            }

            _exportState.value = _exportState.value.copy(
                isExporting = false,
                exportCompleted = true,
                progressPercent = 100,
                currentPass = "Export Complete! Master ready for Theatrical Cinema / Festival Delivery."
            )
            showStatus("Cinema Master Export Completed: ${_exportState.value.outputFileName}")
        }
    }

    fun showStatus(message: String) {
        _systemStatusText.value = message
    }

    // USER SESSION & STORAGE MANAGEMENT
    fun loadUserSession(userId: String) {
        val storage = userStorageManager.getStorageForUser(userId)
        _myFilms.value = storage.myFilms
        _drafts.value = storage.drafts
        _exportedProjects.value = storage.exportedProjects
        _recentProjects.value = storage.recentProjects
        if (storage.myFilms.isNotEmpty()) {
            _currentProject.value = storage.myFilms.first()
        }
    }

    fun signInWithGoogle(activity: Activity) {
        viewModelScope.launch {
            showStatus("Authenticating securely with Google...")
            val result = authManager.signInWithGoogle(activity)
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                loadUserSession(user.userId)
                showStatus("Welcome back, ${user.displayName}! Film projects restored.")
            } else {
                showStatus("Google Sign-In: ${result.exceptionOrNull()?.message ?: "Cancelled"}")
            }
        }
    }

    fun signOut(activity: Activity? = null) {
        viewModelScope.launch {
            authManager.signOut(activity)
            // Clear protected user session from active UI
            _screen.value = StudioScreen.DASHBOARD
            _activeSheet.value = ActiveStudioSheet.NONE
            _selectedClipId.value = null
            _myFilms.value = emptyList()
            _drafts.value = emptyList()
            _exportedProjects.value = emptyList()
            showStatus("Signed out successfully. Protected film projects locked.")
        }
    }

    fun signInAs(email: String, name: String) {
        authManager.signInAs(email, name)
        val uid = "usr_" + email.replace("@", "_").replace(".", "_")
        loadUserSession(uid)
        showStatus("Signed in as $name ($email)")
    }

    // MEDIA LIBRARY MANAGEMENT (Requirement 45 & 49)
    fun setMediaSearchQuery(query: String) {
        _mediaSearchQuery.value = query
    }

    fun setMediaFolderFilter(folder: String?) {
        _selectedFolderCategory.value = folder
    }

    fun setMediaSortOrder(order: MediaSortOrder) {
        _mediaSortOrder.value = order
    }

    fun openMediaPreview(asset: MediaAsset) {
        _selectedMediaForPreview.value = asset
        openSheet(ActiveStudioSheet.MEDIA_PREVIEW_DIALOG)
    }

    fun closeMediaPreview() {
        _selectedMediaForPreview.value = null
        if (_activeSheet.value == ActiveStudioSheet.MEDIA_PREVIEW_DIALOG) {
            closeSheet()
        }
    }

    fun openMediaRename(asset: MediaAsset) {
        _selectedMediaForRename.value = asset
        openSheet(ActiveStudioSheet.RENAME_MEDIA_DIALOG)
    }

    fun closeMediaRename() {
        _selectedMediaForRename.value = null
        if (_activeSheet.value == ActiveStudioSheet.RENAME_MEDIA_DIALOG) {
            closeSheet()
        }
    }

    fun renameMediaAsset(assetId: String, newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isEmpty()) return
        _mediaAssets.value = _mediaAssets.value.map { asset ->
            if (asset.id == assetId) asset.copy(name = trimmed) else asset
        }
        closeMediaRename()
        showStatus("Media renamed to: $trimmed")
    }

    fun deleteMediaAsset(assetId: String) {
        val target = _mediaAssets.value.find { it.id == assetId }
        _mediaAssets.value = _mediaAssets.value.filter { it.id != assetId }
        showStatus("Removed '${target?.name ?: "asset"}' from project (original file preserved on disk)")
    }

    fun addAssetToTimeline(asset: MediaAsset, targetTrackId: String? = null) {
        val currentProj = _currentProject.value
        val playhead = _currentPlayheadMs.value
        val clipDuration = asset.durationMs.coerceAtLeast(4000L).coerceAtMost(30000L)

        // Find matching or target track
        val targetTrack = if (targetTrackId != null) {
            currentProj.tracks.find { it.id == targetTrackId }
        } else {
            when (asset.type) {
                AssetType.LIVE_ACTION -> currentProj.tracks.find { it.type == TrackType.VIDEO_V1 }
                AssetType.ANIMATION, AssetType.RENDERED_SCENE -> currentProj.tracks.find { it.type == TrackType.VIDEO_V2 || it.type == TrackType.VIDEO_V1 }
                AssetType.IMAGE_SEQUENCE, AssetType.STILL_IMAGE -> currentProj.tracks.find { it.type == TrackType.OVERLAY || it.type == TrackType.VIDEO_V1 }
                AssetType.VFX_ASSET -> currentProj.tracks.find { it.type == TrackType.VFX }
                AssetType.AUDIO_SCORE -> currentProj.tracks.find { it.type == TrackType.AUDIO_A2 }
                AssetType.FOLEY_SFX -> currentProj.tracks.find { it.type == TrackType.SFX_A3 }
                AssetType.VOICE_OVER -> currentProj.tracks.find { it.type == TrackType.AUDIO_A1 }
                AssetType.LOGO -> currentProj.tracks.find { it.type == TrackType.TITLES || it.type == TrackType.OVERLAY }
            }
        } ?: currentProj.tracks.firstOrNull()

        if (targetTrack == null) {
            showStatus("No suitable track found for ${asset.name}")
            return
        }

        val newClip = TimelineClip(
            id = "clip_" + UUID.randomUUID().toString().take(8),
            trackId = targetTrack.id,
            assetId = asset.id,
            title = asset.name,
            startMs = playhead,
            endMs = playhead + clipDuration,
            clipColor = asset.colorTag
        )

        val updatedTracks = currentProj.tracks.map { track ->
            if (track.id == targetTrack.id) {
                track.copy(clips = track.clips + newClip)
            } else {
                track
            }
        }

        val updatedProj = currentProj.copy(
            tracks = updatedTracks,
            durationMs = maxOf(currentProj.durationMs, playhead + clipDuration + 5000L)
        )

        saveUndoState()
        _currentProject.value = updatedProj
        _selectedClipId.value = newClip.id
        _selectedTrackId.value = targetTrack.id
        showStatus("Added '${asset.name}' to ${targetTrack.name} at ${formatTimecode(playhead)}")
    }

    fun openAccountMenu() {
        openSheet(ActiveStudioSheet.ACCOUNT_PROFILE_DIALOG)
    }

    fun openRenameDialog(asset: MediaAsset) {
        openMediaRename(asset)
    }

    fun openReplaceMediaDialog(clip: TimelineClip) {
        _selectedClipForReplace.value = clip
        openSheet(ActiveStudioSheet.MEDIA_IMPORT_DIALOG)
    }

    fun prepareReplaceClipAsset(clip: TimelineClip) {
        openReplaceMediaDialog(clip)
    }

    fun replaceSelectedClipWithMedia(newAsset: MediaAsset) {
        executeReplaceClipAsset(newAsset)
    }

    fun executeReplaceClipAsset(newAsset: MediaAsset) {
        val clip = _selectedClipForReplace.value ?: getSelectedClip() ?: return
        val currentProj = _currentProject.value
        val updatedTracks = currentProj.tracks.map { track ->
            if (track.id == clip.trackId) {
                track.copy(clips = track.clips.map { c ->
                    if (c.id == clip.id) {
                        c.copy(
                            assetId = newAsset.id,
                            title = newAsset.name,
                            clipColor = newAsset.colorTag
                        )
                    } else c
                })
            } else track
        }
        saveUndoState()
        _currentProject.value = currentProj.copy(tracks = updatedTracks)
        _selectedClipForReplace.value = null
        closeSheet()
        showStatus("Replaced clip media with '${newAsset.name}'")
    }

    fun importFromDevice(
        name: String,
        type: AssetType,
        extension: String,
        sizeBytes: Long = 1024L * 1024L * 85L,
        uriString: String? = null,
        folderCategory: String = "Footage"
    ) {
        val newAsset = MediaAsset(
            id = "asset_" + UUID.randomUUID().toString().take(8),
            name = name,
            type = type,
            durationMs = when (type) {
                AssetType.STILL_IMAGE, AssetType.LOGO -> 6_000L
                AssetType.FOLEY_SFX -> 3_500L
                AssetType.VFX_ASSET -> 8_000L
                else -> 18_000L
            },
            resolution = if (type == AssetType.STILL_IMAGE || type == AssetType.IMAGE_SEQUENCE) "4096 x 2160" else "4K UHD (Master)",
            fps = if (type == AssetType.AUDIO_SCORE || type == AssetType.FOLEY_SFX || type == AssetType.VOICE_OVER) 0 else 24,
            colorTag = when (type) {
                AssetType.LIVE_ACTION -> 0xFF2E86DE
                AssetType.ANIMATION, AssetType.RENDERED_SCENE -> 0xFFFECA57
                AssetType.IMAGE_SEQUENCE, AssetType.STILL_IMAGE -> 0xFFA55EEA
                AssetType.VFX_ASSET -> 0xFFFF3838
                AssetType.AUDIO_SCORE, AssetType.FOLEY_SFX, AssetType.VOICE_OVER -> 0xFF10AC84
                AssetType.LOGO -> 0xFFFFB834
            },
            description = "Imported from local storage: $name",
            uriString = uriString,
            fileExtension = extension.lowercase(),
            fileSizeBytes = sizeBytes,
            folderCategory = folderCategory,
            dateAddedMs = System.currentTimeMillis()
        )

        _mediaAssets.value = listOf(newAsset) + _mediaAssets.value
        showStatus("Imported '${newAsset.name}' into Media Library ($folderCategory)")
    }

    fun formatTimecode(timeMs: Long): String {
        val totalSeconds = timeMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        val frames = ((timeMs % 1000) * _currentProject.value.fps.fps / 1000).toInt()
        return String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, frames)
    }
}
