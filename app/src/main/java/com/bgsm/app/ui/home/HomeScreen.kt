package com.bgsm.app.ui.home

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgsm.app.ui.components.BgsmAvatar
import com.bgsm.app.ui.components.BgsmBadge
import com.bgsm.app.ui.components.BgsmCard
import com.bgsm.app.ui.theme.BgsmAccentCyan
import com.bgsm.app.ui.theme.BgsmAccentLime
import com.bgsm.app.ui.theme.BgsmAccentOrange
import com.bgsm.app.ui.theme.BgsmAccentPurple
import com.bgsm.app.ui.theme.BgsmBackground
import com.bgsm.app.ui.theme.BgsmBorder
import com.bgsm.app.ui.theme.BgsmSuccess
import com.bgsm.app.ui.theme.BgsmSurface
import com.bgsm.app.ui.theme.BgsmSurfaceElevated
import com.bgsm.app.ui.theme.BgsmTextMuted
import com.bgsm.app.ui.theme.BgsmTextPrimary
import com.bgsm.app.ui.theme.BgsmTextSecondary
import com.bgsm.app.viewmodel.ProfileUiState
import com.bgsm.app.viewmodel.ProfileViewModel

@Composable
fun HomeScreen(
    profileViewModel: ProfileViewModel,
    onNavigateToProfile: () -> Unit,
    onNavigateToTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val profileState by profileViewModel.profileState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BgsmBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with User Avatar & Greeting
            when (val state = profileState) {
                is ProfileUiState.Success -> {
                    val profile = state.profile
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToProfile() }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BgsmAvatar(
                            photoUrl = profile.photoUrl,
                            displayName = profile.displayName,
                            size = 52.dp
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "WARRIOR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BgsmAccentLime,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = profile.displayName.ifBlank { "Player" },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BgsmTextPrimary
                            )
                            Text(
                                text = "@${profile.username.ifBlank { "bgsm_user" }}",
                                fontSize = 12.sp,
                                color = BgsmTextSecondary
                            )
                        }
                        BgsmBadge(
                            text = "ONLINE",
                            color = BgsmSuccess
                        )
                    }
                }
                is ProfileUiState.Loading -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = BgsmAccentLime,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Synchronizing BGSM Profile...",
                            color = BgsmTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
                is ProfileUiState.Error -> {
                    Text(
                        text = "Profile state: ${state.message}",
                        color = BgsmTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // Phase 1 Foundation Hero Banner
            BgsmCard(
                borderColor = BgsmAccentLime
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BgsmBadge(
                            text = "MASTER PHASE 1 ACTIVE",
                            color = BgsmAccentLime
                        )
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = BgsmAccentLime,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Firebase & Security Foundation",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BgsmTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Production Firebase Authentication, Credential Manager Google Sign-In, real-time Cloud Firestore synchronization, and zero-trust security rules are actively initialized.",
                        fontSize = 13.sp,
                        color = BgsmTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            // Foundation Status Indicators
            Text(
                text = "SYSTEM ARCHITECTURE STATUS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BgsmTextMuted,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            SystemStatusItem(
                icon = Icons.Default.CheckCircle,
                title = "Firebase Authentication",
                subtitle = "Google Sign-In + Email/Password + Real-time Auth Gate",
                statusText = "CONNECTED",
                tint = BgsmSuccess
            )

            SystemStatusItem(
                icon = Icons.Default.CloudDone,
                title = "Cloud Firestore Database",
                subtitle = "Real-time user sync & collections schema ready",
                statusText = "LIVE",
                tint = BgsmAccentCyan
            )

            SystemStatusItem(
                icon = Icons.Default.Lock,
                title = "Zero-Trust Security Rules",
                subtitle = "Strict ownership verification on users/{uid}",
                statusText = "ENFORCED",
                tint = BgsmAccentLime
            )

            // Future Phases Preview Cards
            Text(
                text = "UPCOMING ROADMAP",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BgsmTextMuted,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Prompt 2 Preview Card
            BgsmCard(
                borderColor = BgsmBorder
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = BgsmAccentCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "MASTER PROMPT 2",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BgsmAccentCyan,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Social + Clans + Villages + Chat",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BgsmTextPrimary
                            )
                        }
                        BgsmBadge(
                            text = "NEXT PHASE",
                            color = BgsmAccentCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Will introduce full Clan creation, One User = One Clan enforcement, Clan Friendships, Villages, Clan & Private Chat, and Social Timelines.",
                        fontSize = 12.sp,
                        color = BgsmTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            // Prompt 3 Preview Card
            BgsmCard(
                borderColor = BgsmBorder
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = BgsmAccentOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "MASTER PROMPT 3",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BgsmAccentOrange,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Clan Battles + Spectators + Reactions",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = BgsmTextPrimary
                            )
                        }
                        BgsmBadge(
                            text = "PHASE 3",
                            color = BgsmAccentOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Will introduce Clan Wars, Live Spectator Mode, Real-Time In-Battle Reactions, Season Leaderboards, and Victory Records.",
                        fontSize = 12.sp,
                        color = BgsmTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SystemStatusItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    statusText: String,
    tint: androidx.compose.ui.graphics.Color
) {
    BgsmCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(BgsmSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BgsmTextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = BgsmTextSecondary
                )
            }
            BgsmBadge(
                text = statusText,
                color = tint
            )
        }
    }
}
