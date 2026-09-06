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
  var showAdvancedProductionSuite by remember { mutableStateOf(false) }

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
      snackbarHostState.showSnackbar(systemStatus, duration = SnackbarDuration.Short)
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets.systemBars,
    snackbarHost = {
      SnackbarHost(hostState = snackbarHostState) { data ->
        Snackbar(snackbarData = data, containerColor = CinemaSlate900, contentColor = GoldCinema, shape = MaterialTheme.shapes.small)
      }
    }
  ) { innerPadding ->
    Box(Modifier.fillMaxSize().padding(innerPadding)) {
      when (screen) {
        StudioScreen.DASHBOARD -> MainDashboardScreen(
          currentUser = currentUser,
          recentProjects = recentProjects,
          onSelectProject = { viewModel.selectProject(it) },
          onOpenEditor = { viewModel.setScreen(StudioScreen.EDITOR) },
          onOpenSheet = { viewModel.openSheet(it) },
          onOpenAccountMenu = { viewModel.openAccountMenu() },
          onSignInWithGoogle = { coroutineScope.launch { viewModel.signInWithGoogle(activity) } }
        )
        StudioScreen.EDITOR -> FilmEditorScreen(
          viewModel = viewModel,
          onBackToDashboard = { viewModel.setScreen(StudioScreen.DASHBOARD) }
        )
      }

      // Global professional tools launcher: available from both Dashboard and Editor.
      FilledTonalButton(
        onClick = { showAdvancedProductionSuite = true },
        modifier = Modifier.align(androidx.compose.ui.Alignment.TopEnd).padding(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Text("PRO TOOLS", fontSize = 9.sp)
      }

      when (activeSheet) {
        ActiveStudioSheet.HOLLYWOOD_MAGIC -> HollywoodMagicDialog(
          processState = magicProcessState,
          onSelectMagic = { viewModel.startHollywoodMagicProcess(it) },
          onApplyToTimeline = { viewModel.applyMagicToTimeline() },
          onDismiss = { viewModel.closeSheet() }
        )
        ActiveStudioSheet.FILM_TRICKS -> FilmTricksSheet(
          presets = filmTricks,
          onApplyTrick = { viewModel.applyFilmTrick(it) },
          onToggleFavorite = { viewModel.toggleFilmTrickFavorite(it) },
          onSaveCustomTrick = { viewModel.addCustomFilmTrick(it) },
          onDismiss = { viewModel.closeSheet() }
        )
        ActiveStudioSheet.AI_VFX_DIRECTOR -> AiVfxDirectorSheet(
          messages = aiMessages,
          onSendPrompt = { viewModel.sendAiDirectorPrompt(it) },
          onDismiss = { viewModel.closeSheet() }
        )
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
        ActiveStudioSheet.WATERMARK_LOGO -> WatermarkLogoSheet(
          watermarkConfig = watermarkConfig,
          logoSequenceConfig = logoSeqConfig,
          onUpdateWatermark = { viewModel.updateWatermark(it) },
          onUpdateLogoSequence = { viewModel.updateLogoSequence(it) },
          onDismiss = { viewModel.closeSheet() }
        )
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
            onAddFoley = { viewModel.importMediaAsset(it) },
            onDismiss = { viewModel.closeSheet() }
          )
        }
        ActiveStudioSheet.ANIMATION_BUILDER -> AnimationBuilderSheet(onDismiss = { viewModel.closeSheet() })
        ActiveStudioSheet.EXPORT_STUDIO -> ExportDialog(
          exportState = exportState,
          defaultBurnIn = watermarkConfig.isBurnIn,
          onStartExport = { codec, res, br, burnIn -> viewModel.startExportMaster(codec, res, br, burnIn) },
          onDismiss = { viewModel.closeSheet() }
        )
        ActiveStudioSheet.NEW_FILM_DIALOG -> NewFilmDialog(
          onCreateFilm = { title, dir, aspect, fps, cs -> viewModel.createNewProject(title, dir, aspect, fps, cs) },
          onDismiss = { viewModel.closeSheet() }
        )
        ActiveStudioSheet.MEDIA_IMPORT_DIALOG -> MediaImportDialog(
          mediaAssets = mediaAssets,
          clipToReplace = selectedClipForReplace,
          onImportAssetToTimeline = { viewModel.importMediaAsset(it) },
          onExecuteReplaceClip = { viewModel.replaceSelectedClipWithMedia(it) },
          onPreviewAsset = { viewModel.openMediaPreview(it) },
          onRenameAsset = { viewModel.openRenameDialog(it) },
          onDeleteAsset = { viewModel.deleteMediaAsset(it) },
          onImportFromDeviceFile = { name, type, ext, size, uri, cat -> viewModel.importFromDevice(name, type, ext, size, uri, cat) },
          onDismiss = { viewModel.closeSheet() }
        )
        ActiveStudioSheet.MEDIA_PREVIEW_DIALOG -> MediaPreviewDialog(
          asset = selectedMediaForPreview,
          onDismiss = { viewModel.closeSheet() },
          onAddToTimeline = { viewModel.importMediaAsset(it) },
          onOpenRename = { viewModel.openRenameDialog(it) },
          onDeleteAsset = { viewModel.deleteMediaAsset(it) }
        )
        ActiveStudioSheet.RENAME_MEDIA_DIALOG -> RenameMediaDialog(
          asset = selectedMediaForRename,
          onDismiss = { viewModel.closeSheet() },
          onConfirmRename = { id, newName -> viewModel.renameMediaAsset(id, newName) }
        )
        ActiveStudioSheet.ACCOUNT_PROFILE_DIALOG -> AccountMenuDialog(
          currentUser = currentUser,
          myFilmsCount = myFilms.size,
          draftsCount = drafts.size,
          exportedCount = exportedProjects.size,
          onDismiss = { viewModel.closeSheet() },
          onSignInWithGoogle = { coroutineScope.launch { viewModel.signInWithGoogle(activity) } },
          onSignOut = { viewModel.signOut() },
          onNavigateToMyFilms = { viewModel.openSheet(ActiveStudioSheet.MY_FILMS_DIALOG) },
          onNavigateToRecentProjects = { viewModel.openSheet(ActiveStudioSheet.MY_FILMS_DIALOG) }
        )
        ActiveStudioSheet.MY_FILMS_DIALOG -> MyFilmsDialog(
          currentUser = currentUser,
          myFilms = myFilms,
          drafts = drafts,
          exportedProjects = exportedProjects,
          onDismiss = { viewModel.closeSheet() },
          onSelectProject = { viewModel.selectProject(it) },
          onNewFilm = { viewModel.openSheet(ActiveStudioSheet.NEW_FILM_DIALOG) }
        )
        ActiveStudioSheet.NONE -> Unit
      }

      if (showAdvancedProductionSuite) {
        AdvancedProductionToolsSheet(onDismiss = { showAdvancedProductionSuite = false })
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "DIV EDIT AI Studio: $name", modifier = modifier)
}
