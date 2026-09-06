package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.MediaAsset
import com.example.ui.theme.*

@Composable
fun RenameMediaDialog(
    asset: MediaAsset?,
    onDismiss: () -> Unit,
    onConfirmRename: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (asset == null) return

    var currentName by remember(asset) { mutableStateOf(asset.name) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = CinemaSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CinemaOutline),
            modifier = modifier
                .fillMaxWidth(0.9f)
                .widthIn(max = 440.dp)
                .padding(16.dp)
                .testTag("rename_media_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = CinemaCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RENAME MEDIA ASSET",
                            color = CinemaWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CinemaMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Renaming updates the project clip references while preserving the original source file.",
                    color = CinemaMuted,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = currentName,
                    onValueChange = { currentName = it },
                    label = { Text("Asset Title") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CinemaCyan,
                        unfocusedBorderColor = CinemaOutline,
                        focusedTextColor = CinemaWhite,
                        unfocusedTextColor = CinemaWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rename_media_input")
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = CinemaMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (currentName.isNotBlank()) {
                                onConfirmRename(asset.id, currentName)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CinemaCyan,
                            contentColor = androidx.compose.ui.graphics.Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("confirm_rename_button")
                    ) {
                        Text("Save Name", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
