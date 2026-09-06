package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AssetType
import com.example.model.MediaAsset
import com.example.model.TimelineClip
import com.example.ui.theme.*
import com.example.viewmodel.MediaSortOrder

@Composable
fun MediaImportDialog(
    mediaAssets: List<MediaAsset>,
    clipToReplace: TimelineClip? = null,
    onImportAssetToTimeline: (MediaAsset) -> Unit,
    onExecuteReplaceClip: (MediaAsset) -> Unit,
    onPreviewAsset: (MediaAsset) -> Unit,
    onRenameAsset: (MediaAsset) -> Unit,
    onDeleteAsset: (String) -> Unit,
    onImportFromDeviceFile: (String, AssetType, String, Long, String?, String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFolderCategory by remember { mutableStateOf<String?>("All") }
    var currentSortOrder by remember { mutableStateOf(MediaSortOrder.NAME_ASC) }
    var showSortMenu by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Android System File Picker for All Media
    val systemMediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            val path = uri.path ?: uri.toString()
            val fileName = path.substringAfterLast("/").substringAfterLast(":")
            val cleanName = if (fileName.contains(".")) fileName else "Imported_Footage_${System.currentTimeMillis().toString().takeLast(4)}.mov"
            val extension = cleanName.substringAfterLast(".", "mov").lowercase()

            val type = when (extension) {
                "mp4", "mov", "webm", "avi", "mkv" -> AssetType.LIVE_ACTION
                "jpg", "jpeg", "png", "webp", "exr", "tif", "tiff" -> AssetType.STILL_IMAGE
                "mp3", "wav", "aac", "flac", "ogg" -> AssetType.AUDIO_SCORE
                else -> AssetType.LIVE_ACTION
            }

            val folder = when (type) {
                AssetType.LIVE_ACTION -> "Live Footage"
                AssetType.ANIMATION, AssetType.RENDERED_SCENE -> "Animation & Anime"
                AssetType.IMAGE_SEQUENCE, AssetType.STILL_IMAGE -> "Image Sequences & Plates"
                AssetType.VFX_ASSET -> "VFX Elements"
                AssetType.AUDIO_SCORE, AssetType.FOLEY_SFX, AssetType.VOICE_OVER -> "Film Score & Foley"
                AssetType.LOGO -> "Logos & Branding"
            }

            onImportFromDeviceFile(
                cleanName,
                type,
                extension,
                1024L * 1024L * 95L,
                uri.toString(),
                folder
            )
        }
    }

    // Filter & Sort Assets
    val folderCategories = listOf(
        "All",
        "Live Footage",
        "Animation & Anime",
        "Image Sequences & Plates",
        "VFX Elements",
        "Film Score & Foley",
        "Logos & Branding"
    )

    val processedAssets = remember(mediaAssets, searchQuery, selectedFolderCategory, currentSortOrder) {
        var list = mediaAssets

        // Folder Filter
        if (selectedFolderCategory != null && selectedFolderCategory != "All") {
            list = list.filter { it.folderCategory.equals(selectedFolderCategory, ignoreCase = true) }
        }

        // Search Filter
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.type.label.lowercase().contains(q) ||
                it.fileExtension.lowercase().contains(q) ||
                it.folderCategory.lowercase().contains(q)
            }
        }

        // Sorting
        when (currentSortOrder) {
            MediaSortOrder.NAME_ASC -> list.sortedBy { it.name.lowercase() }
            MediaSortOrder.NAME_DESC -> list.sortedByDescending { it.name.lowercase() }
            MediaSortOrder.DATE_ADDED -> list.sortedByDescending { it.dateAddedMs }
            MediaSortOrder.DURATION -> list.sortedByDescending { it.durationMs }
            MediaSortOrder.TYPE -> list.sortedBy { it.type.label }
            MediaSortOrder.FILE_SIZE -> list.sortedByDescending { it.fileSizeBytes }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .testTag("media_import_dialog"),
            shape = RoundedCornerShape(14.dp),
            color = CinemaSurfaceElevated,
            border = BorderStroke(1.5.dp, if (clipToReplace != null) CinemaGold else CinemaCyan)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CinemaSurface)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (clipToReplace != null) CinemaGold.copy(alpha = 0.2f) else CinemaCyan.copy(alpha = 0.2f),
                                    CircleShape
                                )
                                .border(1.dp, if (clipToReplace != null) CinemaGold else CinemaCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (clipToReplace != null) Icons.Default.SwapHoriz else Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = if (clipToReplace != null) CinemaGold else CinemaCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (clipToReplace != null) "REPLACE TIMELINE MEDIA" else "CINEMA MEDIA LIBRARY & IMPORT",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = CinemaWhite,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = if (clipToReplace != null)
                                    "Select replacement asset for clip: '${clipToReplace.title}'"
                                else
                                    "Import Video (MP4/MOV/MKV/WebM), Images, Anime Sequences, & Audio",
                                fontSize = 10.sp,
                                color = if (clipToReplace != null) CinemaGold else CinemaCyan
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = CinemaMuted)
                    }
                }

                // Quick Import Action Strip (Requirement 45 & 49: Prominent buttons)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CinemaSurfaceVariant)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Unified System File Picker (All Formats)
                    Button(
                        onClick = {
                            systemMediaPicker.launch(
                                arrayOf("video/*", "image/*", "audio/*", "*/*")
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CinemaGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("system_import_media_button")
                    ) {
                        Icon(Icons.Default.FileOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("IMPORT MEDIA", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    // Import Video Shortcut
                    OutlinedButton(
                        onClick = {
                            systemMediaPicker.launch(arrayOf("video/*"))
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CinemaCyan),
                        border = BorderStroke(1.dp, CinemaCyan.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("system_import_video_button")
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("IMPORT VIDEO", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    }

                    // Import Image Shortcut
                    OutlinedButton(
                        onClick = {
                            systemMediaPicker.launch(arrayOf("image/*"))
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CinemaWhite),
                        border = BorderStroke(1.dp, CinemaOutline),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("system_import_image_button")
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("IMPORT IMAGE", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    }

                    // Import Audio / Score Shortcut
                    OutlinedButton(
                        onClick = {
                            systemMediaPicker.launch(arrayOf("audio/*"))
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF10AC84)),
                        border = BorderStroke(1.dp, Color(0xFF10AC84).copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("system_import_audio_button")
                    ) {
                        Icon(Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("IMPORT AUDIO", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    }

                    // Simulated Camera / Storage Plate
                    FilledTonalButton(
                        onClick = {
                            onImportFromDeviceFile(
                                "Production_Take_${System.currentTimeMillis().toString().takeLast(3)}_4K.mov",
                                AssetType.LIVE_ACTION,
                                "mov",
                                1024L * 1024L * 320L,
                                null,
                                "Live Footage"
                            )
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = CinemaSurface,
                            contentColor = CinemaWhite
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Simulate Camera Drop", fontSize = 10.sp)
                    }
                }

                // Search Bar & Sort Menu Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search footage, codecs, foley, anime...", fontSize = 11.sp, color = CinemaMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = CinemaMuted, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(20.dp)) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = CinemaMuted)
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CinemaCyan,
                            unfocusedBorderColor = CinemaOutline,
                            focusedTextColor = CinemaWhite,
                            unfocusedTextColor = CinemaWhite
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("media_search_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Sort Order Button
                    Box {
                        OutlinedButton(
                            onClick = { showSortMenu = true },
                            border = BorderStroke(1.dp, CinemaOutline),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("media_sort_button")
                        ) {
                            Icon(Icons.Default.Sort, contentDescription = null, tint = CinemaGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(currentSortOrder.label, fontSize = 10.sp, color = CinemaWhite)
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                            modifier = Modifier.background(CinemaSurfaceElevated)
                        ) {
                            MediaSortOrder.values().forEach { order ->
                                DropdownMenuItem(
                                    text = { Text(order.label, fontSize = 11.sp, color = CinemaWhite) },
                                    onClick = {
                                        currentSortOrder = order
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Folder & Category Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    folderCategories.forEach { category ->
                        val isSelected = selectedFolderCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFolderCategory = category },
                            label = { Text(category, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CinemaCyan,
                                selectedLabelColor = Color.Black,
                                containerColor = CinemaSurface,
                                labelColor = CinemaMuted
                            ),
                            border = BorderStroke(1.dp, if (isSelected) CinemaCyan else CinemaOutline)
                        )
                    }
                }

                // Asset items list
                if (processedAssets.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SearchOff, contentDescription = null, tint = CinemaMuted, modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No media found matching '${searchQuery}'", color = CinemaMuted, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    searchQuery = ""
                                    selectedFolderCategory = "All"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CinemaSurfaceVariant)
                            ) {
                                Text("Reset Filters", fontSize = 11.sp)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(processedAssets, key = { it.id }) { asset ->
                            MediaItemRow(
                                asset = asset,
                                isReplaceMode = clipToReplace != null,
                                onAddOrReplace = {
                                    if (clipToReplace != null) {
                                        onExecuteReplaceClip(asset)
                                    } else {
                                        onImportAssetToTimeline(asset)
                                        onDismiss()
                                    }
                                },
                                onPreview = { onPreviewAsset(asset) },
                                onRename = { onRenameAsset(asset) },
                                onDelete = { onDeleteAsset(asset.id) }
                            )
                        }
                    }
                }

                // Footer Note
                Surface(
                    color = CinemaSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${processedAssets.size} Assets loaded • Non-destructive project references",
                            color = CinemaMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "DIV EDIT AI Studio Engine",
                            color = CinemaGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaItemRow(
    asset: MediaAsset,
    isReplaceMode: Boolean,
    onAddOrReplace: () -> Unit,
    onPreview: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = CinemaSurface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, CinemaOutline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon & Details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onPreview)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(asset.colorTag).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .border(1.dp, Color(asset.colorTag), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (asset.type) {
                            AssetType.LIVE_ACTION -> Icons.Default.Movie
                            AssetType.ANIMATION, AssetType.RENDERED_SCENE -> Icons.Default.Brush
                            AssetType.IMAGE_SEQUENCE, AssetType.STILL_IMAGE -> Icons.Default.BurstMode
                            AssetType.VFX_ASSET -> Icons.Default.AutoAwesome
                            AssetType.AUDIO_SCORE, AssetType.FOLEY_SFX, AssetType.VOICE_OVER -> Icons.Default.GraphicEq
                            AssetType.LOGO -> Icons.Default.Verified
                        },
                        contentDescription = null,
                        tint = Color(asset.colorTag),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = asset.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CinemaWhite
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = CinemaSurfaceVariant
                        ) {
                            Text(
                                text = ".${asset.fileExtension.uppercase()}",
                                fontSize = 8.sp,
                                color = CinemaGold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${asset.type.label} • ${asset.resolution} • ${asset.durationMs / 1000}s • ${asset.fileSizeBytes / (1024 * 1024)}MB",
                        fontSize = 9.sp,
                        color = CinemaMuted,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Action Buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPreview, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Visibility, contentDescription = "Preview", tint = CinemaCyan, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onRename, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Rename", tint = CinemaMuted, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFFF7675), modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(4.dp))
                Button(
                    onClick = onAddOrReplace,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isReplaceMode) CinemaGold else CinemaCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isReplaceMode) Icons.Default.SwapHoriz else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isReplaceMode) "Replace" else "Add",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
