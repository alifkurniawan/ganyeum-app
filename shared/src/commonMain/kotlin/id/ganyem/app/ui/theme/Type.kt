package id.ganyem.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import ganyem.shared.generated.resources.Res
import ganyem.shared.generated.resources.inter
import org.jetbrains.compose.resources.Font as ComposeResourceFont

@androidx.compose.runtime.Composable
fun interFontFamily() = FontFamily(
    ComposeResourceFont(Res.font.inter, FontWeight.Normal),
    ComposeResourceFont(Res.font.inter, FontWeight.Medium),
    ComposeResourceFont(Res.font.inter, FontWeight.SemiBold),
    ComposeResourceFont(Res.font.inter, FontWeight.Bold)
)

@androidx.compose.runtime.Composable
fun ganyemTypography() = Typography(fontFamily = interFontFamily())
