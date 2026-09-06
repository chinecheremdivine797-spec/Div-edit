package com.example.data

import com.example.model.*

object SampleFilmRepository {

    val sampleMediaAssets: List<MediaAsset> = listOf(
        MediaAsset(
            id = "asset_live_1",
            name = "Hero_Alley_Arrival_4K_LogC.mov",
            type = AssetType.LIVE_ACTION,
            durationMs = 28_000L,
            resolution = "4096 x 1714 (2.39:1)",
            fps = 24,
            colorTag = 0xFF2E86DE,
            description = "Cinematic night rain tracking shot of protagonist walking into frame",
            fileExtension = "mov",
            folderCategory = "Live Footage",
            fileSizeBytes = 1024L * 1024L * 480L
        ),
        MediaAsset(
            id = "asset_live_2",
            name = "Roof_Combat_Dual_4K.mp4",
            type = AssetType.LIVE_ACTION,
            durationMs = 19_500L,
            resolution = "3840 x 2160",
            fps = 60,
            colorTag = 0xFF00D2D3,
            description = "High shutter martial arts sword duel under neon billboards",
            fileExtension = "mp4",
            folderCategory = "Live Footage",
            fileSizeBytes = 1024L * 1024L * 220L
        ),
        MediaAsset(
            id = "asset_live_3",
            name = "Highlander_Chase_Cinema.mkv",
            type = AssetType.LIVE_ACTION,
            durationMs = 34_000L,
            resolution = "4096 x 2160 (MKV Master)",
            fps = 24,
            colorTag = 0xFF54A0FF,
            description = "High dynamic range mountain car chase master plate",
            fileExtension = "mkv",
            folderCategory = "Live Footage",
            fileSizeBytes = 1024L * 1024L * 650L
        ),
        MediaAsset(
            id = "asset_live_4",
            name = "City_Timelapse_Night.webm",
            type = AssetType.LIVE_ACTION,
            durationMs = 15_000L,
            resolution = "3840 x 2160 (WebM VP9)",
            fps = 30,
            colorTag = 0xFF5F27CD,
            description = "Long-exposure highway light trails and skyline transit",
            fileExtension = "webm",
            folderCategory = "Live Footage",
            fileSizeBytes = 1024L * 1024L * 95L
        ),
        MediaAsset(
            id = "asset_anim_1",
            name = "Cyber_Dragon_Pass_Render.mov",
            type = AssetType.ANIMATION,
            durationMs = 14_000L,
            resolution = "3840 x 2160",
            fps = 24,
            colorTag = 0xFFFECA57,
            description = "Unreal Engine 5 volumetric smoke and cybernetic creature flight",
            fileExtension = "mov",
            folderCategory = "Animation & Anime",
            fileSizeBytes = 1024L * 1024L * 310L
        ),
        MediaAsset(
            id = "asset_anim_2",
            name = "Anime_Shonen_Powerup_Cut.mp4",
            type = AssetType.ANIMATION,
            durationMs = 9_200L,
            resolution = "1920 x 1080 (Hand-Drawn Anime)",
            fps = 24,
            colorTag = 0xFFFF6B6B,
            description = "Dynamic anime impact frame sequence on-twos with speedlines",
            fileExtension = "mp4",
            folderCategory = "Animation & Anime",
            fileSizeBytes = 1024L * 1024L * 85L
        ),
        MediaAsset(
            id = "asset_anim_scene_2",
            name = "Mecha_Hangar_Takeoff.exr",
            type = AssetType.RENDERED_SCENE,
            durationMs = 22_000L,
            resolution = "4096 x 2160 (3D Scene)",
            fps = 24,
            colorTag = 0xFFFF9F43,
            description = "Multi-pass 3D animated hangar launch sequence with depth buffers",
            fileExtension = "exr",
            folderCategory = "Animation & Anime",
            fileSizeBytes = 1024L * 1024L * 820L
        ),
        MediaAsset(
            id = "asset_seq_1",
            name = "Magic_Glyph_Sequence_%04d.png",
            type = AssetType.IMAGE_SEQUENCE,
            durationMs = 8_000L,
            resolution = "2048 x 2048 (PNG Sequence)",
            fps = 24,
            colorTag = 0xFFA55EEA,
            description = "Alpha transparent hand-animated occult alchemy ring sequence",
            fileExtension = "png",
            folderCategory = "Image Sequences & Plates",
            fileSizeBytes = 1024L * 1024L * 190L
        ),
        MediaAsset(
            id = "asset_still_1",
            name = "Matte_Painting_Cyber_Citadel.jpg",
            type = AssetType.STILL_IMAGE,
            durationMs = 10_000L,
            resolution = "7680 x 4320 (8K Matte)",
            fps = 0,
            colorTag = 0xFF48DBFB,
            description = "High-fidelity digital painted concept background backdrop",
            fileExtension = "jpg",
            folderCategory = "Image Sequences & Plates",
            fileSizeBytes = 1024L * 1024L * 42L
        ),
        MediaAsset(
            id = "asset_still_2",
            name = "Foreground_Silhouettes_Plate.webp",
            type = AssetType.STILL_IMAGE,
            durationMs = 8_000L,
            resolution = "3840 x 2160 (Lossless WebP)",
            fps = 0,
            colorTag = 0xFF1DD1A1,
            description = "Crisp foreground architecture frame with transparent cutouts",
            fileExtension = "webp",
            folderCategory = "Image Sequences & Plates",
            fileSizeBytes = 1024L * 1024L * 18L
        ),
        MediaAsset(
            id = "asset_vfx_1",
            name = "Volumetric_Explosion_Pyro_Alpha.mov",
            type = AssetType.VFX_ASSET,
            durationMs = 5_200L,
            resolution = "4K ProRes 4444",
            fps = 60,
            colorTag = 0xFFFF3838,
            description = "Hollywood detonation with pre-multiplied black channel and ember drift",
            fileExtension = "mov",
            folderCategory = "VFX Elements",
            fileSizeBytes = 1024L * 1024L * 340L
        ),
        MediaAsset(
            id = "asset_vfx_2",
            name = "Anamorphic_Light_Leak_Cyan.mov",
            type = AssetType.VFX_ASSET,
            durationMs = 12_000L,
            resolution = "4K UHD (Screen Blend)",
            fps = 24,
            colorTag = 0xFF00D2D3,
            description = "Organic vintage 1970s Kowa anamorphic glass horizontal streak",
            fileExtension = "mov",
            folderCategory = "VFX Elements",
            fileSizeBytes = 1024L * 1024L * 160L
        ),
        MediaAsset(
            id = "asset_audio_score",
            name = "Hans_Zimmer_Tribute_Score_96k.wav",
            type = AssetType.AUDIO_SCORE,
            durationMs = 120_000L,
            resolution = "24-bit / 96kHz Lossless WAV",
            fps = 0,
            colorTag = 0xFF10AC84,
            description = "Driving cinematic orchestral strings, deep French horns, and war drums",
            fileExtension = "wav",
            folderCategory = "Film Score & Foley",
            fileSizeBytes = 1024L * 1024L * 140L
        ),
        MediaAsset(
            id = "asset_audio_mp3",
            name = "Tokyo_Cyberpunk_Synthwave_Theme.mp3",
            type = AssetType.AUDIO_SCORE,
            durationMs = 95_000L,
            resolution = "320kbps Constant Stereo MP3",
            fps = 0,
            colorTag = 0xFF0ABDE3,
            description = "Analog synthesizer arpeggios with 808 sub bass grooves",
            fileExtension = "mp3",
            folderCategory = "Film Score & Foley",
            fileSizeBytes = 1024L * 1024L * 14L
        ),
        MediaAsset(
            id = "asset_foley_sfx",
            name = "Spatial_Teleport_SubImpact.wav",
            type = AssetType.FOLEY_SFX,
            durationMs = 3_400L,
            resolution = "Stereo 48kHz WAV",
            fps = 0,
            colorTag = 0xFF1DD1A1,
            description = "Binaural quantum displacement snap followed by 28Hz sub-bass thump",
            fileExtension = "wav",
            folderCategory = "Film Score & Foley",
            fileSizeBytes = 1024L * 1024L * 4L
        ),
        MediaAsset(
            id = "asset_voiceover",
            name = "Director_Commentary_Track.aac",
            type = AssetType.VOICE_OVER,
            durationMs = 45_000L,
            resolution = "256kbps Clean Studio AAC",
            fps = 0,
            colorTag = 0xFF54A0FF,
            description = "Clean spoken actor narrative monologue track",
            fileExtension = "aac",
            folderCategory = "Film Score & Foley",
            fileSizeBytes = 1024L * 1024L * 12L
        ),
        MediaAsset(
            id = "asset_logo_1",
            name = "DIV_Edit_Studio_Emblem.png",
            type = AssetType.LOGO,
            durationMs = 60_000L,
            resolution = "1024 x 1024 Alpha PNG",
            fps = 0,
            colorTag = 0xFFFFB834,
            description = "Official high-resolution transparent gold & cyan production watermark",
            fileExtension = "png",
            folderCategory = "Logos & Branding",
            fileSizeBytes = 1024L * 1024L * 3L
        )
    )

