package com.bgsm.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bgsm.app.ui.theme.BgsmAccentCyan
import com.bgsm.app.ui.theme.BgsmAccentLime
import com.bgsm.app.ui.theme.BgsmAccentOrange
import com.bgsm.app.ui.theme.BgsmBackground
import com.bgsm.app.ui.theme.BgsmBorder
import com.bgsm.app.ui.theme.BgsmBorderFocused
import com.bgsm.app.ui.theme.BgsmError
import com.bgsm.app.ui.theme.BgsmErrorBackground
import com.bgsm.app.ui.theme.BgsmSuccess
import com.bgsm.app.ui.theme.BgsmSuccessBackground
import com.bgsm.app.ui.theme.BgsmSurface
import com.bgsm.app.ui.theme.BgsmSurfaceElevated
import com.bgsm.app.ui.theme.BgsmTextMuted
import com.bgsm.app.ui.theme.BgsmTextPrimary
import com.bgsm.app.ui.theme.BgsmTextSecondary

@Composable
fun BgsmLogo(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(BgsmSurfaceElevated, BgsmSurface)
                )
            )
            .border(
                BorderStroke(2.dp, Brush.linearGradient(listOf(BgsmAccentLime, BgsmAccentCyan))),
                RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "BGSM Shield",
                tint = BgsmAccentLime,
                modifier = Modifier.size(size * 0.45f)
            )
            Text(
                text = "BGSM",
                fontWeight = FontWeight.Black,
                fontSize = (size.value * 0.22f).sp,
                color = BgsmTextPrimary,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun BgsmButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    testTag: String = "bgsm_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BgsmAccentLime,
            contentColor = BgsmBackground,
            disabledContainerColor = BgsmSurfaceElevated,
            disabledContentColor = BgsmTextMuted
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = BgsmBackground,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (enabled) BgsmBackground else BgsmTextMuted
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun BgsmSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    testTag: String = "bgsm_secondary_button"
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (enabled) BgsmBorder else BgsmSurfaceElevated),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = BgsmSurfaceElevated,
            contentColor = BgsmTextPrimary,
            disabledContainerColor = BgsmSurface,
            disabledContentColor = BgsmTextMuted
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = BgsmTextPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (enabled) BgsmAccentCyan else BgsmTextMuted
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = if (enabled) BgsmTextPrimary else BgsmTextMuted
                )
            }
        }
    }
}

@Composable
fun BgsmTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    errorMessage: String? = null,
    testTag: String = "bgsm_input"
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (isError) BgsmError else BgsmTextSecondary
                    )
                }
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.testTag("${testTag}_toggle_visibility")
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = BgsmTextSecondary
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BgsmSurfaceElevated,
                unfocusedContainerColor = BgsmSurface,
                errorContainerColor = BgsmSurfaceElevated,
                focusedBorderColor = BgsmBorderFocused,
                unfocusedBorderColor = BgsmBorder,
                errorBorderColor = BgsmError,
                focusedLabelColor = BgsmAccentLime,
                unfocusedLabelColor = BgsmTextSecondary,
                cursorColor = BgsmAccentLime,
                focusedTextColor = BgsmTextPrimary,
                unfocusedTextColor = BgsmTextPrimary,
                errorTextColor = BgsmTextPrimary
            )
        )
        if (isError && !errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                color = BgsmError,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun BgsmAvatar(
    photoUrl: String?,
    displayName: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(BgsmSurfaceElevated)
            .border(BorderStroke(1.5.dp, BgsmAccentLime), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!photoUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "User Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
            )
        } else {
            // Built-in fallback vector avatar
            val initial = displayName.trim().take(1).uppercase()
            if (initial.isNotBlank()) {
                Text(
                    text = initial,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.45f).sp,
                    color = BgsmAccentLime
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Default Avatar",
                    tint = BgsmAccentLime,
                    modifier = Modifier.size(size * 0.6f)
                )
            }
        }
    }
}

@Composable
fun BgsmCard(
    modifier: Modifier = Modifier,
    borderColor: Color = BgsmBorder,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = BgsmSurface,
        content = content
    )
}

@Composable
fun BgsmStatusBanner(
    message: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    AnimatedVisibility(
        visible = message.isNotBlank(),
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        val bgColor = if (isError) BgsmErrorBackground else BgsmSuccessBackground
        val borderColor = if (isError) BgsmError else BgsmSuccess
        val icon = if (isError) Icons.Default.Error else Icons.Default.CheckCircle

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(bgColor)
                .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(12.dp))
                .clickable(enabled = onDismiss != null) { onDismiss?.invoke() }
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = borderColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = message,
                    color = BgsmTextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun BgsmBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = BgsmAccentLime,
    backgroundColor: Color = BgsmSurfaceElevated
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(BorderStroke(1.dp, color.copy(alpha = 0.5f)), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
