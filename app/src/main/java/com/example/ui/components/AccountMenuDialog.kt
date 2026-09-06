package com.example.ui.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.UserAccount
import com.example.ui.theme.*

@Composable
fun AccountButton(
    currentUser: UserAccount?,
    onOpenAccountMenu: () -> Unit,
    onSignInClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (currentUser != null && currentUser.isSignedIn) {
        // Signed-in User Pill
        Surface(
            onClick = onOpenAccountMenu,
            shape = RoundedCornerShape(20.dp),
            color = CinemaSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, CinemaGold.copy(alpha = 0.5f)),
            modifier = modifier.testTag("account_user_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                // Profile Avatar Circle
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(CinemaGold, CinemaCyan))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser.displayName.firstOrNull()?.uppercase() ?: "D",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = currentUser.displayName,
                        color = CinemaWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = currentUser.email,
                        color = CinemaMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Account Menu",
                    tint = CinemaGold,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    } else {
        // Official Google Sign-In Header Button
        OutlinedButton(
            onClick = onSignInClicked,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color(0xFF1F1F1F),
                contentColor = CinemaWhite
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF747775)),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = modifier.testTag("google_sign_in_header_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Google "G" symbol glyph representation
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        color = Color(0xFF4285F4),
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign in with Google",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun AccountMenuDialog(
    currentUser: UserAccount?,
    myFilmsCount: Int,
    draftsCount: Int,
    exportedCount: Int,
    onDismiss: () -> Unit,
    onSignInWithGoogle: () -> Unit,
    onSignOut: () -> Unit,
    onNavigateToMyFilms: () -> Unit,
    onNavigateToRecentProjects: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CinemaSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CinemaOutline),
            modifier = modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 500.dp)
                .padding(16.dp)
                .testTag("account_menu_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = CinemaGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DIV EDIT AI ACCOUNT",
                            color = CinemaGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
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

                Spacer(modifier = Modifier.height(16.dp))

                if (currentUser != null && currentUser.isSignedIn) {
                    // Profile Header Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CinemaSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CinemaOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(CinemaGold, CinemaCyan))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser.displayName.firstOrNull()?.uppercase() ?: "D",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentUser.displayName,
                                        color = CinemaWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = CinemaGold.copy(alpha = 0.2f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, CinemaGold)
                                    ) {
                                        Text(
                                            text = "PRO",
                                            color = CinemaGold,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = currentUser.email,
                                    color = CinemaCyan,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = currentUser.tier,
                                    color = CinemaMuted,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Storage Quotas & Project Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            label = "My Films",
                            value = "$myFilmsCount",
                            subtext = "Active Projects",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onDismiss()
                                onNavigateToMyFilms()
                            }
                        )
                        StatCard(
                            label = "Drafts",
                            value = "$draftsCount",
                            subtext = "Work In Progress",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onDismiss()
                                onNavigateToMyFilms()
                            }
                        )
                        StatCard(
                            label = "Masters",
                            value = "$exportedCount",
                            subtext = "Exported Cuts",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onDismiss()
                                onNavigateToMyFilms()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Menu Options
                    Text(
                        text = "ACCOUNT NAVIGATION",
                        color = CinemaMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    AccountMenuItem(
                        icon = Icons.Default.Movie,
                        title = "My Films & Projects",
                        subtitle = "Access all private cloud-synced film sequences",
                        onClick = {
                            onDismiss()
                            onNavigateToMyFilms()
                        }
                    )

                    AccountMenuItem(
                        icon = Icons.Default.History,
                        title = "Recent Projects",
                        subtitle = "Jump back into ongoing timelines and cuts",
                        onClick = {
                            onDismiss()
                            onNavigateToRecentProjects()
                        }
                    )

                    AccountMenuItem(
                        icon = Icons.Default.CloudSync,
                        title = "Cinema Vault & Storage",
                        subtitle = "Google Drive / Cloud High-Bitrate Master Sync (Active)",
                        onClick = {}
                    )

                    AccountMenuItem(
                        icon = Icons.Default.Settings,
                        title = "Studio Settings",
                        subtitle = "Color management, GPU neural accelerators, shortcuts",
                        onClick = {}
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Clearly Visible Sign Out Button
                    Button(
                        onClick = {
                            onDismiss()
                            onSignOut()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD63031).copy(alpha = 0.2f),
                            contentColor = Color(0xFFFF7675)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD63031)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("account_sign_out_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sign Out",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sign Out of Google Account",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    // Signed-Out State
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(CinemaSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = CinemaGold,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Sign In to DIV EDIT AI",
                            color = CinemaWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Sign in with your Google account to access your private films, saved Hollywood Magic presets, custom watermarks, and 4K masters.",
                            color = CinemaMuted,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                onDismiss()
                                onSignInWithGoogle()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF1F1F1F)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("modal_google_sign_in_button")
                        ) {
                            Text(
                                text = "G",
                                color = Color(0xFF4285F4),
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "Sign in with Google",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    subtext: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CinemaOutline),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = label, color = CinemaMuted, fontSize = 10.sp)
            Text(
                text = value,
                color = CinemaGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = subtext, color = CinemaMuted, fontSize = 8.sp)
        }
    }
}

@Composable
private fun AccountMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(CinemaSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CinemaCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = CinemaWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = CinemaMuted,
                    fontSize = 10.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = CinemaMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
