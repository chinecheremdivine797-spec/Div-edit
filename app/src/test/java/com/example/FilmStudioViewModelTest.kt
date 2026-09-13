package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.model.*
import com.example.viewmodel.ActiveStudioSheet
import com.example.viewmodel.FilmStudioViewModel
import com.example.viewmodel.StudioScreen
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class FilmStudioViewModelTest {

    private lateinit var viewModel: FilmStudioViewModel

    @Before
    fun setup() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        viewModel = FilmStudioViewModel(application)
    }

    @Test fun testInitialProjectLoaded() {
        val project = viewModel.currentProject.value
        assertNotNull(project)
        assertEquals("THE SHADOW APPRENTICE", project.title)
        assertTrue(project.tracks.isNotEmpty())
        assertEquals(AspectRatioPreset.ANAMORPHIC_2_39, project.aspectRatio)
    }

    @Test fun testCreateNewProject() {
        viewModel.createNewProject("NEO TOKYO ODYSSEY", "Director K", AspectRatioPreset.CINEMA_16_9, FpsOption.FPS_24, ColorSpaceOption.DCI_P3)
        val project = viewModel.currentProject.value
        assertEquals("NEO TOKYO ODYSSEY", project.title)
        assertEquals("Director K", project.director)
        assertEquals(AspectRatioPreset.CINEMA_16_9, project.aspectRatio)
        assertEquals(StudioScreen.EDITOR, viewModel.screen.value)
    }

    @Test fun testPlayheadAndMarkPoints() {
        viewModel.seekTo(12000L); assertEquals(12000L, viewModel.currentPlayheadMs.value)
        viewModel.setInPoint(); assertEquals(12000L, viewModel.inPointMs.value)
        viewModel.seekTo(24000L); viewModel.setOutPoint(); assertEquals(24000L, viewModel.outPointMs.value)
        viewModel.clearInOutPoints(); assertNull(viewModel.inPointMs.value); assertNull(viewModel.outPointMs.value)
    }

    @Test fun testHollywoodMagicProcessAndApply() {
        viewModel.startHollywoodMagicProcess(HollywoodMagicType.DISAPPEAR)
        assertEquals(ActiveStudioSheet.HOLLYWOOD_MAGIC, viewModel.activeSheet.value)
        assertEquals(HollywoodMagicType.DISAPPEAR, viewModel.magicProcessState.value.magicType)
        assertEquals(9, viewModel.magicProcessState.value.steps.size)
    }

    @Test fun testApplyFilmTrick() {
        val preset = viewModel.filmTricks.value.first()
        viewModel.applyFilmTrick(preset)
        viewModel.getSelectedClip()?.let { assertEquals(preset.name, it.appliedFilmTrickName) }
    }

    @Test fun testWatermarkConfiguration() {
        viewModel.updateWatermark(
            WatermarkConfig(
                isEnabled = true,
                type = WatermarkType.BRAND_TEXT,
                textContent = "CONFIDENTIAL FESTIVAL CUT",
                isBurnIn = true
            )
        )
        assertTrue(viewModel.watermarkConfig.value.isEnabled)
        assertTrue(viewModel.watermarkConfig.value.isBurnIn)
        assertEquals("CONFIDENTIAL FESTIVAL CUT", viewModel.watermarkConfig.value.textContent)
    }

    @Test fun testAiDirectorPrompt() {
        viewModel.sendAiDirectorPrompt("Make this character disappear.")
        assertTrue(viewModel.aiMessages.value.any { it.text == "Make this character disappear." })
    }

    @Test fun testSplitClipAtPlayhead() {
        val clip = viewModel.getSelectedClip()
        if (clip != null) {
            viewModel.seekTo((clip.startMs + clip.endMs) / 2); viewModel.splitClipAtPlayhead()
            val track = viewModel.currentProject.value.tracks.find { it.id == clip.trackId }
            assertNotNull(track); assertTrue(track!!.clips.size >= 2)
        }
    }

    @Test fun testTimecodeFormatting() {
        assertTrue(viewModel.formatTimecode(3661000L).startsWith("01:01:01"))
    }
}
