package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AssetType
import com.example.model.FilmProject
import com.example.ui.components.*
import com.example.ui.screens.FilmEditorScreen
import com.example.ui.screens.MainDashboardScreen
import com.example.ui.theme.CinemaSlate900
import com.example.ui.theme.GoldCinema
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ActiveStudioSheet
import com.example.viewmodel.FilmStudioViewModel
import com.example.viewmodel.StudioScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  private val viewModel: FilmStudioViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        DivEditAiApp(activity = this@MainActivity, viewModel = viewModel)
      }
    }
  }
}

@Composable
fun DivEditAiApp(
  activity: ComponentActivity,
  viewModel: FilmStudioViewModel
) {
  val coroutineScope = rememberCoroutineScope()

  val screen by viewModel.screen.collectAsState()
  val activeSheet by viewModel.activeSheet.collectAsState()
  val recentProjects by viewModel.recentProjects.collectAsState()
  val mediaAssets by viewModel.mediaAssets.collectAsState()
  val filmTricks by viewModel.filmTricks.collectAsState()
  val watermarkConfig by viewModel.watermarkConfig.collectAsState()
  val logoSeqConfig by viewModel.logoSequenceConfig.collectAsState()
  val magicProcessState by viewModel.magicProcessState.collectAsState()
  val aiMessages by viewModel.aiMessages.collectAsState()
  val exportState by viewModel.exportState.collectAsState()
  val systemStatus by viewModel.systemStatusText.collectAsState()

  val currentUser by viewModel.currentUser.collectAsState()
  val myFilms by viewModel.myFilms.collectAsState()
  val drafts by viewModel.drafts.collectAsState()
  val exportedProjects by viewModel.exportedProjects.collectAsState()
  val selectedMediaForPreview by viewModel.selectedMediaForPreview.collectAsState()
  val selectedMediaForRename by viewModel.selectedMediaForRename.collectAsState()
  val selectedClipForReplace by viewModel.selectedClipForReplace.collectAsState()

  val selectedClip = viewModel.getSelectedClip()

  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(systemStatus) {
    if (systemStatus.isNotBlank() && !systemStatus.contains("Ready")) {
      snackbarHostState.showSnackbar(
        message = systemStatus,
        duration = SnackbarDuration.Short
      )
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets.systemBars,
    snackbarHost = {
      SnackbarHost(hostState = snackbarHostState) { data ->
        Snackbar(
          snackbarData = data,
          containerColor = CinemaSlate900,
          contentColor = GoldCinema,
          shape = MaterialTheme.shapes.small
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (screen) {
        StudioScreen.DASHBOARD -> {
          MainDashboardScreen(
            currentUser = currentUser,
            recentProjects = recentProjects,
            onSelectProject = { proj -> viewModel.selectProject(proj) },
            onOpenEditor = { viewModel.setScreen(StudioScreen.EDITOR) },
            onOpenSheet = { sheet -> viewModel.openSheet(sheet) },
            onOpenAccountMenu = { viewModel.openAccountMenu() },
            onSignInWithGoogle = {
              coroutineScope.launch {
                viewModel.signInWithGoogle(activity)
              }
            }
          )
        }
        StudioScreen.EDITOR -> {
          FilmEditorScreen(
            viewModel = viewModel,
            onBackToDashboard = { viewModel.setScreen(StudioScreen.DASHBOARD) }
          )
        }
      }

      // Dialogs & Sheets based on activeSheet state
      when (activeSheet) {
        ActiveStudioSheet.HOLLYWOOD_MAGIC -> {
          HollywoodMagicDialog(
            processState = magicProcessState,
            onSelectMagic = { magic -> viewModel.startHollywoodMagicProcess(magic) },
            onApplyToTimeline = { viewModel.applyMagicToTimeline() },
            onDismiss = { viewModel.closeSheet() }
          )
        }
        ActiveStudioSheet.FILM_TRICKS -> {
          FilmTricksSheet(
            presets = filmTricks,
            onApplyTrick = { trick -> viewModel.applyFilmTrick(trick) },
            onToggleFavorite = { id -> viewModel.toggleFilmTrickFavorite(id) },
            onSaveCustomTrick = { custom -> viewModel.addCustomFilmTrick(custom) },
            onDismiss = { viewModel.closeSheet() }
          )
        }
        ActiveStudioSheet.AI_VFX_DIRECTOR -> {
          AiVfxDirectorSheet(
            messages = aiMessages,
            onSendPrompt = { prompt -> viewModel.sendAiDirectorPrompt(prompt) },
            onDismiss = { viewModel.closeSheet() }
          )
        }
        ActiveStudioSheet.AI_TOOLS_STUDIO -> {
          val selectedAiTool by viewModel.selectedAiTool.collectAsState()
          val executionState by viewModel.aiToolExecutionState.collectAsState()
          val paramValues by viewModel.aiToolParamValues.collectAsState()

          AiToolsStudioSheet(
            tools = viewModel.aiTools,
            selectedTool = selectedAiTool,
            executionState = executionState,
            parameterValues = paramValues,
            onSelectTool = { viewModel.selectAiTool(it) },
            onUpdateParam = { id, value -> viewModel.updateAiToolParam(id, value) },
            onRunProcessing = { viewModel.startAiToolProcess(it) },
            onApplyToTimeline = { viewModel.applyAiToolToTimeline(it) },
            onDismiss = { viewModel.closeSheet() }
          )
        }
        ActiveStudioSheet.WATERMARK_LOGO -> {
          WatermarkLogoSheet(
            watermarkConfig = watermarkConfig,
            logoSequenceConfig = logoSeqConfig,
            onUpdateWatermark = { viewModel.updateWatermark(it) },
            onUpdateLogoSequence = { viewModel.updateLogoSequence(it) },
            onDismiss = { viewModel.closeSheet() }
          )
        }
        ActiveStudioSheet.COLOR_STUDIO -> {
          selectedClip?.let { clip ->
            ColorStudioSheet(
              currentColorGrade = clip.colorGrade,
              onUpdateColorGrade = { viewModel.updateSelectedClipColorGrade(it) },
              onDismiss = { viewModel.closeSheet() }
            )
          } ?: run {
            viewModel.closeSheet()
            viewModel.showStatus("Select a clip on the timeline first to color grade")
          }
        }
        ActiveStudioSheet.AUDIO_STUDIO -> {
          val foleyAssets = mediaAssets.filter { it.type == AssetType.FOLEY_SFX || it.type == AssetType.AUDIO_SCORE }
          AudioStudioSheet(
            tracks = viewModel.currentProject.value.tracks,
            foleyAssets = foleyAssets,
            onAddFoley = { asset -> viewModel.importMediaAsset(asset) },
            onDismiss = { viewModel.closeSheet() }
          )
        }
        ActiveStudioSheet.ANIMATION_BUILDER -> {
          AnimationBuilderSheet(
            onDismiss = { viewModel.closeSheet() }
          )
        }
        ActiveStudioSheet.EXPORT_STUDIO -> {
          ExportDialog(
            exportState = exportState,
            defaultBurnIn = watermarkConfig.isBurnIn,
            onStartExport = { codec, res, br, burnIn ->
              viewModel.startExportMaster(codec, res, br, burnIn)
            },
            onDismiss = { viewModel.closeSheet() }
          )
        }
        ActiveStudioSheet.NEW_FILM_DIALOG -> {
          NewFilmDialog(
            onCreateFilm = { title, dir, aspect, fps, cs ->
              viewModel.createNewProject(title, dir, aspect, fps, cs)
            },
            onDismiss = { viewModel.closeSheet() }
          )
        }
        ActiveStudioSheet.MEDIA_IMPORT_DIALOG -> {
          MediaImportDialog(
            mediaAssets = mediaAssets,
            clipToReplace = selectedClipForReplace,
            onImportAssetToTimeline = { asset -> viewModel.importMediaAsset(asset) },
            onExecuteReplaceClip = { asset -> viewModel.replaceSelectedClipWithMedia(asset) },
            onPreviewAsset = { asset -> viewModel.openMediaPreview(asset) },
            onRenameAsset = { asset -> viewModel.openRenameDialog(asset) },
            onDeleteAsset = { assetId -> viewModel.deleteMediaAsset(assetId) },
            onImportFromDeviceFile = { name, type, ext, size, uri, cat ->
              viewModel.importFromDevice(name, type, ext, size, uri, cat)
            },
            onDismiss = { viewModel.closeSheet() }
          )
        }
        ActiveStudioSheet.MEDIA_PREVIEW_DIALOG -> {
          MediaPreviewDialog(
            asset = selectedMediaForPreview,
            onDismiss = { viewModel.closeSheet() },
            onAddToTimeline = { asset -> viewModel.importMediaAsset(asset) },
            onOpenRename = { asset -> viewModel.openRenameDialog(asset) },
            onDeleteAsset = { assetId -> viewModel.deleteMediaAsset(assetId) }
          )
        }
        ActiveStudioSheet.RENAME_MEDIA_DIALOG -> {
          RenameMediaDialog(
            asset = selectedMediaForRename,
            onDismiss = { viewModel.closeSheet() },
            onConfirmRename = { id, newName -> viewModel.renameMediaAsset(id, newName) }
          )
        }
        ActiveStudioSheet.ACCOUNT_PROFILE_DIALOG -> {
          AccountMenuDialog(
            currentUser = currentUser,
            myFilmsCount = myFilms.size,
            draftsCount = drafts.size,
            exportedCount = exportedProjects.size,
            onDismiss = { viewModel.closeSheet() },
            onSignInWithGoogle = {
              coroutineScope.launch {
                viewModel.signInWithGoogle(activity)
              }
            },
            onSignOut = { viewModel.signOut() },
            onNavigateToMyFilms = { viewModel.openSheet(ActiveStudioSheet.MY_FILMS_DIALOG) },
            onNavigateToRecentProjects = { viewModel.openSheet(ActiveStudioSheet.MY_FILMS_DIALOG) }
          )
        }
        ActiveStudioSheet.MY_FILMS_DIALOG -> {
          MyFilmsDialog(
            currentUser = currentUser,
            myFilms = myFilms,
            drafts = drafts,
            exportedProjects = exportedProjects,
            onDismiss = { viewModel.closeSheet() },
            onSelectProject = { project -> viewModel.selectProject(project) },
            onNewFilm = { viewModel.openSheet(ActiveStudioSheet.NEW_FILM_DIALOG) }
          )
        }
        ActiveStudioSheet.NONE -> {
          // No modal active
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "DIV EDIT AI Studio: $name", modifier = modifier)
}
