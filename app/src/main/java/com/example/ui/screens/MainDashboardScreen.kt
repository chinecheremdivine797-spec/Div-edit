package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FilmProject
import com.example.model.UserAccount
import com.example.ui.components.AccountButton
import com.example.ui.theme.*
import com.example.viewmodel.ActiveStudioSheet

data class StudioQuickAction(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val testTag: String,
    val onClick: () -> Unit
)

@Composable
fun MainDashboardScreen(
    currentUser: UserAccount?,
    recentProjects: List<FilmProject>,
    onSelectProject: (FilmProject) -> Unit,
    onOpenEditor: () -> Unit,
    onOpenSheet: (ActiveStudioSheet) -> Unit,
    onOpenAccountMenu: () -> Unit,
    onSignInWithGoogle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quickActions = listOf(
        StudioQuickAction(
            title = "Import Media",
            subtitle = "Unified Cinema Picker",
            icon = Icons.Default.FileOpen,
            color = AnamorphicCyan,
            testTag = "action_import_media",
            onClick = { onOpenSheet(ActiveStudioSheet.MEDIA_IMPORT_DIALOG) }
        ),
        StudioQuickAction(
            title = "New Film",
            subtitle = "2.39:1 / 4K Master",
            icon = Icons.Default.MovieCreation,
            color = GoldCinema,
            testTag = "action_new_film",
            onClick = { onOpenSheet(ActiveStudioSheet.NEW_FILM_DIALOG) }
        ),
        StudioQuickAction(
            title = "My Films & Vault",
            subtitle = "Encrypted Cloud Projects",
            icon = Icons.Default.FolderSpecial,
            color = GoldCinema,
            testTag = "action_my_films",
            onClick = { onOpenSheet(ActiveStudioSheet.MY_FILMS_DIALOG) }
        ),
        StudioQuickAction(
            title = "Film Editor",
            subtitle = "Multi-Track Timeline",
            icon = Icons.Default.Timeline,
            color = AnamorphicCyan,
            testTag = "action_open_editor",
            onClick = onOpenEditor
        ),
        StudioQuickAction(
            title = "AI Cinema Suite",
            subtitle = "20 Neural Pro Engines",
            icon = Icons.Default.AutoAwesome,
            color = AnamorphicCyan,
            testTag = "action_ai_cinema_suite",
            onClick = { onOpenSheet(ActiveStudioSheet.AI_TOOLS_STUDIO) }
        ),
        StudioQuickAction(
            title = "Hollywood Magic",
            subtitle = "26 One-Click VFX",
            icon = Icons.Default.MovieFilter,
            color = MagicViolet,
            testTag = "action_hollywood_magic",
            onClick = { onOpenSheet(ActiveStudioSheet.HOLLYWOOD_MAGIC) }
        ),
        StudioQuickAction(
            title = "Film Tricks",
            subtitle = "Reusable Techniques",
            icon = Icons.Default.ContentCut,
            color = TrickOrange,
            testTag = "action_film_tricks",
            onClick = { onOpenSheet(ActiveStudioSheet.FILM_TRICKS) }
        ),
        StudioQuickAction(
            title = "AI VFX Director",
            subtitle = "Natural Language Engine",
            icon = Icons.Default.Psychology,
            color = GoldCinema,
            testTag = "action_ai_director",
            onClick = { onOpenSheet(ActiveStudioSheet.AI_VFX_DIRECTOR) }
        ),
        StudioQuickAction(
            title = "Watermark & Logo",
            subtitle = "Non-Destructive & Burn-In",
            icon = Icons.Default.Verified,
            color = GoldCinema,
            testTag = "action_watermark_logo",
            onClick = { onOpenSheet(ActiveStudioSheet.WATERMARK_LOGO) }
        ),
        StudioQuickAction(
            title = "Color Studio",
            subtitle = "3-Way Wheels & LUTs",
            icon = Icons.Default.Palette,
            color = GoldCinema,
            testTag = "action_color_studio",
            onClick = { onOpenSheet(ActiveStudioSheet.COLOR_STUDIO) }
        ),
        StudioQuickAction(
            title = "Audio Studio",
            subtitle = "Multitrack Mixer & Foley",
            icon = Icons.Default.GraphicEq,
            color = AudioEmerald,
            testTag = "action_audio_studio",
            onClick = { onOpenSheet(ActiveStudioSheet.AUDIO_STUDIO) }
        ),
        StudioQuickAction(
            title = "Animation Post",
            subtitle = "Anime & 2D/3D Timing",
            icon = Icons.Default.Brush,
            color = AnimationAmber,
            testTag = "action_animation_builder",
            onClick = { onOpenSheet(ActiveStudioSheet.ANIMATION_BUILDER) }
        ),
        StudioQuickAction(
            title = "Export Master",
            subtitle = "ProRes / 4K Theatrical",
            icon = Icons.Default.CloudDownload,
            color = RecordRed,
            testTag = "action_export_studio",
            onClick = { onOpenSheet(ActiveStudioSheet.EXPORT_STUDIO) }
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaSlate950)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top App Bar / Header with Google Sign-In & Brand
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(GoldCinemaGlow, CircleShape)
                        .border(1.dp, GoldCinema, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MovieFilter,
                        contentDescription = null,
                        tint = GoldCinema,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "DIV EDIT AI",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = CinemaSlate50,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Turn Footage Into Cinema.",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldCinema,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Google Account Button
            AccountButton(
                currentUser = currentUser,
                onOpenAccountMenu = onOpenAccountMenu,
                onSignInClicked = onSignInWithGoogle
            )
        }

        // Studio Hero Banner with Prominent IMPORT MEDIA & NEW FILM Buttons
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = CinemaSlate900,
            border = BorderStroke(1.5.dp, GoldCinema.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("hero_studio_banner")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                CinemaSlate950,
                                CinemaSlate900,
                                Color(0xFF16162A)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NEXT-GENERATION FILMMAKING STUDIO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldCinema,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        // Studio Master Status
                        Surface(
                            color = CinemaSlate950,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, AnamorphicCyanDark)
                        ) {
                            Text(
                                text = "STUDIO PRO // 4K 24FPS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AnamorphicCyan,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "FILM EDITING + VFX + HOLLYWOOD MAGIC + FILM TRICKS + ANIMATION POST-PRODUCTION",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = CinemaSlate50,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Professional cinematic workstation designed for filmmakers, directors, animators, and VFX artists. Import your live-action cuts, image sequences, or anime renders to craft Hollywood-grade scenes.",
                        fontSize = 11.sp,
                        color = CinemaSlate400,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Prominent Primary Action Buttons (Requirement 45 & 49)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Clear and Prominent IMPORT MEDIA Button (Requirement 45)
                        Button(
                            onClick = { onOpenSheet(ActiveStudioSheet.MEDIA_IMPORT_DIALOG) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AnamorphicCyan,
                                contentColor = CinemaSlate950
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                            modifier = Modifier.testTag("hero_import_media_button")
                        ) {
                            Icon(Icons.Default.FileOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "IMPORT MEDIA",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                letterSpacing = 0.8.sp
                            )
                        }

                        // NEW FILM Button
                        Button(
                            onClick = { onOpenSheet(ActiveStudioSheet.NEW_FILM_DIALOG) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldCinema,
                                contentColor = CinemaSlate950
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                            modifier = Modifier.testTag("hero_new_film_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "NEW FILM",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                letterSpacing = 0.8.sp
                            )
                        }

                        // Prominent IMPORT VIDEO Button (Requirement 49)
                        OutlinedButton(
                            onClick = { onOpenSheet(ActiveStudioSheet.MEDIA_IMPORT_DIALOG) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CinemaSlate50),
                            border = BorderStroke(1.dp, AnamorphicCyan.copy(alpha = 0.7f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                            modifier = Modifier.testTag("hero_import_video_button")
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = AnamorphicCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("IMPORT VIDEO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Prominent IMPORT IMAGE Button (Requirement 49)
                        OutlinedButton(
                            onClick = { onOpenSheet(ActiveStudioSheet.MEDIA_IMPORT_DIALOG) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CinemaSlate50),
                            border = BorderStroke(1.dp, CinemaSlate700),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                            modifier = Modifier.testTag("hero_import_image_button")
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = CinemaSlate200, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("IMPORT IMAGE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // 20+ AI Cinematic Tools Button
                        Button(
                            onClick = { onOpenSheet(ActiveStudioSheet.AI_TOOLS_STUDIO) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MagicViolet,
                                contentColor = CinemaSlate50
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                            modifier = Modifier.testTag("hero_ai_studio_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = CinemaSlate50)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("20+ AI ENGINES", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // Quick Actions Grid
        Text(
            text = "STUDIO DEPARTMENTS & WORKFLOWS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = GoldCinema,
            fontFamily = FontFamily.Monospace
        )

        // Custom 2-column action card grid
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            quickActions.chunked(2).forEach { rowActions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowActions.forEach { action ->
                        Surface(
                            onClick = action.onClick,
                            shape = RoundedCornerShape(8.dp),
                            color = CinemaSlate900,
                            border = BorderStroke(1.dp, CinemaSlate700),
                            modifier = Modifier
                                .weight(1f)
                                .testTag(action.testTag)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(action.color.copy(alpha = 0.15f), CircleShape)
                                        .border(1.dp, action.color.copy(alpha = 0.5f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = action.icon,
                                        contentDescription = null,
                                        tint = action.color,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = action.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CinemaSlate50
                                    )
                                    Text(
                                        text = action.subtitle,
                                        fontSize = 9.sp,
                                        color = CinemaSlate400,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                    if (rowActions.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // 20+ AI Cinematic Engines Feature Showcase
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = CinemaSlate900,
            border = BorderStroke(1.dp, AnamorphicCyan.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dashboard_ai_showcase_card")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AnamorphicCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "20 SPECIALIZED AI CINEMA ENGINES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = CinemaSlate50,
                            letterSpacing = 0.5.sp
                        )
                    }
                    TextButton(
                        onClick = { onOpenSheet(ActiveStudioSheet.AI_TOOLS_STUDIO) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("btn_view_all_ai_tools")
                    ) {
                        Text("EXPLORE ALL 20 →", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AnamorphicCyan)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Hollywood-grade neural computing: 3D Face Relighting, Studio ADR Voice Cloning, Sky Engine, Sub-pixel Hair Rotoscoping, 960fps Optical Flow & 8K Mastering.",
                    fontSize = 10.sp,
                    color = CinemaSlate300,
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Horizontal scrollable chip highlights of the 20 tools
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val highlightTools = listOf(
                        "#01 Face Relight" to GoldCinema,
                        "#02 Voice Clone ADR" to AnamorphicCyan,
                        "#03 Sky Replacement" to MagicViolet,
                        "#04 Hair Rotoscope" to TrickOrange,
                        "#07 3D Bokeh" to Color(0xFF45AAF2),
                        "#08 Beat Sync" to RecordRed,
                        "#14 8K Upscaler" to AudioEmerald,
                        "#16 Optical Slow-Mo" to AnimationAmber
                    )
                    highlightTools.forEach { (name, color) ->
                        Surface(
                            onClick = { onOpenSheet(ActiveStudioSheet.AI_TOOLS_STUDIO) },
                            color = CinemaSlate950,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(0.5.dp, color.copy(alpha = 0.7f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(name, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CinemaSlate200, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }

        // Recent Film Masters Carousel / Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECENT FILM PRODUCTIONS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CinemaSlate200,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "${recentProjects.size} Films",
                fontSize = 10.sp,
                color = CinemaSlate400,
                fontFamily = FontFamily.Monospace
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            recentProjects.forEach { project ->
                Surface(
                    onClick = { onSelectProject(project) },
                    shape = RoundedCornerShape(8.dp),
                    color = CinemaSlate900,
                    border = BorderStroke(1.dp, CinemaSlate700),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recent_film_${project.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(CinemaSlate800, RoundedCornerShape(6.dp))
                                    .border(1.dp, GoldCinemaDark, RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Movie,
                                    contentDescription = null,
                                    tint = GoldCinema,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = project.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CinemaSlate50
                                )
                                Text(
                                    text = "Dir: ${project.director} // ${project.aspectRatio.label} // ${project.fps.fps} FPS",
                                    fontSize = 10.sp,
                                    color = CinemaSlate400,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Button(
                            onClick = { onSelectProject(project) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CinemaSlate800,
                                contentColor = GoldCinema
                            ),
                            border = BorderStroke(1.dp, GoldCinemaDark),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Open Film", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
