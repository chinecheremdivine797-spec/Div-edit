package com.example.engine

/** Validates a timeline before expensive rendering starts. */
object RenderPlanValidator {
    data class Issue(val clipId: String?, val message: String)

    fun validate(plan: TimelineRenderPlan): List<Issue> = buildList {
        plan.orderedClips().forEach { clip ->
            if (clip.sourcePath.isBlank()) add(Issue(clip.id, "Missing source path"))
            if (clip.endMs <= clip.startMs) add(Issue(clip.id, "Clip duration must be greater than zero"))
            if (clip.speed <= 0f) add(Issue(clip.id, "Clip speed must be greater than zero"))
            if (clip.volume < 0f) add(Issue(clip.id, "Clip volume cannot be negative"))
        }
    }
}
