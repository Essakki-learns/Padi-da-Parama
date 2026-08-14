package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.AssignmentEntity
import com.example.ui.components.AddAssignmentDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AssignmentsScreen(
    viewModel: StudentViewModel,
    modifier: Modifier = Modifier
) {
    val allAssignments by viewModel.allAssignments.collectAsStateWithLifecycle()
    val allSubjects by viewModel.allSubjects.collectAsStateWithLifecycle()

    var filterType by remember { mutableStateOf("ALL") } // ALL, PENDING, EXAMS, ASSIGNMENTS, COMPLETED
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredList = remember(allAssignments, filterType) {
        when (filterType) {
            "PENDING" -> allAssignments.filter { !it.isComplete }
            "EXAMS" -> allAssignments.filter { it.type == "EXAM" }
            "ASSIGNMENTS" -> allAssignments.filter { it.type == "ASSIGNMENT" }
            "COMPLETED" -> allAssignments.filter { it.isComplete }
            else -> allAssignments
        }
    }

    val pendingCount = allAssignments.count { !it.isComplete }
    val examCount = allAssignments.count { it.type == "EXAM" && !it.isComplete }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryIndigo,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("add_assignment_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp, top = 16.dp)
        ) {
            // Header stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .frostedGlass(shape = RoundedCornerShape(18.dp), elevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Pending Tasks", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "$pendingCount Active",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .frostedGlass(shape = RoundedCornerShape(18.dp), elevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Upcoming Exams", style = MaterialTheme.typography.labelSmall, color = StatusRed)
                            Text(
                                "$examCount Tests",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = StatusRed)
                            )
                        }
                    }
                }
            }

            // Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filters = listOf("ALL", "PENDING", "EXAMS", "ASSIGNMENTS", "COMPLETED")
                    items(filters) { f ->
                        FilterChip(
                            selected = filterType == f,
                            onClick = { filterType = f },
                            label = { Text(f) },
                            modifier = Modifier.testTag("filter_chip_$f")
                        )
                    }
                }
            }

            if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .frostedGlass(shape = RoundedCornerShape(18.dp))
                    ) {
                        Text(
                            text = "No tasks found in this view.",
                            modifier = Modifier.padding(20.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(filteredList) { task ->
                    val subject = allSubjects.find { it.id == task.subjectId }
                    AssignmentCardItem(
                        task = task,
                        subjectName = subject?.name ?: "Course",
                        onToggle = { viewModel.toggleAssignment(task) },
                        onDelete = { viewModel.deleteAssignment(task) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddAssignmentDialog(
            subjects = allSubjects,
            onDismiss = { showAddDialog = false },
            onConfirm = { subId, title, desc, type, due, prio, hrs, wt ->
                viewModel.addAssignment(subId, title, desc, type, due, prio, hrs, wt)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AssignmentCardItem(
    task: AssignmentEntity,
    subjectName: String,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sdf = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
    val dueDateStr = sdf.format(Date(task.dueDate))
    val isExam = task.type == "EXAM"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .frostedGlass(
                shape = RoundedCornerShape(18.dp),
                elevation = if (task.isComplete) 1.dp else 2.dp,
                alpha = if (task.isComplete) 0.65f else 0.85f
            )
            .testTag("assignment_card_${task.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isComplete,
                onCheckedChange = { onToggle() },
                modifier = Modifier.testTag("assignment_check_${task.id}")
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isExam) {
                        Text(
                            text = "EXAM",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = StatusRed,
                            modifier = Modifier
                                .background(StatusRedContainer, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (task.isComplete) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "$subjectName • Due: $dueDateStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!task.description.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "⚡ ${task.estimatedHours}h est.",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "🎯 ${task.weightage}% weight",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = AccentAmber
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Task",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}

