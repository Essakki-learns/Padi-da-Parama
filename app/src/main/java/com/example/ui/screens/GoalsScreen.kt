package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.GoalSubtaskEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.GoalWithSubtasks
import com.example.ui.viewmodel.StudentViewModel

@Composable
fun GoalsScreen(
    viewModel: StudentViewModel,
    modifier: Modifier = Modifier
) {
    val goalsWithSubtasks by viewModel.goalsWithSubtasks.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf("ALL") }
    var showAddGoalDialog by remember { mutableStateOf(false) }

    val categories = listOf("ALL", "ACADEMIC", "CAREER", "FINANCIAL", "HEALTH", "PERSONAL")

    val filteredGoals = remember(goalsWithSubtasks, selectedCategory) {
        if (selectedCategory == "ALL") goalsWithSubtasks
        else goalsWithSubtasks.filter { it.goal.category == selectedCategory }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddGoalDialog = true },
                containerColor = PrimaryIndigo,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("add_goal_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
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
            // Category filter
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }

            if (filteredGoals.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .frostedGlass(shape = RoundedCornerShape(18.dp))
                    ) {
                        Text(
                            text = "No goals found in this category. Set a new milestone with '+'!",
                            modifier = Modifier.padding(20.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(filteredGoals) { item ->
                    GoalCardItem(
                        item = item,
                        onToggleSubtask = { subtask -> viewModel.toggleGoalSubtask(subtask) },
                        onAddSubtask = { title -> viewModel.addGoalSubtask(item.goal.id, title) },
                        onDeleteGoal = { viewModel.deleteGoal(item.goal) }
                    )
                }
            }
        }
    }

    if (showAddGoalDialog) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("ACADEMIC") }
        var timeframe by remember { mutableStateOf("SHORT_TERM") }
        var daysToTarget by remember { mutableStateOf("60") }

        Dialog(onDismissRequest = { showAddGoalDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .frostedGlass(shape = RoundedCornerShape(24.dp), elevation = 6.dp)
                    .padding(16.dp)
                    .testTag("add_goal_dialog")
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Define New Milestone / Goal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Goal Title *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description & Outcome") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Category", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("ACADEMIC", "CAREER", "FINANCIAL", "HEALTH").forEach { c ->
                            FilterChip(
                                selected = category == c,
                                onClick = { category = c },
                                label = { Text(c, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddGoalDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    viewModel.addGoal(
                                        title.trim(),
                                        description.trim(),
                                        category,
                                        timeframe,
                                        daysToTarget.toIntOrNull() ?: 60
                                    )
                                    showAddGoalDialog = false
                                }
                            },
                            enabled = title.isNotBlank()
                        ) {
                            Text("Create")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoalCardItem(
    item: GoalWithSubtasks,
    onToggleSubtask: (GoalSubtaskEntity) -> Unit,
    onAddSubtask: (String) -> Unit,
    onDeleteGoal: () -> Unit,
    modifier: Modifier = Modifier
) {
    var newSubtaskText by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .frostedGlass(shape = RoundedCornerShape(20.dp), elevation = 2.dp)
            .testTag("goal_card_${item.goal.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = {},
                    label = { Text(item.goal.category) }
                )

                Text(
                    text = "${item.computedProgress.toInt()}%",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryIndigo
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.goal.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!item.goal.description.isNullOrEmpty()) {
                Text(
                    text = item.goal.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { item.computedProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = PrimaryIndigo,
                trackColor = PrimaryIndigo.copy(alpha = 0.15f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Subtasks checklist
            Text(
                text = "ACTION STEPS (${item.subtasks.count { it.isCompleted }}/${item.subtasks.size})",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                item.subtasks.forEach { subtask ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = subtask.isCompleted,
                            onCheckedChange = { onToggleSubtask(subtask) },
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = subtask.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (subtask.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Quick add subtask row
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newSubtaskText,
                    onValueChange = { newSubtaskText = it },
                    placeholder = { Text("Add next action step...", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = {
                        if (newSubtaskText.isNotBlank()) {
                            onAddSubtask(newSubtaskText.trim())
                            newSubtaskText = ""
                        }
                    },
                    enabled = newSubtaskText.isNotBlank()
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add subtask", tint = PrimaryIndigo)
                }
            }
        }
    }
}

