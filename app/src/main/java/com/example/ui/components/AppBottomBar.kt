package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.navigation.Screen
import com.example.ui.theme.frostedGlass

data class NavItem(
    val screen: Screen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
)

@Composable
fun AppBottomBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit,
    onOpenMoreSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(Screen.Dashboard, Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "Home"),
        NavItem(Screen.Academics, Icons.Filled.School, Icons.Outlined.School, "Academics"),
        NavItem(Screen.Assignments, Icons.Filled.AssignmentTurnedIn, Icons.Outlined.Assignment, "Tasks"),
        NavItem(Screen.Habits, Icons.Filled.LocalFireDepartment, Icons.Outlined.LocalFireDepartment, "Habits"),
        NavItem(Screen.Finances, Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, "Finances")
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("app_bottom_bar")
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .frostedGlass(
                    shape = RoundedCornerShape(26.dp),
                    borderWidth = 1.2.dp,
                    elevation = 6.dp,
                    alpha = 0.88f
                ),
            color = Color.Transparent,
            shape = RoundedCornerShape(26.dp)
        ) {
            NavigationBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.screen.route || 
                        (item.screen == Screen.Academics && currentRoute?.startsWith("subject_detail") == true)
                    NavigationBarItem(
                        modifier = Modifier.testTag("bottom_nav_${item.label.lowercase()}"),
                        selected = isSelected,
                        onClick = { onNavigate(item.screen) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(text = item.label, style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}

