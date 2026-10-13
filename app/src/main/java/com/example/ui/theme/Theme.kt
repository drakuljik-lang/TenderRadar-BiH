package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary              = LimePrimary,
    onPrimary            = LimeOnPrimary,
    primaryContainer     = Color(0xFF2A3313),
    onPrimaryContainer   = LimePrimaryLight,

    secondary            = LimePrimary,
    onSecondary          = LimeOnPrimary,
    secondaryContainer   = Color(0xFF1F2610),
    onSecondaryContainer = LimePrimaryLight,

    tertiary             = LimePrimaryDark,
    onTertiary           = LimeOnPrimary,

    background           = BackgroundBlack,
    onBackground         = TextPrimaryDark,

    surface              = SurfaceElevated,
    onSurface            = TextPrimaryDark,

    surfaceVariant       = SurfaceVariantDark,
    onSurfaceVariant     = TextSecondaryDark,

    outline              = OutlineDark,
    outlineVariant       = Color(0xFF1A1A1A),

    error                = UrgencyRed,
    onError              = Color.White,

    inverseSurface       = CardWhite,
    inverseOnSurface     = TextOnWhite,
    inversePrimary       = LimePrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary              = Color(0xFF0A0A0A),
    onPrimary            = Color.White,
    primaryContainer     = LimePrimary,
    onPrimaryContainer   = LimeOnPrimary,

    secondary            = LimePrimaryDark,
    onSecondary          = LimeOnPrimary,
    secondaryContainer   = Color(0xFFE5FF6B),
    onSecondaryContainer = Color(0xFF1A1A0A),

    tertiary             = Color(0xFF333333),
    onTertiary           = Color.White,

    background           = BackgroundLight,
    onBackground         = TextPrimaryLight,

    surface              = SurfaceLight,
    onSurface            = TextPrimaryLight,

    surfaceVariant       = Color(0xFFF2F2F2),
    onSurfaceVariant     = TextSecondaryLight,

    outline              = Color(0xFFE0E0E0),
    outlineVariant       = Color(0xFFEBEBEB),

    error                = UrgencyRed,
    onError              = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Isključeno — fiksna brand paleta
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
