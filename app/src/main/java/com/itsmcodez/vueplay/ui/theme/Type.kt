package com.itsmcodez.vueplay.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.itsmcodez.vueplay.R

/*val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val FredokaFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Fredoka"),
        fontProvider = provider,
    )
)

val FredokaFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Fredoka"),
        fontProvider = provider,
    )
)*/

val FredokaFontFamily = FontFamily(
    Font(R.font.fredoka_regular, FontWeight.Normal),
    Font(R.font.fredoka_light, FontWeight.Light),
    Font(R.font.fredoka_medium, FontWeight.Medium),
    Font(R.font.fredoka_semi_bold, FontWeight.SemiBold),
    Font(R.font.fredoka_bold, FontWeight.Bold)
)

// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = FredokaFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = FredokaFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = FredokaFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = FredokaFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = FredokaFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = FredokaFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = FredokaFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = FredokaFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = FredokaFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = FredokaFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = FredokaFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = FredokaFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = FredokaFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = FredokaFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = FredokaFontFamily),
)

