package com.example.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import com.example.viewmodel.AiDirectorMessage

@Composable
fun AiVfxDirectorSheet(
    messages: List<AiDirectorMessage>,
    onSendPrompt: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var promptInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val suggestedPrompts = listOf(
        "Run 3D face relighting with golden key light.",
        "Clone actor voice and run studio ADR sync.",
        "Replace sky with volumetric sunset atmosphere.",
        "Smart rotoscope hair matte without green screen.",
        "Synthesize 960fps optical slow-motion.",
        "Clean sensor ISO noise and preserve 35mm grain.",
        "Color match grade from reference film frame.",
        "Generate 16-bit depth map and anamorphic bokeh.",
        "Auto beat-sync cut video clips to music transients.",
        "Synthesize foley footsteps matching actor movement.",
        "Isolate voice dialogue and kill room reverb & wind.",
        "Erase boom microphone with neural inpainting.",
        "Auto-frame character motion for 9:16 vertical cinema.",
        "Upscale footage to theatrical 8K resolution.",
        "Generate kinetic animated subtitles on speech.",
        "Add anamorphic blue streaks and lens flares.",
        "Make this character disappear.",
        "Make him teleport with cyan spark burst.",
        "Make this object explode.",
        "Create a superhero landing impact."
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
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
                .testTag("ai_vfx_director_dialog"),
            shape = RoundedCornerShape(12.dp),
            color = CinemaSlate950,
            border = BorderStroke(1.5.dp, AnamorphicCyan)
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
                                .background(AnamorphicCyanGlow, CircleShape)
                                .border(1.dp, AnamorphicCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = AnamorphicCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AI VFX DIRECTOR",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = CinemaSlate50
                            )
                            Text(
                                text = "Natural Language Cinematic Engine // Real VFX Pipeline Configuration",
                                fontSize = 10.sp,
                                color = AnamorphicCyan
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = CinemaSlate400)
                    }
                }

                // Chat stream
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { msg ->
                        AiMessageBubble(message = msg)
                    }
                }

                // Suggested natural language command chips
                Text(
                    text = "DIRECTOR QUICK ACTIONS:",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = CinemaSlate400,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                    fontFamily = FontFamily.Monospace
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(suggestedPrompts) { prompt ->
                        SuggestionChip(
                            onClick = { onSendPrompt(prompt) },
                            label = { Text(prompt, fontSize = 10.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = CinemaSlate900,
                                labelColor = CinemaSlate200
                            ),
                            border = BorderStroke(1.dp, CinemaSlate700)
                        )
                    }
                }

                // Prompt Input Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CinemaSlate900)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        placeholder = { Text("Speak or type cinematic instruction (e.g. 'Make this character disappear')...", fontSize = 11.sp, color = CinemaSlate400) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_vfx_prompt_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CinemaSlate950,
                            unfocusedContainerColor = CinemaSlate950,
                            focusedBorderColor = AnamorphicCyan,
                            unfocusedBorderColor = CinemaSlate700,
                            focusedTextColor = CinemaSlate50,
                            unfocusedTextColor = CinemaSlate50
                        ),
                        singleLine = false,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (promptInput.isNotBlank()) {
                                onSendPrompt(promptInput)
                                promptInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(AnamorphicCyan, RoundedCornerShape(8.dp))
                            .testTag("btn_send_ai_prompt")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = CinemaSlate950
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AiMessageBubble(message: AiDirectorMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isFromUser) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(AnamorphicCyan.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = AnamorphicCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            color = if (message.isFromUser) CinemaSlate800 else CinemaSlate900,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, if (message.isFromUser) GoldCinema else CinemaSlate700),
            modifier = Modifier.widthIn(max = 340.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = if (message.isFromUser) "DIRECTOR" else "AI VFX SYSTEM",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (message.isFromUser) GoldCinema else AnamorphicCyan,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.text,
                    fontSize = 12.sp,
                    color = CinemaSlate50
                )

                message.appliedMagicType?.let { magic ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = MagicVioletGlow,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(0.5.dp, MagicViolet)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = MagicViolet, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Configured: ${magic.title}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MagicViolet)
                        }
                    }
                }

                message.technicalNote?.let { note ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ℹ $note",
                        fontSize = 9.sp,
                        color = CinemaSlate400,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
