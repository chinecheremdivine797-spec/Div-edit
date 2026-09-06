package com.example.data

import com.example.model.*
import java.util.concurrent.ConcurrentHashMap

class UserStorageManager {

    // Isolated in-memory and local repository keyed strictly by userId
    private val userStorageMap = ConcurrentHashMap<String, UserProjectStorage>()

    init {
        // Initialize default authenticated user storage for divstudio03@gmail.com
        val defaultUserId = "usr_divstudio03"
        val defaultProject = SampleFilmRepository.createDefaultProject()
        val defaultRecent = SampleFilmRepository.sampleRecentProjects

        userStorageMap[defaultUserId] = UserProjectStorage(
            userId = defaultUserId,
            myFilms = defaultRecent,
            drafts = listOf(
                FilmProject(
                    id = "draft_tokyo_cyber",
                    title = "NEO SHIBUYA ANIME PASS [DRAFT]",
                    director = "DIV Studio",
                    aspectRatio = AspectRatioPreset.CINEMA_16_9,
                    fps = FpsOption.FPS_24,
                    colorSpace = ColorSpaceOption.DCI_P3,
                    durationMs = 45_000L,
                    thumbnailTag = "project_thumb_2"
                )
            ),
            recentProjects = defaultRecent,
            exportedProjects = listOf(
                FilmProject(
                    id = "exp_chronosphere_theatrical",
                    title = "THE SHADOW APPRENTICE - DCI 4K MASTER",
                    director = "DIV Studio",
                    aspectRatio = AspectRatioPreset.ANAMORPHIC_2_39,
                    fps = FpsOption.FPS_24,
                    colorSpace = ColorSpaceOption.DCI_P3,
                    durationMs = 65_000L,
                    thumbnailTag = "project_thumb_1"
                )
            ),
            savedFilmTricks = SampleFilmRepository.sampleFilmTricks.take(6)
        )
    }

    fun getStorageForUser(userId: String): UserProjectStorage {
        return userStorageMap.getOrPut(userId) {
            UserProjectStorage(
                userId = userId,
                myFilms = listOf(SampleFilmRepository.createDefaultProject()),
                drafts = emptyList(),
                recentProjects = listOf(SampleFilmRepository.createDefaultProject()),
                exportedProjects = emptyList(),
                savedFilmTricks = SampleFilmRepository.sampleFilmTricks.take(4)
            )
        }
    }

    fun saveProject(userId: String, project: FilmProject) {
        val current = getStorageForUser(userId)
        val updatedMyFilms = (listOf(project) + current.myFilms.filter { it.id != project.id })
        val updatedRecent = (listOf(project) + current.recentProjects.filter { it.id != project.id })
        userStorageMap[userId] = current.copy(
            myFilms = updatedMyFilms,
            recentProjects = updatedRecent
        )
    }

    fun saveDraft(userId: String, project: FilmProject) {
        val current = getStorageForUser(userId)
        val updatedDrafts = (listOf(project) + current.drafts.filter { it.id != project.id })
        userStorageMap[userId] = current.copy(drafts = updatedDrafts)
    }

    fun saveExportedProject(userId: String, project: FilmProject) {
        val current = getStorageForUser(userId)
        val updatedExported = (listOf(project) + current.exportedProjects.filter { it.id != project.id })
        userStorageMap[userId] = current.copy(exportedProjects = updatedExported)
    }
}
