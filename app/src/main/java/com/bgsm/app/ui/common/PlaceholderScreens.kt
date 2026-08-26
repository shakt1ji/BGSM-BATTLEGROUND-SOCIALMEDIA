package com.bgsm.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgsm.app.ui.components.BgsmBadge
import com.bgsm.app.ui.components.BgsmCard
import com.bgsm.app.ui.theme.BgsmAccentCyan
import com.bgsm.app.ui.theme.BgsmAccentLime
import com.bgsm.app.ui.theme.BgsmAccentOrange
import com.bgsm.app.ui.theme.BgsmAccentPurple
import com.bgsm.app.ui.theme.BgsmBackground
import com.bgsm.app.ui.theme.BgsmSurfaceElevated
import com.bgsm.app.ui.theme.BgsmTextMuted
import com.bgsm.app.ui.theme.BgsmTextPrimary
import com.bgsm.app.ui.theme.BgsmTextSecondary

@Composable
fun PhasePlaceholderScreen(
    title: String,
    phaseTag: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = BgsmBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(BgsmSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            BgsmBadge(
                text = phaseTag,
                color = accentColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = BgsmTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = BgsmTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            BgsmCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Foundation Ready",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BgsmAccentLime
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Firestore security rules, collections architecture, and authentication gates are prepared for this module.",
                        fontSize = 12.sp,
                        color = BgsmTextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