    val sampleFilmTricks: List<FilmTrickPreset> = listOf(
        FilmTrickPreset("ft_flash", "Cinematic Flash", "High-intensity white frame overexposure transition into scene", FilmTrickCategory.TRANSITIONS, 600L, 0.9f, isFavorite = true),
        FilmTrickPreset("ft_film_burn", "Film Burn", "Organic vintage 35mm celluloid burn with orange heat blooming", FilmTrickCategory.CINEMATIC, 1200L, 0.85f, isFavorite = true),
        FilmTrickPreset("ft_light_leak", "Light Leak", "Warm golden anamorphic lens flare bleeding across frame edges", FilmTrickCategory.CINEMATIC, 2400L, 0.75f, isFavorite = true),
        FilmTrickPreset("ft_film_grain", "Film Grain", "35mm Kodak 5219 organic photochem emulsion simulation", FilmTrickCategory.CINEMATIC, 5000L, 0.65f),
        FilmTrickPreset("ft_vignette", "Vignette", "Anamorphic optical falloff with subtle edge chromatic defocus", FilmTrickCategory.CINEMATIC, 5000L, 0.7f),
        FilmTrickPreset("ft_cam_shake", "Camera Shake", "Dynamic 6-axis handheld visceral kinetic camera impact", FilmTrickCategory.ACTION, 800L, 0.95f, isFavorite = true),
        FilmTrickPreset("ft_impact", "Impact Hit", "Sub-frame zoom slam synchronized with air shockwave distortion", FilmTrickCategory.ACTION, 500L, 1.0f, isFavorite = true),
        FilmTrickPreset("ft_speed_ramp", "Speed Ramp", "Hyper-velocity 8x whip into 0.2x ultra slow-motion action apex", FilmTrickCategory.ACTION, 3000L, 0.9f, isFavorite = true),
        FilmTrickPreset("ft_flashback", "Flashback", "Desaturated bleached tones, high contrast, and flickering shutter", FilmTrickCategory.DRAMA, 4500L, 0.8f),
        FilmTrickPreset("ft_dream", "Dream Sequence", "Luminous soft halation glow, chromatic dispersion, and slow drift", FilmTrickCategory.FANTASY, 4000L, 0.75f),
        FilmTrickPreset("ft_night", "Night Effect (Day-for-Night)", "Hollywood blue-shifted tungsten curves and crushed shadows", FilmTrickCategory.CINEMATIC, 5000L, 0.85f),
        FilmTrickPreset("ft_fog", "Cinematic Fog", "Volumetric low-hanging atmospheric mist catching backlights", FilmTrickCategory.SUPERNATURAL, 5000L, 0.7f),
        FilmTrickPreset("ft_particles", "Particles", "Floating golden anamorphic bokeh motes drifting in slow air", FilmTrickCategory.MAGIC, 4000L, 0.8f, isFavorite = true),
        FilmTrickPreset("ft_magic_reveal", "Magic Reveal", "Concentric radiant glyphs expanding to reveal hidden subject", FilmTrickCategory.MAGIC, 2200L, 0.9f, isFavorite = true),
        FilmTrickPreset("ft_magic_disappear", "Magic Disappear", "Subject disintegrates into swirling luminous embers", FilmTrickCategory.MAGIC, 1800L, 0.95f, isFavorite = true),
        FilmTrickPreset("ft_clone_trick", "Clone Trick", "Horizontal split matte with feather blending for identical actors", FilmTrickCategory.MAGIC, 6000L, 0.85f),
        FilmTrickPreset("ft_mirror_trick", "Mirror Trick", "Actor reflection moving with independent supernatural timing", FilmTrickCategory.HORROR, 3500L, 0.8f),
        FilmTrickPreset("ft_ghost_trail", "Ghost Trail", "Lagging temporal motion echoes with chromatic edge fringing", FilmTrickCategory.SUPERNATURAL, 2500L, 0.75f),
        FilmTrickPreset("ft_afterimage", "Afterimage", "Speedster RGB split trails following subject kinetic momentum", FilmTrickCategory.ACTION, 1500L, 0.85f),
        FilmTrickPreset("ft_freeze_trick", "Freeze-Frame Trick", "Action snaps to still, desaturates, and titles slam in", FilmTrickCategory.ACTION, 2000L, 0.9f),
        FilmTrickPreset("ft_trans_trick", "Transformation Trick", "Organic mesh displacement shifting skin texture into metal", FilmTrickCategory.SCI_FI, 2800L, 0.9f),
        FilmTrickPreset("ft_portal_trick", "Portal Trick", "Sparks circular boundary cutting cleanly into alternate plate", FilmTrickCategory.FANTASY, 3200L, 0.95f, isFavorite = true),
        FilmTrickPreset("ft_invisible_trick", "Invisible-Person Trick", "Refractive heat shimmer silhouette moving through room", FilmTrickCategory.SCI_FI, 4000L, 0.9f),
        FilmTrickPreset("ft_teleport_trick", "Teleport Trick", "Instant frame collapse with cyan capacitor flash and arrival", FilmTrickCategory.SCI_FI, 1000L, 1.0f, isFavorite = true)
    )

