package com.bgsm.app.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgsm.app.ui.components.BgsmButton
import com.bgsm.app.ui.components.BgsmTextField
import com.bgsm.app.ui.theme.BgsmAccentLime
import com.bgsm.app.ui.theme.BgsmSurfaceElevated
import com.bgsm.app.ui.theme.BgsmTextMuted
import com.bgsm.app.ui.theme.BgsmTextPrimary
import com.bgsm.app.ui.theme.BgsmTextSecondary

@Composable
fun ForgotPasswordDialog(
    initialEmail: String = "",
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var email by remember { mutableStateOf(initialEmail) }
    var emailError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = BgsmSurfaceElevated,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = null,
                    tint = BgsmAccentLime
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reset Password",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = BgsmTextPrimary
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Enter your registered email address and we'll send a Firebase password reset link to your inbox.",
                    fontSize = 14.sp,
                    color = BgsmTextSecondary,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                BgsmTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    label = "Email Address",
                    leadingIcon = Icons.Default.Email,
                    isError = emailError != null,
                    errorMessage = emailError,
                    testTag = "forgot_password_email_input"
                )
            }
        },
        confirmButton = {
            BgsmButton(
                text = "Send Reset Link",
                onClick = {
                    if (email.isBlank() || !email.contains("@")) {
                        emailError = "Please enter a valid email address"
                    } else {
                        onSubmit(email.trim())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                testTag = "send_reset_link_button"
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("forgot_password_cancel_button")
            ) {
                Text(
                    text = "Cancel",
                    color = BgsmTextMuted,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}
