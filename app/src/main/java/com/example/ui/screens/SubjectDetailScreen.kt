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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.AssignmentEntity
import com.example.data.local.entities.SyllabusNodeEntity
import com.example.ui.components.AddAssignmentDialog
import com.example.ui.components.StudyTimerDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    subjectId: Long,
    viewModel: StudentViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allSubjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    val allSyllabusNodes by viewModel.allSyllabusNodes.collectAsStateWithLifecycle()
    val allAssignments by viewModel.allAssignments.collectAsStateWithLifecycle()
    val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()
    val timerSecondsLeft by viewModel.timerSecondsLeft.collectAsStateWithLifecycle()
    val selectedTimerSubject by viewModel.selectedTimerSubject.collectAsStateWithLifecycle()

    val subject = remember(allSubjects, subjectId) {
        allSubjects.find { it.id == subjectId }
    }
    val syllabusNodes = remember(allSyllabusNodes, subjectId) {
        allSyllabusNodes.filter { it.subjectId == subjectId }
    }
    val subjectAssignments = remember(allAssignments, subjectId) {
        allAssignments.filter { it.subjectId == subjectId }
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddTopicDialog by remember { mutableStateOf(false) }
    var showAddAssignmentDialog by remember { mutableStateOf(false) }
    var showTimerDialog by remember { mutableStateOf(false) }

    val parsedColor = remember(subject?.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(subject?.colorHex ?: "#4338CA"))
        } catch (e: Exception) {
            PrimaryIndigo
        }
    }

    val completedNodes = syllabusNodes.count { it.status == "COMPLETED" }
    val progressPct = if (syllabusNodes.isNotEmpty()) (completedNodes * 100) / syllabusNodes.size else 0

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showAddTopicDialog = true
                    else showAddAssignmentDialog = true
                },
                containerColor = parsedColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("subject_detail_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)
        ) {
            // Subject Hero Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .frostedGlass(shape = RoundedCornerShape(24.dp), elevation = 3.dp)
                        .testTag("subject_header_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = subject?.code ?: "COURSE",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = parsedColor,
                                modifier = Modifier
                                    .background(parsedColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )

                            Button(
                                onClick = {
                                    viewModel.setTimerSubject(subject?.name ?: "Subject")
                                    showTimerDialog = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = parsedColor),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Study Timer", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = subject?.name ?: "Subject Detail",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (!subject?.instructor.isNullOrEmpty()) {
                            Text(
                                text = "Faculty: ${subject?.instructor}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress indicator
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Syllabus Mastery",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$completedNodes / ${syllabusNodes.size} ($progressPct%)",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = parsedColor
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progressPct / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = parsedColor,
                            trackColor = parsedColor.copy(alpha = 0.15f)
                        )
                    }
                }
            }

            // Tab Row
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .frostedGlass(shape = RoundedCornerShape(16.dp))
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = parsedColor
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Syllabus Tree (${syllabusNodes.size})") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Tasks & Exams (${subjectAssignments.size})") }
                        )
                    }
                }
            }

            if (selectedTab == 0) {
                // Syllabus Tree
                if (syllabusNodes.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .frostedGlass(shape = RoundedCornerShape(16.dp))
                        ) {
                            Text(
                                text = "No syllabus topics added yet. Tap '+' to add units and chapters.",
                                modifier = Modifier.padding(20.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    items(syllabusNodes) { node ->
                        SyllabusNodeCard(
                            node = node,
                            themeColor = parsedColor,
                            onToggleStatus = { viewModel.toggleSyllabusStatus(node) },
                            onDelete = { viewModel.deleteSyllabusNode(node) }
                        )
                    }
                }
            } else {
                // Assignments & Exams Tab
                if (subjectAssignments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .frostedGlass(shape = RoundedCornerShape(16.dp))
                        ) {
                            Text(
                                text = "No assignments or exams recorded for this subject.",
                                modifier = Modifier.padding(20.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    items(subjectAssignments) { assignment ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .frostedGlass(shape = RoundedCornerShape(16.dp), elevation = 1.dp)
                                .testTag("subject_assignment_${assignment.id}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = assignment.isComplete,
                                    onCheckedChange = { viewModel.toggleAssignment(assignment) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (assignment.type == "EXAM") {
                                            Text(
                                                text = "EXAM",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = StatusRed,
                                                modifier = Modifier
                                                    .background(StatusRedContainer, RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }
                                        Text(
                                            text = assignment.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                    }
                                    if (!assignment.description.isNullOrEmpty()) {
                                        Text(
                                            text = assignment.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                IconButton(onClick = { viewModel.deleteAssignment(assignment) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Syllabus Topic Dialog
    if (showAddTopicDialog && subject != null) {
        var topicName by remember { mutableStateOf("") }
        var topicType by remember { mutableStateOf("TOPIC") }

        Dialog(onDismissRequest = { showAddTopicDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .frostedGlass(shape = RoundedCornerShape(24.dp), elevation = 6.dp)
                    .padding(16.dp)
                    .testTag("add_syllabus_dialog")
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Add Syllabus Topic", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = topicType == "UNIT",
                            onClick = { topicType = "UNIT" },
                            label = { Text("Unit Header") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = topicType == "TOPIC",
                            onClick = { topicType = "TOPIC" },
                            label = { Text("Sub-Topic") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = topicName,
                        onValueChange = { topicName = it },
                        label = { Text("Name / Description *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddTopicDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (topicName.isNotBlank()) {
                                    viewModel.addSyllabusNode(subject.id, topicName.trim(), topicType)
                                    showAddTopicDialog = false
                                }
                            },
                            enabled = topicName.isNotBlank()
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }

    if (showAddAssignmentDialog && subject != null) {
        AddAssignmentDialog(
            subjects = listOf(subject),
            preselectedSubjectId = subject.id,
            onDismiss = { showAddAssignmentDialog = false },
            onConfirm = { subId, title, desc, type, due, prio, hrs, wt ->
                viewModel.addAssignment(subId, title, desc, type, due, prio, hrs, wt)
                showAddAssignmentDialog = false
            }
        )
    }

    if (showTimerDialog) {
        StudyTimerDialog(
            secondsLeft = timerSecondsLeft,
            isRunning = isTimerRunning,
            selectedSubject = selectedTimerSubject,
            onStart = { viewModel.startTimer() },
            onPause = { viewModel.pauseTimer() },
            onReset = { mins -> viewModel.resetTimer(mins) },
            onDismiss = { showTimerDialog = false }
        )
    }
}

@Composable
fun SyllabusNodeCard(
    node: SyllabusNodeEntity,
    themeColor: Color,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUnit = node.type == "UNIT"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = if (isUnit) 0.dp else 16.dp)
            .frostedGlass(
                shape = RoundedCornerShape(14.dp),
                elevation = if (isUnit) 2.dp else 1.dp
            )
            .clickable { onToggleStatus() }
            .testTag("syllabus_node_${node.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val statusIcon = when (node.status) {
                "COMPLETED" -> Icons.Default.CheckCircle
                "IN_PROGRESS" -> Icons.Default.HourglassTop
                else -> Icons.Default.RadioButtonUnchecked
            }
            val statusColor = when (node.status) {
                "COMPLETED" -> TertiaryEmerald
                "IN_PROGRESS" -> AccentAmber
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }

            Icon(
                imageVector = statusIcon,
                contentDescription = node.status,
                tint = statusColor,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = node.name,
                    style = if (isUnit)
                        MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    else
                        MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Status: ${node.status.replace('_', ' ')} (Tap to advance)",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = statusColor
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete Topic",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

