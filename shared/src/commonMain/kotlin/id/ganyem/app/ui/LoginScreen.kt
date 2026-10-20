package id.ganyem.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ganyem.shared.generated.resources.Res
import ganyem.shared.generated.resources.login_brand
import ganyem.shared.generated.resources.login_description
import ganyem.shared.generated.resources.login_error_fallback
import ganyem.shared.generated.resources.login_footer
import ganyem.shared.generated.resources.login_google_sign_in
import ganyem.shared.generated.resources.login_hero
import ganyem.shared.generated.resources.login_hero_badge
import ganyem.shared.generated.resources.login_hero_content_description
import ganyem.shared.generated.resources.login_legal_disclaimer
import ganyem.shared.generated.resources.login_loading
import ganyem.shared.generated.resources.login_security_info
import ganyem.shared.generated.resources.login_title
import id.ganyem.app.auth.AppTokens
import id.ganyem.app.auth.AuthRepository
import id.ganyem.app.ui.theme.GanyemColors
import id.ganyem.app.ui.theme.GanyemTheme
import id.ganyem.app.ui.theme.GoogleBrandColors
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(
    repository: AuthRepository,
    onLoginSuccess: (AppTokens) -> Unit
) {
    val scope = rememberCoroutineScope()
    val loginErrorFallback = stringResource(Res.string.login_error_fallback)
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LoginScreenContent(
        loading = loading,
        errorMessage = errorMessage,
        onLogin = {
            scope.launch {
                loading = true
                errorMessage = null
                try {
                    onLoginSuccess(repository.login())
                } catch (e: Exception) {
                    errorMessage = e.message ?: loginErrorFallback
                } finally {
                    loading = false
                }
            }
        }
    )
}

@Composable
private fun LoginScreenContent(
    loading: Boolean,
    errorMessage: String?,
    onLogin: () -> Unit
) {
    val loginFontFamily = MaterialTheme.typography.bodyMedium.fontFamily ?: FontFamily.Default
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .background(GanyemColors.loginBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 0.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Row(
            modifier = Modifier.height(72.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(GanyemColors.primary),
                contentAlignment = Alignment.Center
            ) {
                Text("❉", color = GanyemColors.applicationBackground, fontSize = 25.sp)
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(Res.string.login_brand),
                color = GanyemColors.primaryText,
                fontFamily = loginFontFamily,
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Image(
                painter = painterResource(Res.drawable.login_hero),
                contentDescription = stringResource(Res.string.login_hero_content_description),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Surface(
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                shape = RoundedCornerShape(50),
                color = GanyemColors.applicationBackground
            ) {
                Text(
                    text = stringResource(Res.string.login_hero_badge),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    color = GanyemColors.primary,
                    fontFamily = loginFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(26.dp))
        Text(
            text = stringResource(Res.string.login_title),
            color = GanyemColors.primaryText,
            fontFamily = loginFontFamily,
            fontSize = 34.sp,
            lineHeight = 39.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(Res.string.login_description),
            color = GanyemColors.secondaryText,
            fontFamily = loginFontFamily,
            fontSize = 14.sp,
            lineHeight = 21.sp
        )
        Spacer(Modifier.height(26.dp))

        LoginActionButton(
            label = stringResource(if (loading) Res.string.login_loading else Res.string.login_google_sign_in),
            loading = loading,
            enabled = !loading,
            fontFamily = loginFontFamily,
            onClick = onLogin
        )

        errorMessage?.let { message ->
            Spacer(Modifier.height(10.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                fontFamily = loginFontFamily,
                fontSize = 13.sp
            )
        }

        Spacer(Modifier.height(14.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = GanyemColors.softPrimarySurface
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("♧", color = GanyemColors.primary, fontSize = 19.sp)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = stringResource(Res.string.login_security_info),
                    color = GanyemColors.primary,
                    fontFamily = loginFontFamily,
                    fontSize = 11.sp,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(Modifier.height(26.dp))
        Text(
            text = stringResource(Res.string.login_legal_disclaimer),
            modifier = Modifier.fillMaxWidth(),
            color = GanyemColors.secondaryText,
            fontFamily = loginFontFamily,
            fontSize = 10.sp,
            lineHeight = 15.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(Res.string.login_footer),
            modifier = Modifier.fillMaxWidth(),
            color = GanyemColors.primary,
            fontFamily = loginFontFamily,
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    GanyemTheme {
        LoginScreenContent(loading = false, errorMessage = null, onLogin = {})
    }
}

@Composable
private fun LoginActionButton(
    label: String,
    loading: Boolean,
    enabled: Boolean,
    fontFamily: FontFamily,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GanyemColors.border),
        colors = ButtonDefaults.buttonColors(
            containerColor = GanyemColors.applicationBackground,
            contentColor = GanyemColors.primaryText,
            disabledContainerColor = GanyemColors.applicationBackground,
            disabledContentColor = GanyemColors.primaryText
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(19.dp),
                color = GanyemColors.primary,
                strokeWidth = 2.dp
            )
            Spacer(Modifier.width(10.dp))
        } else {
            Text("G", color = GoogleBrandColors.blue, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(10.dp))
        }
        Text(label, fontFamily = fontFamily, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}