    fun createDefaultProject(): FilmProject {
        val trackV1 = TimelineTrack(
            id = "track_v1",
            type = TrackType.VIDEO_V1,
            name = "V1: Live Action Master",
            clips = listOf(
                TimelineClip(
                    id = "clip_hero_arrive",
                    trackId = "track_v1",
                    assetId = "asset_live_1",
                    title = "Hero_Alley_Arrival.mov",
                    startMs = 0L,
                    endMs = 18_000L,
                    clipColor = 0xFF2E86DE,
                    appliedFilmTrickName = "Film Burn Intro"
                ),
                TimelineClip(
                    id = "clip_combat_duel",
                    trackId = "track_v1",
                    assetId = "asset_live_2",
                    title = "Roof_Combat_Dual.mov",
                    startMs = 18_000L,
                    endMs = 36_000L,
                    clipColor = 0xFF00D2D3,
                    speed = 1.0f
                ),
                TimelineClip(
                    id = "clip_dragon_climax",
                    trackId = "track_v1",
                    assetId = "asset_anim_1",
                    title = "Cyber_Dragon_Pass.mov",
                    startMs = 36_000L,
                    endMs = 52_000L,
                    clipColor = 0xFFFECA57
                )
            )
        )

        val trackVfx = TimelineTrack(
            id = "track_vfx",
            type = TrackType.VFX,
            name = "FX: Hollywood Magic",
            clips = listOf(
                TimelineClip(
                    id = "clip_teleport_burst",
                    trackId = "track_vfx",
                    assetId = "asset_seq_1",
                    title = "TELEPORT Refraction & Arcs",
                    startMs = 16_500L,
                    endMs = 21_000L,
                    blendMode = BlendModeType.SCREEN,
                    clipColor = 0xFFA55EEA,
                    appliedMagicId = "TELEPORT",
                    appliedMagicName = "Hollywood Magic: Teleport"
                ),
                TimelineClip(
                    id = "clip_pyro_detonation",
                    trackId = "track_vfx",
                    assetId = "asset_vfx_1",
                    title = "EXPLOSION Pyro Pass",
                    startMs = 34_000L,
                    endMs = 40_000L,
                    blendMode = BlendModeType.ADD,
                    clipColor = 0xFFFF3838,
                    appliedMagicId = "EXPLOSION",
                    appliedMagicName = "Hollywood Magic: Explosion"
                )
            )
        )

        val trackAdj = TimelineTrack(
            id = "track_adj",
            type = TrackType.OVERLAY,
            name = "ADJ: 35mm Anamorphic Master",
            clips = listOf(
                TimelineClip(
                    id = "clip_anamorphic_grade",
                    trackId = "track_adj",
                    assetId = "asset_vfx_2",
                    title = "Kodak 5219 + Anamorphic Flare",
                    startMs = 0L,
                    endMs = 60_000L,
                    blendMode = BlendModeType.SCREEN,
                    opacity = 0.65f,
                    clipColor = 0xFFFF9F43
                )
            )
        )

        val trackTitles = TimelineTrack(
            id = "track_titles",
            type = TrackType.TITLES,
            name = "TXT: Main Title & Lower Thirds",
            clips = listOf(
                TimelineClip(
                    id = "clip_title_card",
                    trackId = "track_titles",
                    assetId = "asset_logo_1",
                    title = "TITLE: THE SHADOW APPRENTICE",
                    startMs = 2_000L,
                    endMs = 8_000L,
                    blendMode = BlendModeType.NORMAL,
                    opacity = 0.95f,
                    clipColor = 0xFFFFB834
                )
            )
        )

        val trackA1 = TimelineTrack(
            id = "track_a1",
            type = TrackType.AUDIO_A1,
            name = "A1: Dialog & ADR Sync",
            volume = 0.9f,
            clips = listOf(
                TimelineClip(
                    id = "clip_dialog_alley",
                    trackId = "track_a1",
                    assetId = "asset_voiceover",
                    title = "Narrator_Monologue_Clean.wav",
                    startMs = 1_000L,
                    endMs = 18_000L,
                    volume = 0.9f,
                    clipColor = 0xFF10AC84
                )
            )
        )

        val trackA2 = TimelineTrack(
            id = "track_a2",
            type = TrackType.AUDIO_A2,
            name = "A2: Orchestral Film Score",
            volume = 0.8f,
            clips = listOf(
                TimelineClip(
                    id = "clip_score_full",
                    trackId = "track_a2",
                    assetId = "asset_audio_score",
                    title = "Hans_Zimmer_Tribute_Score.wav",
                    startMs = 0L,
                    endMs = 60_000L,
                    volume = 0.75f,
                    fadeInMs = 1500L,
                    fadeOutMs = 2000L,
                    clipColor = 0xFF1DD1A1
                )
            )
        )

        val trackA3 = TimelineTrack(
            id = "track_a3",
            type = TrackType.SFX_A3,
            name = "A3: Cinematic Foley SFX",
            volume = 1.0f,
            clips = listOf(
                TimelineClip(
                    id = "clip_teleport_sub",
                    trackId = "track_a3",
                    assetId = "asset_foley_sfx",
                    title = "Teleport_Snap_SubDrop.wav",
                    startMs = 16_800L,
                    endMs = 20_200L,
                    volume = 1.0f,
                    clipColor = 0xFF00D2D3
                )
            )
        )

        return FilmProject(
            id = "proj_shadow_apprentice",
            title = "THE SHADOW APPRENTICE",
            director = "Elena Rostova",
            aspectRatio = AspectRatioPreset.ANAMORPHIC_2_39,
            fps = FpsOption.FPS_24,
            colorSpace = ColorSpaceOption.DCI_P3,
            durationMs = 65_000L,
            tracks = listOf(trackV1, trackVfx, trackAdj, trackTitles, trackA1, trackA2, trackA3),
            markers = listOf(
                FilmMarker("m1", 2000L, "Main Title In"),
                FilmMarker("m2", 16800L, "Teleport Magic Peak"),
                FilmMarker("m3", 34000L, "Explosion Cue"),
                FilmMarker("m4", 52000L, "Dragon Fade Out")
            ),
            thumbnailTag = "project_thumb_1"
        )
    }

