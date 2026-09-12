package com.example.ui.components

import androidx.compose.foundation.background
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
fun AccountButton(currentUser: UserAccount?, onOpenAccountMenu: () -> Unit, onSignInClicked: () -> Unit, modifier: Modifier = Modifier) {
    Surface(onClick = onOpenAccountMenu, shape = RoundedCornerShape(20.dp), color = CinemaSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, CinemaGold.copy(alpha = 0.5f)), modifier = modifier.testTag("account_user_button")) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            Box(Modifier.size(26.dp).clip(CircleShape).background(Brush.linearGradient(listOf(CinemaGold, CinemaCyan))), contentAlignment = Alignment.Center) { Text("D", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            Spacer(Modifier.width(8.dp))
            Column { Text("DIV Studio", color = CinemaWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold); Text("Local studio session", color = CinemaMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace) }
            Spacer(Modifier.width(4.dp)); Icon(Icons.Default.KeyboardArrowDown, "Studio menu", tint = CinemaGold, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun AccountMenuDialog(currentUser: UserAccount?, myFilmsCount: Int, draftsCount: Int, exportedCount: Int, onDismiss: () -> Unit,
    onSignInWithGoogle: () -> Unit, onSignOut: () -> Unit, onNavigateToMyFilms: () -> Unit, onNavigateToRecentProjects: () -> Unit,
    modifier: Modifier = Modifier) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(shape = RoundedCornerShape(16.dp), color = CinemaSurfaceElevated, border = androidx.compose.foundation.BorderStroke(1.dp, CinemaOutline),
            modifier = modifier.fillMaxWidth(0.92f).widthIn(max = 500.dp).padding(16.dp).testTag("account_menu_dialog")) {
            Column(Modifier.padding(20.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("DIV EDIT AI STUDIO", color = CinemaGold, fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Close, "Close", tint = CinemaMuted) }
                }
                Spacer(Modifier.height(14.dp))
                Card(colors = CardDefaults.cardColors(containerColor = CinemaSurface), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(48.dp).clip(CircleShape).background(Brush.linearGradient(listOf(CinemaGold, CinemaCyan))), contentAlignment = Alignment.Center) { Text("D", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 20.sp) }
                        Spacer(Modifier.width(12.dp)); Column { Text("DIV Studio", color = CinemaWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp); Text("Local studio session", color = CinemaCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace); Text("FREE // CINEMA STUDIO", color = CinemaMuted, fontSize = 10.sp) }
                    }
                }
                Spacer(Modifier.height(14.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("My Films", "$myFilmsCount", Modifier.weight(1f)) { onDismiss(); onNavigateToMyFilms() }
                    StatCard("Drafts", "$draftsCount", Modifier.weight(1f)) { onDismiss(); onNavigateToMyFilms() }
                    StatCard("Masters", "$exportedCount", Modifier.weight(1f)) { onDismiss(); onNavigateToMyFilms() }
                }
                Spacer(Modifier.height(16.dp)); Text("STUDIO NAVIGATION", color = CinemaMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp); Spacer(Modifier.height(8.dp))
                MenuItem(Icons.Default.Movie, "My Films & Projects", "Access your film sequences") { onDismiss(); onNavigateToMyFilms() }
                MenuItem(Icons.Default.History, "Recent Projects", "Continue ongoing timelines") { onDismiss(); onNavigateToRecentProjects() }
                MenuItem(Icons.Default.CloudSync, "Cinema Vault & Storage", "Local project storage") {}
                MenuItem(Icons.Default.Settings, "Studio Settings", "Editing and production preferences") {}
                Spacer(Modifier.height(12.dp))
                Surface(color = CinemaSurfaceVariant, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.LockOpen, null, tint = CinemaCyan, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Authentication is not required. DIV EDIT AI runs as a free local studio.", color = CinemaMuted, fontSize = 10.sp) }
                }
            }
        }
    }
}

@Composable private fun StatCard(label: String, value: String, modifier: Modifier, onClick: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = CinemaSurface), modifier = modifier.clickable(onClick = onClick)) { Column(Modifier.padding(10.dp)) { Text(label, color = CinemaMuted, fontSize = 10.sp); Text(value, color = CinemaGold, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text("Projects", color = CinemaMuted, fontSize = 8.sp) } }
}

@Composable private fun MenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(onClick = onClick, color = Color.Transparent, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(vertical = 8.dp, horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = CinemaCyan, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, color = CinemaWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium); Text(subtitle, color = CinemaMuted, fontSize = 10.sp) }; Icon(Icons.Default.ChevronRight, null, tint = CinemaMuted, modifier = Modifier.size(16.dp))
        }
    }
}
