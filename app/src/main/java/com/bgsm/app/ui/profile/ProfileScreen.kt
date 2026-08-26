package com.bgsm.app.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgsm.app.data.model.UserProfile
import com.bgsm.app.ui.components.BgsmAvatar
import com.bgsm.app.ui.components.BgsmBadge
import com.bgsm.app.ui.components.BgsmButton
import com.bgsm.app.ui.components.BgsmCard
import com.bgsm.app.ui.components.BgsmSecondaryButton
import com.bgsm.app.ui.components.BgsmTextField
import com.bgsm.app.ui.theme.BgsmAccentCyan
import com.bgsm.app.ui.theme.BgsmAccentLime
import com.bgsm.app.ui.theme.BgsmAccentOrange
import com.bgsm.app.ui.theme.BgsmBackground
import com.bgsm.app.ui.theme.BgsmBorder
import com.bgsm.app.ui.theme.BgsmError
import com.bgsm.app.ui.theme.BgsmSurface
import com.bgsm.app.ui.theme.BgsmSurfaceElevated
import com.bgsm.app.ui.theme.BgsmTextMuted
import com.bgsm.app.ui.theme.BgsmTextPrimary
import com.bgsm.app.ui.theme.BgsmTextSecondary
import com.bgsm.app.viewmodel.AuthViewModel
import com.bgsm.app.viewmodel.ProfileUiState
import com.bgsm.app.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profileState by profileViewModel.profileState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BgsmBackground
    ) {
        when (val state = profileState) {
            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = BgsmAccentLime)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Syncing Profile from Firestore...",
                            color = BgsmTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Profile Sync Error",
                            color = BgsmError,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            color = BgsmTextSecondary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        BgsmButton(
                            text = "Retry Sync",
                            onClick = { profileViewModel.observeCurrentProfile() }
                        )
                    }
                }
            }

            is ProfileUiState.Success -> {
                val profile = state.profile

                if (showEditDialog) {
                    EditProfileDialog(
                        profile = profile,
                        onDismiss = { showEditDialog = false },
                        onSave = { name, username, bio, interests ->
                            showEditDialog = false
                            profileViewModel.updateProfile(name, username, bio, interests)
                        }
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Header Card
                    BgsmCard(
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = { showEditDialog = true },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(BgsmSurfaceElevated)
                                        .testTag("edit_profile_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Profile",
                                        tint = BgsmAccentLime,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            BgsmAvatar(
                                photoUrl = profile.photoUrl,
                                displayName = profile.displayName,
                                size = 84.dp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = profile.displayName.ifBlank { "Unknown Warrior" },
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = BgsmTextPrimary
                            )

                            Text(
                                text = "@${profile.username.ifBlank { "warrior" }}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = BgsmAccentCyan
                            )

                            if (profile.bio.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = profile.bio,
                                    fontSize = 14.sp,
                                    color = BgsmTextSecondary,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    // Clan Status Card (One User = One Active Clan Foundation)
                    BgsmCard(
                        modifier = Modifier.padding(bottom = 16.dp),
                        borderColor = if (profile.activeClanId != null) BgsmAccentLime else BgsmBorder
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = if (profile.activeClanId != null) BgsmAccentLime else BgsmTextMuted,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ACTIVE CLAN STATUS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BgsmTextMuted,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = if (profile.activeClanId != null) "Clan ID: ${profile.activeClanId}" else "No Active Clan (Prompt 2 Ready)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (profile.activeClanId != null) BgsmTextPrimary else BgsmTextSecondary
                                )
                            }
                            BgsmBadge(
                                text = if (profile.activeClanId != null) "MEMBER" else "FREE AGENT",
                                color = if (profile.activeClanId != null) BgsmAccentLime else BgsmTextSecondary
                            )
                        }
                    }

                    // Gaming Interests Tag Card
                    BgsmCard(
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SportsEsports,
                                    contentDescription = null,
                                    tint = BgsmAccentLime,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "GAMING INTERESTS",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BgsmTextPrimary,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (profile.interests.isNotEmpty()) {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    profile.interests.forEach { tag ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(BgsmSurfaceElevated)
                                                .border(1.dp, BgsmBorder, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = tag,
                                                color = BgsmAccentLime,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "No interests selected. Tap edit to customize.",
                                    color = BgsmTextMuted,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    // Account Identity & Metadata Card
                    BgsmCard(
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "FIREBASE IDENTITY",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BgsmTextPrimary,
                                letterSpacing = 0.5.sp
                            )

                            // Permanent UID
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    tint = BgsmAccentCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Permanent UID (Source of Truth)",
                                        fontSize = 11.sp,
                                        color = BgsmTextMuted
                                    )
                                    Text(
                                        text = profile.uid,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = BgsmTextPrimary
                                    )
                                }
                            }

                            // Email
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = BgsmAccentCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Email Address",
                                        fontSize = 11.sp,
                                        color = BgsmTextMuted
                                    )
                                    Text(
                                        text = profile.email.ifBlank { "Not provided" },
                                        fontSize = 13.sp,
                                        color = BgsmTextPrimary
                                    )
                                }
                            }

                            // Joined Date
                            val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }
                            val dateString = profile.createdAt?.toDate()?.let { dateFormat.format(it) } ?: "Just now"

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = BgsmAccentCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Joined BGSM Platform",
                                        fontSize = 11.sp,
                                        color = BgsmTextMuted
                                    )
                                    Text(
                                        text = dateString,
                                        fontSize = 13.sp,
                                        color = BgsmTextPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Sign Out Button
                    BgsmSecondaryButton(
                        text = "Sign Out",
                        onClick = {
                            authViewModel.signOut(context)
                        },
                        leadingIcon = Icons.AutoMirrored.Filled.Logout,
                        testTag = "profile_sign_out_button"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileDialog(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (displayName: String, username: String, bio: String, interests: List<String>) -> Unit
) {
    var displayName by remember { mutableStateOf(profile.displayName) }
    var username by remember { mutableStateOf(profile.username) }
    var bio by remember { mutableStateOf(profile.bio) }
    var interestInput by remember { mutableStateOf("") }
    val interests = remember { mutableStateListOf<String>().apply { addAll(profile.interests) } }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BgsmSurfaceElevated,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Edit Profile",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = BgsmTextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BgsmTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = "Display Name",
                    testTag = "edit_display_name_input"
                )

                BgsmTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "Username Handle",
                    testTag = "edit_username_input"
                )

                BgsmTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = "Bio",
                    testTag = "edit_bio_input"
                )

                Text(
                    text = "Interests / Game Tags",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BgsmTextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BgsmTextField(
                        value = interestInput,
                        onValueChange = { interestInput = it },
                        label = "Add Tag",
                        modifier = Modifier.weight(1f),
                        testTag = "add_interest_tag_input"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (interestInput.isNotBlank() && !interests.contains(interestInput.trim())) {
                                interests.add(interestInput.trim())
                                interestInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BgsmAccentLime)
                            .testTag("add_interest_tag_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Tag",
                            tint = BgsmBackground
                        )
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    interests.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(BgsmSurface)
                                .border(1.dp, BgsmBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = tag,
                                    color = BgsmAccentLime,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove Tag",
                                    tint = BgsmTextMuted,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { interests.remove(tag) }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            BgsmButton(
                text = "Save Changes",
                onClick = {
                    onSave(displayName, username, bio, interests.toList())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                testTag = "save_profile_button"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = BgsmTextMuted,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}
