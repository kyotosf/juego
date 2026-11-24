package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val SpiderLoveColorScheme = darkColorScheme(
    primary = SpiderCrimson,
    onPrimary = CrispInkWhite,
    primaryContainer = VelvetRose,
    onPrimaryContainer = CrispInkWhite,
    secondary = ElectricWebCyan,
    onSecondary = MidnightNavy,
    secondaryContainer = ElevatedCardIndigo,
    onSecondaryContainer = ElectricWebCyan,
    tertiary = GoldenSunset,
    onTertiary = MidnightNavy,
    background = MidnightNavy,
    onBackground = CrispInkWhite,
    surface = DeepIndigoSurface,
    onSurface = CrispInkWhite,
    surfaceVariant = ElevatedCardIndigo,
    onSurfaceVariant = MutedMoonlight,
    outline = OutlineIndigo,
    error = CrimsonError,
    onError = CrispInkWhite
)

private val SpiderLoveShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun SpiderLoveTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SpiderLoveColorScheme,
        typography = SpiderLoveTypography,
        shapes = SpiderLoveShapes,
        content = content
    )
}
