package com.bgsm.app.ui.auth

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgsm.app.ui.components.BgsmLogo
import com.bgsm.app.ui.components.BgsmSecondaryButton
import com.bgsm.app.ui.components.BgsmStatusBanner
import com.bgsm.app.ui.theme.BgsmAccentCyan
import com.bgsm.app.ui.theme.BgsmAccentLime
import com.bgsm.app.ui.theme.BgsmBackground
import com.bgsm.app.ui.theme.BgsmBorder
import com.bgsm.app.ui.theme.BgsmSurface
import com.bgsm.app.ui.theme.BgsmSurfaceElevated
import com.bgsm.app.ui.theme.BgsmTextMuted
import com.bgsm.app.ui.theme.BgsmTextPrimary
import com.bgsm.app.ui.theme.BgsmTextSecondary
import com.bgsm.app.viewmodel.AuthUiState
import com.bgsm.app.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by authViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Login, 1 = Register
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPasswordEmail by remember { mutableStateOf("") }

    val isLoading = uiState is AuthUiState.Loading
    val errorMessage = (uiState as? AuthUiState.Error)?.message
    val successMessage = (uiState as? AuthUiState.PasswordResetSent)?.let {
        "Password reset email sent to ${it.email}. Check your inbox."
    }

    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(
            initialEmail = forgotPasswordEmail,
            onDismiss = { showForgotPasswordDialog = false },
            onSubmit = { email ->
                showForgotPasswordDialog = false
                authViewModel.sendPasswordReset(email)
            }
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BgsmBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Header & Branding
            BgsmLogo(size = 72.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "BGSM",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = BgsmTextPrimary,
                letterSpacing = 1.sp
            )

            Text(
                text = "Battleground Social Media",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = BgsmAccentLime,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Status Banners
            if (!errorMessage.isNullOrBlank()) {
                BgsmStatusBanner(
                    message = errorMessage,
                    isError = true,
                    onDismiss = { authViewModel.clearState() },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (!successMessage.isNullOrBlank()) {
                BgsmStatusBanner(
                    message = successMessage,
                    isError = false,
                    onDismiss = { authViewModel.clearState() },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Google Sign-In Option
            BgsmSecondaryButton(
                text = "Continue with Google",
                onClick = {
                    (context as? Activity)?.let { activity ->
                        authViewModel.signInWithGoogle(activity)
                    }
                },
                isLoading = isLoading && (uiState as? AuthUiState.Loading)?.message?.contains("Google") == true,
                leadingIcon = Icons.Default.AccountCircle,
                testTag = "google_sign_in_button"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = BgsmBorder
                )
                Text(
                    text = "OR EMAIL",
                    color = BgsmTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = BgsmBorder
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Auth Tab Switcher
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BgsmSurface)
                    .border(1.dp, BgsmBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTab == 0) BgsmSurfaceElevated else Color.Transparent)
                            .clickable {
                                selectedTab = 0
                                authViewModel.clearState()
                            }
                            .padding(vertical = 10.dp)
                            .testTag("tab_sign_in"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sign In",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp,
                            color = if (selectedTab == 0) BgsmAccentLime else BgsmTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTab == 1) BgsmSurfaceElevated else Color.Transparent)
                            .clickable {
                                selectedTab = 1
                                authViewModel.clearState()
                            }
                            .padding(vertical = 10.dp)
                            .testTag("tab_register"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Register",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp,
                            color = if (selectedTab == 1) BgsmAccentLime else BgsmTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Animated Tab Content
            AnimatedContent(
                targetState = selectedTab,
                label = "AuthTabs"
            ) { tab ->
                if (tab == 0) {
                    LoginScreen(
                        isLoading = isLoading,
                        onLogin = { email, password ->
                            authViewModel.login(email, password)
                        },
                        onForgotPassword = { email ->
                            forgotPasswordEmail = email
                            showForgotPasswordDialog = true
                        }
                    )
                } else {
                    RegisterScreen(
                        isLoading = isLoading,
                        onRegister = { name, email, password, confirm ->
                            authViewModel.register(name, email, password, confirm)
                        }
                    )
                }
            }
        }
    }
}
