package com.itsmcodez.vueplay.ui.component.vueui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object VueColors {

    @Composable
    fun materialColorScheme() = MaterialTheme.colorScheme
    @Composable
    fun MaterialTheme.semantics() = Semantics

    object VuePlatformsBrand {
        val Sapphire = Color(0xFF5170FF)
        val Coral = Color(0xFFFF5757)
    }

    object Semantics {

        val Success = Color(0xFF34C759)
        val Warning = Color(0xFFFF9F0A)
        val Info = Color(0xFF0A84FF)

        val SuccessContainer = Color(0xFFEAF8EF)
        val WarningContainer = Color(0xFFFFF5E6)
        val InfoContainer = Color(0xFFEAF3FF)

        val SuccessOnContainer = Color(0xFF185C37)
        val WarningOnContainer = Color(0xFF6A4300)
        val InfoOnContainer = Color(0xFF1D4ED8)
    }

    val LightColorScheme = lightColorScheme(

        // Brand
        primary = Color(0xFF5170FF),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFEEF2FF),
        onPrimaryContainer = Color(0xFF1B2C7A),

        secondary = Color(0xFF5170FF),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFF5F6F8),
        onSecondaryContainer = Color(0xFF111111),

        // Coral
        tertiary = Color(0xFFFF5757),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFFF0F0),
        onTertiaryContainer = Color(0xFF742929),

        // Error
        error = Color(0xFFFF3B30),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFECEB),
        onErrorContainer = Color(0xFF6D1A16),

        // Backgrounds
        background = Color(0xFFFAFAFB),
        onBackground = Color(0xFF111111),

        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF111111),

        surfaceVariant = Color(0xFFF5F6F8),
        onSurfaceVariant = Color(0xFF6B7280),

        surfaceTint = Color(0xFF5170FF),

        inverseSurface = Color(0xFF1E1E20),
        inverseOnSurface = Color(0xFFFFFFFF),
        inversePrimary = Color(0xFF8FA6FF),

        outline = Color(0xFFE3E5E8),
        outlineVariant = Color(0xFFECEDEF),

        scrim = Color(0x80000000),

        // Surface Containers (Material 3 expressive)
        surfaceDim = Color(0xFFF1F2F5),
        surfaceBright = Color(0xFFFFFFFF),

        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFFCFCFD),
        surfaceContainer = Color(0xFFF7F8FA),
        surfaceContainerHigh = Color(0xFFF3F4F6),
        surfaceContainerHighest = Color(0xFFF1F2F5)
    )

    val DarkColorScheme = darkColorScheme(

        // Brand
        primary = Color(0xFF6E88FF),
        onPrimary = Color(0xFF08154A),
        primaryContainer = Color(0xFF223597),
        onPrimaryContainer = Color(0xFFDCE5FF),

        secondary = Color(0xFF6E88FF),
        onSecondary = Color(0xFF08154A),
        secondaryContainer = Color(0xFF2B2B2E),
        onSecondaryContainer = Color(0xFFFFFFFF),

        // Coral
        tertiary = Color(0xFFFF7272),
        onTertiary = Color(0xFF551717),
        tertiaryContainer = Color(0xFF6A2525),
        onTertiaryContainer = Color(0xFFFFDCDC),

        // Error
        error = Color(0xFFFF6B60),
        onError = Color(0xFF4D0F0B),
        errorContainer = Color(0xFF6D1A16),
        onErrorContainer = Color(0xFFFFDAD6),

        // Backgrounds
        background = Color(0xFF000000),
        onBackground = Color(0xFFFFFFFF),

        surface = Color(0xFF161616),
        onSurface = Color(0xFFFFFFFF),

        surfaceVariant = Color(0xFF2B2B2E),
        onSurfaceVariant = Color(0xFFA1A1AA),

        surfaceTint = Color(0xFF6E88FF),

        inverseSurface = Color(0xFFF7F8FA),
        inverseOnSurface = Color(0xFF111111),
        inversePrimary = Color(0xFF5170FF),

        outline = Color(0xFF3A3A3E),
        outlineVariant = Color(0xFF2F2F33),

        scrim = Color(0x99000000),

        // Surface Containers (Material 3 expressive)
        surfaceDim = Color(0xFF111111),
        surfaceBright = Color(0xFF242426),

        surfaceContainerLowest = Color(0xFF0B0B0C),
        surfaceContainerLow = Color(0xFF141416),
        surfaceContainer = Color(0xFF1A1A1C),
        surfaceContainerHigh = Color(0xFF212123),
        surfaceContainerHighest = Color(0xFF2B2B2E)
    )
}

object VueSpacing {
    val thin = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
    val huge = 48.dp
}

object VueElevation {
    val None = 0.dp
    val Low = 1.dp
    val Medium = 3.dp
    val High = 6.dp
}


