package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddHabitDialog
import com.example.ui.components.HeatmapView
import com.example.ui.theme.*
import com.example.ui.viewmodel.HabitWithStreak
import com.example.ui.viewmodel.StudentViewModel

@Composable
fun HabitsScreen(
    viewModel: StudentViewModel,
    modifier: Modifier = Modifier
) {
    val habitsWithStreaks by viewModel.habitsWithStreaks.collectAsStateWithLifecycle()
    val habitLogs by viewModel.allHabitLogs.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    val activeHabitDates = remember(habitLogs) {
        habitLogs.filter { it.completed }.map { it.dateString }.toSet()
    }

    val completedToday = habitsWithStreaks.count { it.isCompletedToday }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AccentAmber,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("add_habit_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 80.dp, top = 16.dp)
        ) {
            // Header card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .frostedGlass(shape = RoundedCornerShape(22.dp), elevation = 3.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daily Habits & Discipline",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$completedToday / ${habitsWithStreaks.size} Completed Today",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(AccentAmberContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🔥",
                                fontSize = 24.sp
                            )
                        }
                    }
                }
            }

            // Consistency Heatmap
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .frostedGlass(shape = RoundedCornerShape(22.dp), elevation = 2.dp)
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        HeatmapView(activeDates = activeHabitDates)
                    }
                }
            }

            item {
                Text(
                    text = "ACTIVE HABITS (${habitsWithStreaks.size})",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(habitsWithStreaks) { item ->
                HabitDetailedCard(
                    item = item,
                    onToggle = { viewModel.toggleHabitToday(item) },
                    onDelete = { viewModel.deleteHabit(item.habit) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, freq, goal, unit, icon, color ->
                viewModel.addHabit(name, freq, goal, unit, icon, color)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun HabitDetailedCard(
    item: HabitWithStreak,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .frostedGlass(
                shape = RoundedCornerShape(22.dp),
                elevation = 2.dp,
                alpha = if (item.isCompletedToday) 0.95f else 0.82f
            )
            .clickable { onToggle() }
            .testTag("habit_detail_card_${item.habit.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.habit.icon, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = item.habit.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Goal: ${item.habit.goal} ${item.habit.unit} (${item.habit.frequency})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                FilledIconToggleButton(
                    checked = item.isCompletedToday,
                    onCheckedChange = { onToggle() },
                    modifier = Modifier.testTag("toggle_habit_btn_${item.habit.id}")
                ) {
                    Icon(
                        imageVector = if (item.isCompletedToday) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = "Log Habit"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Streak info & 7-day visual dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔥 ${item.currentStreak} Days Streak",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = AccentAmber
                )

                // 7 day indicator dots
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    item.last7DaysHistory.forEach { isDayDone ->
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isDayDone) TertiaryEmerald else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                                )
                        )
                    }
                }
            }
        }
    }
}

