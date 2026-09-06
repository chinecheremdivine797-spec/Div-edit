package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.AiToolExecutionState

@Composable
fun AiToolsStudioSheet(
    tools: List<AiToolSpec>,
    selectedTool: AiToolSpec,
    executionState: AiToolExecutionState,
    parameterValues: Map<String, Float>,
    onSelectTool: (AiToolSpec) -> Unit,
    onUpdateParam: (String, Float) -> Unit,
    onRunProcessing: (AiToolSpec) -> Unit,
    onApplyToTimeline: (AiToolSpec) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AiToolCategory.ALL) }

    val filteredTools = remember(searchQuery, selectedCategory, tools) {
        tools.filter { tool ->
            val matchesCategory = (selectedCategory == AiToolCategory.ALL || tool.category == selectedCategory)
            val matchesSearch = searchQuery.isBlank() ||
                    tool.name.contains(searchQuery, ignoreCase = true) ||
                    tool.tagline.contains(searchQuery, ignoreCase = true) ||
                    tool.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f)
                .testTag("ai_tools_studio_dialog"),
            shape = RoundedCornerShape(12.dp),
            color = CinemaSlate950,
            border = BorderStroke(1.5.dp, AnamorphicCyan)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header Bar
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
                                .background(AnamorphicCyanGlow, CircleShape)
                                .border(1.dp, AnamorphicCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AnamorphicCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AI CINEMA SUITE",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = CinemaSlate50
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = GoldCinemaGlow,
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(0.5.dp, GoldCinema)
                                ) {
                                    Text(
                                        text = "20 PRO ENGINES",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldCinema,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Neural 3D Relighting, Voice Cloning, Sky Engine, Rotoscoping & 8K Mastering",
                                fontSize = 10.sp,
                                color = AnamorphicCyan
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_ai_tools_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = CinemaSlate400)
                    }
                }

                // Search & Filter Row
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CinemaSlate900.copy(alpha = 0.6f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search 20 AI tools (e.g. relight, voice, sky, slow-mo, beat, denoise)...", fontSize = 11.sp, color = CinemaSlate400) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AnamorphicCyan, modifier = Modifier.size(16.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = CinemaSlate400, modifier = Modifier.size(14.dp))
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("ai_tools_search_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CinemaSlate950,
                            unfocusedContainerColor = CinemaSlate950,
                            focusedBorderColor = AnamorphicCyan,
                            unfocusedBorderColor = CinemaSlate700,
                            focusedTextColor = CinemaSlate50,
                            unfocusedTextColor = CinemaSlate50
                        ),
                        singleLine = true
                    )

                    // Category Filter Tabs
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(AiToolCategory.values()) { category ->
                            val isSelected = (selectedCategory == category)
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = category },
                                label = {
                                    Text(
                                        text = category.title,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = CinemaSlate900,
                                    labelColor = CinemaSlate400,
                                    selectedContainerColor = AnamorphicCyanDark,
                                    selectedLabelColor = CinemaSlate50
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = CinemaSlate700,
                                    selectedBorderColor = AnamorphicCyan,
                                    borderWidth = 1.dp,
                                    selectedBorderWidth = 1.dp,
                                    enabled = true,
                                    selected = isSelected
                                )
                            )
                        }
                    }
                }

                // Main Content Body: Responsive Split Layout
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Column: 20 Tools Navigation List
                    Column(
                        modifier = Modifier
                            .weight(0.48f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "SELECT NEURAL ENGINE (${filteredTools.size}/${tools.size}):",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = CinemaSlate400,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(CinemaSlate900, RoundedCornerShape(8.dp))
                                .padding(6.dp)
                                .testTag("ai_tools_list"),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(filteredTools) { tool ->
                                val isSelected = (tool.id == selectedTool.id)
                                val toolAccent = Color(tool.accentColor)

                                Surface(
                                    onClick = { onSelectTool(tool) },
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) CinemaSlate800 else CinemaSlate950,
                                    border = BorderStroke(
                                        width = if (isSelected) 1.5.dp else 0.5.dp,
                                        color = if (isSelected) toolAccent else CinemaSlate700
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("ai_tool_card_${tool.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Tool Number badge
                                        Surface(
                                            color = toolAccent.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp),
                                            border = BorderStroke(1.dp, toolAccent.copy(alpha = 0.5f)),
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "#${tool.number.toString().padStart(2, '0')}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = toolAccent,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = tool.shortName,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) CinemaSlate50 else CinemaSlate200
                                                )
                                                Text(
                                                    text = tool.category.title.take(12),
                                                    fontSize = 8.sp,
                                                    color = CinemaSlate400,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                            Text(
                                                text = tool.tagline,
                                                fontSize = 9.sp,
                                                color = if (isSelected) toolAccent else CinemaSlate400,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Right Column: Active Tool Inspector & Controls
                    Column(
                        modifier = Modifier
                            .weight(0.52f)
                            .fillMaxHeight()
                            .background(CinemaSlate900, RoundedCornerShape(8.dp))
                            .border(1.dp, Color(selectedTool.accentColor).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val activeAccent = Color(selectedTool.accentColor)

                        // Inspector Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ENGINE #${selectedTool.number.toString().padStart(2, '0')} // ${selectedTool.category.title.uppercase()}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = activeAccent,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = selectedTool.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = CinemaSlate50
                                )
                            }
                        }

                        // Description Box
                        Surface(
                            color = CinemaSlate950,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(0.5.dp, CinemaSlate700),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = selectedTool.description,
                                    fontSize = 10.sp,
                                    color = CinemaSlate300,
                                    lineHeight = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Layers, contentDescription = null, tint = activeAccent, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Target Output: ${selectedTool.outputEffectType}",
                                        fontSize = 8.sp,
                                        color = activeAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        // Parameter Sliders Section
                        Text(
                            text = "NEURAL TUNING PARAMETERS:",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldCinema,
                            fontFamily = FontFamily.Monospace
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            selectedTool.defaultParameters.forEach { param ->
                                val currentValue = parameterValues[param.id] ?: param.value

                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = param.name,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = CinemaSlate200
                                        )
                                        Text(
                                            text = "${(currentValue * 100).toInt()}%",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = activeAccent,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Slider(
                                        value = currentValue,
                                        onValueChange = { onUpdateParam(param.id, it) },
                                        valueRange = param.minValue..param.maxValue,
                                        colors = SliderDefaults.colors(
                                            thumbColor = activeAccent,
                                            activeTrackColor = activeAccent,
                                            inactiveTrackColor = CinemaSlate700
                                        ),
                                        modifier = Modifier
                                            .height(24.dp)
                                            .testTag("slider_${param.id}")
                                    )
                                }
                            }
                        }

                        // 4-Step Neural Pipeline Tracker
                        Text(
                            text = "4-PHASE NEURAL PIPELINE:",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = CinemaSlate400,
                            fontFamily = FontFamily.Monospace
                        )

                        Surface(
                            color = CinemaSlate950,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(0.5.dp, CinemaSlate700),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                selectedTool.processingSteps.forEachIndexed { index, step ->
                                    val isStepComplete = executionState.isComplete || (executionState.isProcessing && index < executionState.stepIndex)
                                    val isStepCurrent = executionState.isProcessing && index == executionState.stepIndex

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = when {
                                                isStepComplete -> Icons.Default.CheckCircle
                                                isStepCurrent -> Icons.Default.Sync
                                                else -> Icons.Default.RadioButtonUnchecked
                                            },
                                            contentDescription = null,
                                            tint = when {
                                                isStepComplete -> AudioEmerald
                                                isStepCurrent -> GoldCinema
                                                else -> CinemaSlate600
                                            },
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Phase ${index + 1}: $step",
                                            fontSize = 9.sp,
                                            color = when {
                                                isStepComplete -> CinemaSlate200
                                                isStepCurrent -> GoldCinema
                                                else -> CinemaSlate400
                                            },
                                            fontWeight = if (isStepCurrent) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        // Telemetry & Progress
                        if (executionState.isProcessing) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = executionState.logMessage,
                                        fontSize = 9.sp,
                                        color = GoldCinema,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "${executionState.progress}%",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldCinema,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                LinearProgressIndicator(
                                    progress = { executionState.progress / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = activeAccent,
                                    trackColor = CinemaSlate800
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Action Buttons: Run & Apply
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onRunProcessing(selectedTool) },
                                enabled = !executionState.isProcessing,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CinemaSlate800,
                                    contentColor = activeAccent
                                ),
                                border = BorderStroke(1.dp, activeAccent),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .testTag("btn_run_ai_tool")
                            ) {
                                Icon(Icons.Default.Memory, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("RUN NEURAL PASS", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onApplyToTimeline(selectedTool) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = activeAccent,
                                    contentColor = CinemaSlate950
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .testTag("btn_apply_ai_tool")
                            ) {
                                Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("APPLY TO TIMELINE", fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}
