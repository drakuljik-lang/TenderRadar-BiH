package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================
// TenderRadar BiH — Black & Lime Palette (fintech style)
// ============================================================

// PRIMARY ACCENT — Lime / Yellow
val LimePrimary      = Color(0xFFD4F542)
val LimePrimaryDark  = Color(0xFFB8E035)
val LimePrimaryLight = Color(0xFFE5FF6B)
val LimeOnPrimary    = Color(0xFF0A0A0A)

// Legacy aliases (staro ime -> nova boja) — ne mijenjati nazive
val CyanAccent      = LimePrimary
val CyanAccentLight = LimePrimaryLight
val CyanAccentDark  = LimePrimaryDark

// DARK BACKGROUNDS (crna pozadina)
val BackgroundBlack   = Color(0xFF0A0A0A)
val SurfaceElevated   = Color(0xFF141414)
val SurfaceVariantDark= Color(0xFF1F1F1F)
val OutlineDark       = Color(0xFF2A2A2A)

// Legacy aliases
val NavyPrimary   = LimePrimary
val NavyDark      = BackgroundBlack
val NavyLight     = SurfaceElevated
val SurfaceDark   = SurfaceElevated
val CardBackgroundDark = SurfaceVariantDark

// TEXT ON DARK
val TextPrimaryDark   = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xFFA3A3A3)
val TextMutedDark     = Color(0xFF6B6B6B)
val SlateSecondary    = TextSecondaryDark
val SlateLight        = TextMutedDark

// WHITE CARDS (kao KPI kartice na slici)
val CardWhite             = Color(0xFFFFFFFF)
val TextOnWhite           = Color(0xFF0A0A0A)
val TextOnWhiteSecondary  = Color(0xFF525252)
val CardBackgroundLight   = CardWhite

// LIGHT THEME
val BackgroundLight   = Color(0xFFFAFAFA)
val SurfaceLight      = Color(0xFFFFFFFF)
val TextPrimaryLight  = Color(0xFF0A0A0A)
val TextSecondaryLight= Color(0xFF525252)
val TextMutedLight    = Color(0xFF737373)

// MATCH SCORE STATUS
val MatchHighGreen     = LimePrimary              // 80-100: lime
val MatchGoodCyan      = Color(0xFF86EFAC)        // 60-79: svijetlozelena
val MatchPossibleAmber = Color(0xFFFCD34D)        // 40-59: amber
val MatchLowSlate      = Color(0xFF6B6B6B)        // 0-39: siva

// URGENCY
val UrgencyRed    = Color(0xFFEF4444)
val UrgencyOrange = Color(0xFFF97316)
