package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = TextDark,
    secondary = SilverSecondary,
    onSecondary = SlateObsidian,
    background = SlateObsidian,
    onBackground = TextLight,
    surface = CardSlateDark,
    onSurface = TextLight,
    primaryContainer = GoldDark,
    onPrimaryContainer = TextLight,
    secondaryContainer = SilverDark,
    onSecondaryContainer = TextLight,
    error = RubyRed
)

private val LightColorScheme = lightColorScheme(
    primary = GoldPrimary,
    onPrimary = TextLight,
    secondary = SilverSecondary,
    onSecondary = TextDark,
    background = WarmCream,
    onBackground = TextDark,
    surface = CardWarmWhite,
    onSurface = TextDark,
    primaryContainer = GoldLight,
    onPrimaryContainer = TextDark,
    secondaryContainer = SilverLight,
    onSecondaryContainer = TextDark,
    error = RubyRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to enforce our custom gold & silver branding
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
