package com.bgsm.app.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgsm.app.ui.components.BgsmButton
import com.bgsm.app.ui.components.BgsmTextField
import com.bgsm.app.ui.theme.BgsmAccentCyan
import com.bgsm.app.ui.theme.BgsmTextSecondary

@Composable
fun LoginScreen(
    isLoading: Boolean,
    onLogin: (String, String) -> Unit,
    onForgotPassword: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    fun validateAndSubmit() {
        var isValid = true
        if (email.isBlank() || !email.contains("@")) {
            emailError = "Please enter a valid email address"
            isValid = false
        } else {
            emailError = null
        }

        if (password.isBlank()) {
            passwordError = "Password cannot be empty"
            isValid = false
        } else {
            passwordError = null
        }

        if (isValid) {
            focusManager.clearFocus()
            onLogin(email.trim(), password)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BgsmTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
            },
            label = "Email Address",
            leadingIcon = Icons.Default.Email,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            isError = emailError != null,
            errorMessage = emailError,
            testTag = "login_email_input"
        )

        BgsmTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            label = "Password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { validateAndSubmit() }
            ),
            isError = passwordError != null,
            errorMessage = passwordError,
            testTag = "login_password_input"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Forgot password?",
                color = BgsmAccentCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable { onForgotPassword(email.trim()) }
                    .padding(vertical = 4.dp, horizontal = 2.dp)
                    .testTag("forgot_password_link")
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        BgsmButton(
            text = "Sign In",
            onClick = { validateAndSubmit() },
            isLoading = isLoading,
            testTag = "sign_in_submit_button"
        )
    }
}
