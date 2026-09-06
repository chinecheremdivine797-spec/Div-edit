package com.example.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CustomFilmTrick
import com.example.model.FilmTrickCategory
import com.example.model.FilmTrickPreset
import com.example.ui.theme.*

@Composable
fun FilmTricksSheet(
    presets: List<FilmTrickPreset>,
    onApplyTrick: (FilmTrickPreset) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onSaveCustomTrick: (CustomFilmTrick) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FilmTrickCategory.ALL) }
    var showOnlyFavorites by remember { mutableStateOf(false) }
    var showCustomBuilder by remember { mutableStateOf(false) }

    val filteredPresets = remember(presets, searchQuery, selectedCategory, showOnlyFavorites) {
        presets.filter { preset ->
            val matchesCategory = (selectedCategory == FilmTrickCategory.ALL || preset.category == selectedCategory)
            val matchesQuery = searchQuery.isBlank() || preset.name.contains(searchQuery, ignoreCase = true) || preset.description.contains(searchQuery, ignoreCase = true)
            val matchesFav = !showOnlyFavorites || preset.isFavorite
            matchesCategory && matchesQuery && matchesFav
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("film_tricks_dialog"),
            shape = RoundedCornerShape(12.dp),
            color = CinemaSlate950,
            border = BorderStroke(1.5.dp, TrickOrange)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CinemaSlate900)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(TrickOrange.copy(alpha = 0.2f), CircleShape)
                                .border(1.dp, TrickOrange, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MovieFilter,
                                contentDescription = null,
                                tint = TrickOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "FILM TRICKS STUDIO",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = CinemaSlate50
                            )
                            Text(
                                text = "Fast Reusable Cinematic Techniques & Presets",
                                fontSize = 11.sp,
                                color = TrickOrange
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { showCustomBuilder = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldCinema,
                                contentColor = CinemaSlate950
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_create_custom_trick")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Create Custom Trick", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = CinemaSlate400)
                        }
                    }
                }

                // Search Bar and Favorites Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search Film Tricks (e.g. flash, speed ramp, portal, clone)...", fontSize = 11.sp, color = CinemaSlate400) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CinemaSlate400) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("search_film_tricks"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CinemaSlate900,
                            unfocusedContainerColor = CinemaSlate900,
                            focusedBorderColor = TrickOrange,
                            unfocusedBorderColor = CinemaSlate700,
                            focusedTextColor = CinemaSlate50,
                            unfocusedTextColor = CinemaSlate50
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { showOnlyFavorites = !showOnlyFavorites },
                        modifier = Modifier
                            .size(44.dp)
                            .background(if (showOnlyFavorites) GoldCinema.copy(alpha = 0.2f) else CinemaSlate900, RoundedCornerShape(8.dp))
                            .border(1.dp, if (showOnlyFavorites) GoldCinema else CinemaSlate700, RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = if (showOnlyFavorites) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Show Favorites",
                            tint = if (showOnlyFavorites) GoldCinema else CinemaSlate400
                        )
                    }
                }

                // 12 Category Filter Pills
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(FilmTrickCategory.values()) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.title, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TrickOrange,
                                selectedLabelColor = CinemaSlate950,
                                containerColor = CinemaSlate900,
                                labelColor = CinemaSlate200
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // List of Film Trick Presets
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredPresets) { preset ->
                        FilmTrickItemRow(
                            preset = preset,
                            onApply = { onApplyTrick(preset) },
                            onToggleFavorite = { onToggleFavorite(preset.id) }
                        )
                    }
                }
            }
        }
    }

    if (showCustomBuilder) {
        CustomFilmTrickBuilderDialog(
            onSave = {
                onSaveCustomTrick(it)
                showCustomBuilder = false
            },
            onDismiss = { showCustomBuilder = false }
        )
    }
}

@Composable
fun FilmTrickItemRow(
    preset: FilmTrickPreset,
    onApply: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Surface(
        color = CinemaSlate900,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, CinemaSlate700),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("trick_row_${preset.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (preset.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (preset.isFavorite) GoldCinema else CinemaSlate600,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = preset.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CinemaSlate50
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(CinemaSlate800, RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = preset.category.title.uppercase(),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = TrickOrange
                            )
                        }
                        if (preset.isCustom) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(GoldCinemaDark, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("CUSTOM", fontSize = 7.sp, fontWeight = FontWeight.Black, color = CinemaSlate50)
                            }
                        }
                    }
                    Text(
                        text = preset.description,
                        fontSize = 10.sp,
                        color = CinemaSlate400,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onApply,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TrickOrange,
                    contentColor = CinemaSlate950
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                shape = RoundedCornerShape(6.dp)
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Apply", fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun CustomFilmTrickBuilderDialog(
    onSave: (CustomFilmTrick) -> Unit,
    onDismiss: () -> Unit
) {
    var trickName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FilmTrickCategory.ACTION) }
    var maskType by remember { mutableStateOf("Bezier Feather Mask") }
    var particleType by remember { mutableStateOf("Volumetric Smoke & Sparks") }
    var audioCue by remember { mutableStateOf("Cinematic Sub Drop & Whoosh") }
    var transitionCurve by remember { mutableStateOf("Cubic Bezier Fast Out") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = CinemaSlate950,
            border = BorderStroke(1.5.dp, GoldCinema),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CREATE CUSTOM FILM TRICK",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldCinema
                )
                Text(
                    text = "Combine Video, Masks, Particles, Keyframes, Audio & Transitions",
                    fontSize = 10.sp,
                    color = CinemaSlate400,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = trickName,
                    onValueChange = { trickName = it },
                    label = { Text("Film Trick Name") },
                    placeholder = { Text("e.g. Hyperspace Distortion Punch") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CinemaSlate900,
                        unfocusedContainerColor = CinemaSlate900,
                        focusedBorderColor = GoldCinema,
                        unfocusedBorderColor = CinemaSlate700
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Mask Architecture: $maskType", fontSize = 11.sp, color = CinemaSlate200)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Bezier Feather", "Chroma Key", "Planar Bounding").forEach {
                        FilterChip(
                            selected = maskType.contains(it),
                            onClick = { maskType = "$it Mask" },
                            label = { Text(it, fontSize = 9.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Particle FX Pass: $particleType", fontSize = 11.sp, color = CinemaSlate200)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Sparks & Fire", "Cyan Energy", "Anamorphic Dust").forEach {
                        FilterChip(
                            selected = particleType.contains(it),
                            onClick = { particleType = it },
                            label = { Text(it, fontSize = 9.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Audio Cue: $audioCue", fontSize = 11.sp, color = CinemaSlate200)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Sub Drop & Whoosh", "Laser Snap", "Thunder Impact").forEach {
                        FilterChip(
                            selected = audioCue.contains(it),
                            onClick = { audioCue = it },
                            label = { Text(it, fontSize = 9.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = CinemaSlate400)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                CustomFilmTrick(
                                    id = "custom_${System.currentTimeMillis()}",
                                    name = trickName.ifBlank { "Custom Cinematic Trick" },
                                    baseCategory = selectedCategory,
                                    maskType = maskType,
                                    particleType = particleType,
                                    audioCue = audioCue,
                                    transitionCurve = transitionCurve
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldCinema,
                            contentColor = CinemaSlate950
                        )
                    ) {
                        Text("Save Preset to Library", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
