package com.bgsm.app.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bgsm.app.ui.auth.AuthScreen
import com.bgsm.app.ui.common.PhasePlaceholderScreen
import com.bgsm.app.ui.home.HomeScreen
import com.bgsm.app.ui.profile.ProfileScreen
import com.bgsm.app.ui.theme.BgsmAccentCyan
import com.bgsm.app.ui.theme.BgsmAccentLime
import com.bgsm.app.ui.theme.BgsmAccentOrange
import com.bgsm.app.ui.theme.BgsmAccentPurple
import com.bgsm.app.ui.theme.BgsmBackground
import com.bgsm.app.ui.theme.BgsmBorder
import com.bgsm.app.ui.theme.BgsmSurface
import com.bgsm.app.ui.theme.BgsmTextMuted
import com.bgsm.app.ui.theme.BgsmTextPrimary
import com.bgsm.app.ui.theme.BgsmTextSecondary
import com.bgsm.app.viewmodel.AuthViewModel
import com.bgsm.app.viewmodel.ProfileViewModel

sealed class BottomNavItem(
    val routeIndex: Int,
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    object Home : BottomNavItem(0, "Home", Icons.Default.Home, "nav_item_home")
    object Discover : BottomNavItem(1, "Discover", Icons.Default.Explore, "nav_item_discover")
    object Clan : BottomNavItem(2, "My Clan", Icons.Default.Group, "nav_item_clan")
    object Chats : BottomNavItem(3, "Chats", Icons.AutoMirrored.Filled.Chat, "nav_item_chats")
    object Battles : BottomNavItem(4, "Battles", Icons.Default.Whatshot, "nav_item_battles")
    object Profile : BottomNavItem(5, "Profile", Icons.Default.Person, "nav_item_profile")
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val currentUser by authViewModel.authStateFlow.collectAsState(initial = authViewModel.currentUser)

    if (currentUser == null) {
        AuthScreen(authViewModel = authViewModel)
    } else {
        MainScaffold(
            authViewModel = authViewModel,
            profileViewModel = profileViewModel
        )
    }
}

@Composable
fun MainScaffold(
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val navItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Discover,
        BottomNavItem.Clan,
        BottomNavItem.Chats,
        BottomNavItem.Battles,
        BottomNavItem.Profile
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BgsmBackground,
        bottomBar = {
            NavigationBar(
                containerColor = BgsmSurface,
                contentColor = BgsmTextPrimary,
                tonalElevation = 8.dp
            ) {
                navItems.forEach { item ->
                    val isSelected = selectedTab == item.routeIndex
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = item.routeIndex },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BgsmBackground,
                            selectedTextColor = BgsmAccentLime,
                            indicatorColor = BgsmAccentLime,
                            unselectedIconColor = BgsmTextMuted,
                            unselectedTextColor = BgsmTextMuted
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = selectedTab,
            label = "TabNavigation",
            modifier = Modifier.padding(innerPadding)
        ) { tab ->
            when (tab) {
                0 -> HomeScreen(
                    profileViewModel = profileViewModel,
                    onNavigateToProfile = { selectedTab = 5 },
                    onNavigateToTab = { selectedTab = it }
                )
                1 -> PhasePlaceholderScreen(
                    title = "Discover & Social Feed",
                    phaseTag = "MASTER PROMPT 2",
                    description = "Global social feed, friend posts, media updates, and trending clans.",
                    icon = Icons.Default.Explore,
                    accentColor = BgsmAccentCyan
                )
                2 -> PhasePlaceholderScreen(
                    title = "Clan & Village Hub",
                    phaseTag = "MASTER PROMPT 2",
                    description = "Clan creation, One User = One Clan membership, Clan Friendships, and Village bases.",
                    icon = Icons.Default.Group,
                    accentColor = BgsmAccentLime
                )
                3 -> PhasePlaceholderScreen(
                    title = "Clan & Private Chats",
                    phaseTag = "MASTER PROMPT 2",
                    description = "Real-time encrypted private DMs and Clan group channels.",
                    icon = Icons.AutoMirrored.Filled.Chat,
                    accentColor = BgsmAccentPurple
                )
                4 -> PhasePlaceholderScreen(
                    title = "Clan Wars & Arena",
                    phaseTag = "MASTER PROMPT 3",
                    description = "Live Clan Battles, Spectator Arena, Real-time In-game Reactions, and Season Leaderboards.",
                    icon = Icons.Default.Whatshot,
                    accentColor = BgsmAccentOrange
                )
                5 -> ProfileScreen(
                    profileViewModel = profileViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
