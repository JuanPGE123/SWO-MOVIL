package com.example.swo.ui.theme

import androidx.compose.ui.graphics.Color

// Corporativo SWO - Navy Corporate Theme
val SwoBlue = Color(0xFF0F2C59) // Corporate Navy
val SwoLightBlue = Color(0xFF2563EB) // Blue
val SwoAccent = Color(0xFF3B82F6) // Brighter Blue

// Alias para compatibilidad con pantallas existentes
val NeonBlue = SwoLightBlue
val NeonCyan = SwoAccent
val CharcoalGrey = Color(0xFF0F172A)
val SurfaceGrey = Color(0xFF1E293B)

// Estados
val StatusSuccess = Color(0xFF10B981)
val StatusError = Color(0xFFEF4444)
val StatusInfo = SwoAccent
val StatusCancelled = Color(0xFF64748B)

// Material 3 Mapping
val PrimaryDark = SwoBlue
val OnPrimaryDark = Color.White
val SecondaryDark = SwoLightBlue
val OnSecondaryDark = Color.White
val TertiaryDark = SwoAccent
val OnTertiaryDark = Color.White
val BackgroundDark = Color(0xFF0B1120) // Dark Navy Background
val OnBackgroundDark = Color.White
val SurfaceVariantDark = Color(0xFF1E293B)
val OnSurfaceVariantDark = Color(0xFF94A3B8)
val ErrorDark = StatusError
val OnErrorDark = Color.White

// Dark Theme Variants
val DarkBackground_OLED = Color(0xFF000000)
val DarkBackground_Slate = Color(0xFF0F172A)
val DarkBackground_DeepBlue = Color(0xFF090D1A) // Deep Navy

val SurfaceDark = Color(0xFF1E293B)
val OnSurfaceDark = Color(0xFFF1F5F9)

// Glow & Gradients
val GradientStart = SwoBlue
val GradientEnd = SwoLightBlue
val GlowColor = Color(0x663B82F6)
