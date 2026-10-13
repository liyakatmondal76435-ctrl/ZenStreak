package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.MintActivePill
import com.example.ui.theme.MintBadgeGreen

enum class NavigationTab {
    HOME,
    ANALYTICS,
    SETTINGS
}

@Composable
fun FloatingPillBottomBar(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    onPlusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Main pill capsule
        Surface(
            shape = RoundedCornerShape(36.dp),
            color = MintActivePill.copy(alpha = 0.95f),
            shadowElevation = 8.dp,
            modifier = Modifier.clip(RoundedCornerShape(36.dp))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Home tab
                PillTabItem(
                    title = "Home",
                    icon = Icons.Default.Home,
                    isSelected = selectedTab == NavigationTab.HOME,
                    onClick = { onTabSelected(NavigationTab.HOME) },
                    testTag = "tab_home"
                )

                // Analytics tab
                PillTabItem(
                    title = "Analytics",
                    icon = Icons.Default.BarChart,
                    isSelected = selectedTab == NavigationTab.ANALYTICS,
                    onClick = { onTabSelected(NavigationTab.ANALYTICS) },
                    testTag = "tab_analytics"
                )

                // Settings tab
                PillTabItem(
                    title = "Settings",
                    icon = Icons.Default.Settings,
                    isSelected = selectedTab == NavigationTab.SETTINGS,
                    onClick = { onTabSelected(NavigationTab.SETTINGS) },
                    testTag = "tab_settings"
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Floating Action Button [+]
        FloatingActionButton(
            onClick = onPlusClick,
            shape = CircleShape,
            containerColor = MintBadgeGreen,
            contentColor = ForestGreen,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
            modifier = Modifier
                .size(56.dp)
                .testTag("floating_plus_button")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Log Incident or Urge",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun PillTabItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) ForestGreen else Color.Transparent,
        label = "pillBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else ForestGreen,
        label = "pillContent"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(28.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = if (isSelected) 16.dp else 12.dp, vertical = 10.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
            if (isSelected) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    fontSize = 14.sp
                )
            }
        }
    }
}
