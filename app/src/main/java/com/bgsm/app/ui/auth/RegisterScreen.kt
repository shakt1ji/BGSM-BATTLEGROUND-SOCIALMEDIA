package com.bgsm.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bgsm.app.ui.components.BgsmButton
import com.bgsm.app.ui.components.BgsmTextField

@Composable
fun RegisterScreen(
    isLoading: Boolean,
    onRegister: (String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    fun validateAndSubmit() {
        var isValid = true

        if (displayName.isBlank()) {
            nameError = "Display name is required"
            isValid = false
        } else {
            nameError = null
        }

        if (email.isBlank() || !email.contains("@")) {
            emailError = "Please enter a valid email"
            isValid = false
        } else {
            emailError = null
        }

        if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        } else {
            passwordError = null
        }

        if (password != confirmPassword) {
            confirmError = "Passwords do not match"
            isValid = false
        } else {
            confirmError = null
        }

        if (isValid) {
            focusManager.clearFocus()
            onRegister(displayName.trim(), email.trim(), password, confirmPassword)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        BgsmTextField(
            value = displayName,
            onValueChange = {
                displayName = it
                nameError = null
            },
            label = "Display Name (Gamer Tag)",
            leadingIcon = Icons.Default.Person,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            isError = nameError != null,
            errorMessage = nameError,
            testTag = "register_name_input"
        )

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
            testTag = "register_email_input"
        )

        BgsmTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            label = "Password (min 6 chars)",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            isError = passwordError != null,
            errorMessage = passwordError,
            testTag = "register_password_input"
        )

        BgsmTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmError = null
            },
            label = "Confirm Password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { validateAndSubmit() }
            ),
            isError = confirmError != null,
            errorMessage = confirmError,
            testTag = "register_confirm_password_input"
        )

        Spacer(modifier = Modifier.height(4.dp))

        BgsmButton(
            text = "Create Account",
            onClick = { validateAndSubmit() },
            isLoading = isLoading,
            testTag = "create_account_submit_button"
        )
    }
}
