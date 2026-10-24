package com.felix.bestfelix.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Indigo40,
    onPrimary = Color.White,
    primaryContainer = Indigo90,
    onPrimaryContainer = Indigo10,
    inversePrimary = Indigo80,
    secondary = Gold40,
    onSecondary = Color.White,
    secondaryContainer = Gold95,
    onSecondaryContainer = Gold20,
    tertiary = Green40,
    onTertiary = Color.White,
    tertiaryContainer = Green90,
    onTertiaryContainer = Green20,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red20,
    background = NeutralLightBackground,
    onBackground = NeutralLightOnSurface,
    surface = NeutralLightSurface,
    onSurface = NeutralLightOnSurface,
    surfaceVariant = NeutralLightSurfaceVariant,
    onSurfaceVariant = NeutralLightOnSurfaceVariant,
    surfaceContainer = Indigo95,
    surfaceContainerHigh = Color(0xFFEFEEF9),
    outline = NeutralLightOutline,
    outlineVariant = Color(0xFFE2E1EC),
)

private val DarkColors = darkColorScheme(
    primary = Indigo80,
    onPrimary = Indigo20,
    primaryContainer = Indigo30,
    onPrimaryContainer = Indigo90,
    inversePrimary = Indigo40,
    secondary = Gold80,
    onSecondary = Gold20,
    secondaryContainer = Gold30,
    onSecondaryContainer = Gold90,
    tertiary = Green70,
    onTertiary = Green20,
    tertiaryContainer = Green30,
    onTertiaryContainer = Green90,
    error = Red70,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = NeutralDarkBackground,
    onBackground = NeutralDarkOnSurface,
    surface = NeutralDarkSurface,
    onSurface = NeutralDarkOnSurface,
    surfaceVariant = NeutralDarkSurfaceVariant,
    onSurfaceVariant = NeutralDarkOnSurfaceVariant,
    surfaceContainer = NeutralDarkSurfaceHigh,
    surfaceContainerHigh = Color(0xFF262B38),
    outline = NeutralDarkOutline,
    outlineVariant = Color(0xFF353A47),
)

/** Dynamic color volontairement désactivé : l'identité indigo/or reste la même sur tout téléphone. */
@Composable
fun BestFelixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val accents = if (darkTheme) DarkAccents else LightAccents

    CompositionLocalProvider(LocalAccents provides accents) {
        MaterialTheme(
            colorScheme = colors,
            typography = BestFelixTypography,
            content = content,
        )
    }
}

object BestFelixTheme {
    val accents: Accents
        @Composable @ReadOnlyComposable
        get() = LocalAccents.current
}
