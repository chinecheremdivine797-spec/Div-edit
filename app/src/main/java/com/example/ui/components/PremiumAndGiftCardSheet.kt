package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage

@Composable
fun PremiumAndGiftCardSheet(
    onDismiss: () -> Unit
) {
    var giftCardUri by remember { mutableStateOf<Uri?>(null) }
    var paymentMessage by remember { mutableStateOf<String?>(null) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> giftCardUri = uri }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth()) {
            LazyColumn(modifier = Modifier.padding(20.dp)) {
                item {
                    Text("DIV EDIT Premium", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(6.dp))
                    Text("Full DIV Edit AI tools and professional export")
                    Spacer(Modifier.height(14.dp))
                    Text("₦5,000 / month", style = MaterialTheme.typography.headlineMedium)
                    Text("Cancel anytime. Premium access should be granted only after verified payment.")
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            paymentMessage = "Paystack checkout is ready for connection. Your Paystack public key and secure server checkout endpoint must be configured before real charges are enabled."
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Subscribe — ₦5,000/month") }

                    paymentMessage?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(20.dp))
                    Divider()
                    Spacer(Modifier.height(16.dp))
                    Text("Gift Card", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(4.dp))
                    Text("Upload a clear image of your gift card for review.")
                    Spacer(Modifier.height(10.dp))

                    giftCardUri?.let { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = "Gift card preview",
                            modifier = Modifier.fillMaxWidth().height(180.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { picker.launch("image/*") },
                            modifier = Modifier.weight(1f)
                        ) { Text(if (giftCardUri == null) "Upload image" else "Replace image") }

                        if (giftCardUri != null) {
                            OutlinedButton(
                                onClick = { giftCardUri = null },
                                modifier = Modifier.weight(1f)
                            ) { Text("Remove") }
                        }
                    }

                    Spacer(Modifier.height(18.dp))
                    Text(
                        "Gift-card uploads are only a submission area. Do not upload PINs or codes unless DIV explicitly requests them through a secure verification process.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                        Text("Done")
                    }
                }
            }
        }
    }
}
