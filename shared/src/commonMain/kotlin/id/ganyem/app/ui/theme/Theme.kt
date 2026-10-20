package id.ganyem.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val GanyemLightColorScheme = lightColorScheme(
    primary = GanyemColors.primary,
    onPrimary = GanyemColors.applicationBackground,
    background = GanyemColors.applicationBackground,
    surface = GanyemColors.applicationBackground,
    onSurface = GanyemColors.primaryText,
    onSurfaceVariant = GanyemColors.secondaryText,
    outline = GanyemColors.border
)

@Composable
fun GanyemTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GanyemLightColorScheme,
        typography = ganyemTypography(),
        content = content
    )
}
