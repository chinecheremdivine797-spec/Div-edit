package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CinemaDarkColorScheme = darkColorScheme(
  primary = GoldCinema,
  onPrimary = CinemaSlate950,
  primaryContainer = GoldCinemaDark,
  onPrimaryContainer = CinemaSlate50,
  secondary = AnamorphicCyan,
  onSecondary = CinemaSlate950,
  secondaryContainer = AnamorphicCyanDark,
  onSecondaryContainer = CinemaSlate50,
  tertiary = MagicViolet,
  onTertiary = CinemaSlate950,
  tertiaryContainer = MagicVioletDark,
  onTertiaryContainer = CinemaSlate50,
  background = CinemaSlate950,
  onBackground = CinemaSlate50,
  surface = CinemaSlate900,
  onSurface = CinemaSlate50,
  surfaceVariant = CinemaSlate800,
  onSurfaceVariant = CinemaSlate200,
  outline = CinemaSlate700,
  outlineVariant = CinemaSlate600,
  error = RecordRed,
  onError = CinemaSlate50
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Cinema studio default is professional darkroom
  dynamicColor: Boolean = false, // Use DIV EDIT AI brand palette
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = CinemaDarkColorScheme,
    typography = Typography,
    content = content
  )
}