    val sampleRecentProjects: List<FilmProject> = listOf(
        createDefaultProject(),
        FilmProject(
            id = "proj_cyber_horizon",
            title = "CYBER HORIZON 2088",
            director = "Kaelen Voss",
            aspectRatio = AspectRatioPreset.CINEMA_16_9,
            fps = FpsOption.FPS_60,
            colorSpace = ColorSpaceOption.ACES_CG,
            durationMs = 140_000L,
            thumbnailTag = "project_thumb_2"
        ),
        FilmProject(
            id = "proj_anima_spirit",
            title = "ANIMA: FLIGHT OF THE SPIRIT BIRD",
            director = "Sora Takahashi",
            aspectRatio = AspectRatioPreset.CINEMA_16_9,
            fps = FpsOption.FPS_24,
            colorSpace = ColorSpaceOption.REC_709,
            durationMs = 95_000L,
            thumbnailTag = "project_thumb_3"
        ),
        FilmProject(
            id = "proj_neon_noir",
            title = "OBSIDIAN ECHO: DIRECTORS CUT",
            director = "Marcus Sterling",
            aspectRatio = AspectRatioPreset.ANAMORPHIC_2_39,
            fps = FpsOption.FPS_24,
            colorSpace = ColorSpaceOption.LOG_C,
            durationMs = 210_000L,
            thumbnailTag = "project_thumb_4"
        )
    )
}
