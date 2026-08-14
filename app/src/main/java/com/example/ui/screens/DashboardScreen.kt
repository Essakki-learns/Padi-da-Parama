package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.*
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudentViewModel

data class QuickNavTile(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val screen: Screen
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: StudentViewModel,
    onNavigate: (Screen) -> Unit,
    onOpenSubject: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val subjectsWithStats by viewModel.subjectsWithStats.collectAsStateWithLifecycle()
    val upcomingTasks by viewModel.upcomingTasks.collectAsStateWithLifecycle()
    val habitsWithStreaks by viewModel.habitsWithStreaks.collectAsStateWithLifecycle()
    val totalExpense by viewModel.totalExpense.collectAsStateWithLifecycle()
    val habitLogs by viewModel.allHabitLogs.collectAsStateWithLifecycle()
    val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()
    val timerSecondsLeft by viewModel.timerSecondsLeft.collectAsStateWithLifecycle()
    val selectedTimerSubject by viewModel.selectedTimerSubject.collectAsStateWithLifecycle()

    var showStudyTimerDialog by remember { mutableStateOf(false) }
    var showAddAssignmentDialog by remember { mutableStateOf(false) }
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showAddTransactionDialog by remember { mutableStateOf(false) }

    // Check if user has initialized their profile
    if (profile == null || profile?.name?.isBlank() == true) {
        var setupName by remember { mutableStateOf("") }
        var setupCollege by remember { mutableStateOf("") }
        var setupCourse by remember { mutableStateOf("") }
        var setupTargetGpa by remember { mutableStateOf("9.0") }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("dashboard_onboarding_view"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .frostedGlass(shape = RoundedCornerShape(24.dp), elevation = 6.dp)
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryIndigo.copy(alpha = 0.15f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = PrimaryIndigo,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Text(
                            text = "Welcome to Padi da Parama! 🎓",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Set up your student profile to personalize your dashboard, track coursework, tasks, and streaks.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        OutlinedTextField(
                            value = setupName,
                            onValueChange = { setupName = it },
                            label = { Text("Your Full Name *") },
                            placeholder = { Text("e.g. Parama Sundaram") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            colors = appTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = setupCollege,
                            onValueChange = { setupCollege = it },
                            label = { Text("College / University *") },
                            placeholder = { Text("e.g. SKCET Coimbatore") },
                            leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                            colors = appTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = setupCourse,
                            onValueChange = { setupCourse = it },
                            label = { Text("Course & Year *") },
                            placeholder = { Text("e.g. B.E. Computer Science - Year 3") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                            colors = appTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = setupTargetGpa,
                            onValueChange = { setupTargetGpa = it },
                            label = { Text("Target GPA (out of 10)") },
                            placeholder = { Text("9.0") },
                            leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
                            colors = appTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (setupName.isNotBlank()) {
                                    val gpa = setupTargetGpa.toDoubleOrNull() ?: 9.0
                                    viewModel.setupInitialProfile(
                                        name = setupName.trim(),
                                        college = setupCollege.trim().ifBlank { "University" },
                                        courseYear = setupCourse.trim().ifBlank { "Student" },
                                        targetGpa = gpa
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("setup_profile_submit_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                        ) {
                            Text("Complete Setup & Open Dashboard", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
        return
    }

    val activeHabitDates = remember(habitLogs) {
        habitLogs.filter { it.completed }.map { it.dateString }.toSet()
    }

    val quickNavTiles = listOf(
        QuickNavTile("Academics", "${subjectsWithStats.size} Subjects", Icons.Default.School, PrimaryIndigo, Screen.Academics),
        QuickNavTile("Tasks & Exams", "${upcomingTasks.size} Pending", Icons.Default.AssignmentTurnedIn, AccentAmber, Screen.Assignments),
        QuickNavTile("Daily Journal", "Reflect & Grow", Icons.Default.MenuBook, StatusPurple, Screen.Journal),
        QuickNavTile("Notes & Links", "Wiki Knowledge", Icons.Default.EditNote, StatusCyan, Screen.Notes),
        QuickNavTile("Documents Vault", "PDFs & Materials", Icons.Default.Folder, PrimaryIndigoLight, Screen.Documents),
        QuickNavTile("Book Tracker", "Reading List", Icons.Default.Bookmark, TertiaryEmerald, Screen.Books),
        QuickNavTile("Goals Roadmap", "Target Success", Icons.Default.Flag, StatusRed, Screen.Goals),
        QuickNavTile("Habit Streaks", "Build Discipline", Icons.Default.LocalFireDepartment, AccentAmber, Screen.Habits),
        QuickNavTile("Finances", "Budget & UPI", Icons.Default.AccountBalanceWallet, TertiaryEmerald, Screen.Finances),
        QuickNavTile("Search All", "Instant Find", Icons.Default.Search, PrimaryIndigoLight, Screen.Search)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // --- 1. Hero Student Welcome Banner ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .frostedGlass(shape = RoundedCornerShape(24.dp), elevation = 4.dp)
                    .testTag("dashboard_hero_card")
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.banner_dashboard),
                            contentDescription = "Student Workspace Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.65f)
                                        )
                                    )
                                )
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Vanakkam, ${profile?.name ?: "Student"}! 🎓",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${profile?.college ?: "University"} • ${profile?.semester ?: "Current Term"}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = profile?.major ?: "Student Course",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { showStudyTimerDialog = true },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTimerRunning) StatusRed else PrimaryIndigo
                            ),
                            modifier = Modifier.testTag("study_timer_quick_button")
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isTimerRunning) "Focusing" else "Focus Timer")
                        }
                    }
                }
            }
        }

        // --- 2. Core Metrics / Key Stats Grid ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "TARGET GPA",
                    value = if ((profile?.targetGpa ?: 0.0) > 0) "${profile?.targetGpa} / 10" else "Set Target",
                    subtitle = if ((profile?.currentGpa ?: 0.0) > 0) "Current: ${profile?.currentGpa}" else "Tap to Track Courses",
                    icon = Icons.Default.TrendingUp,
                    gradientColors = listOf(PrimaryIndigo, PrimaryIndigoLight),
                    onClick = { onNavigate(Screen.Academics) },
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "PENDING TASKS",
                    value = "${upcomingTasks.size}",
                    subtitle = "Assignments & Exams",
                    icon = Icons.Default.CheckCircleOutline,
                    gradientColors = listOf(AccentAmber, Color(0xFFF59E0B)),
                    onClick = { onNavigate(Screen.Assignments) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val maxStreak = habitsWithStreaks.maxOfOrNull { it.currentStreak } ?: 0
                StatCard(
                    title = "BEST STREAK",
                    value = "$maxStreak Days",
                    subtitle = "${habitsWithStreaks.count { it.isCompletedToday }}/${habitsWithStreaks.size} Done Today",
                    icon = Icons.Default.LocalFireDepartment,
                    gradientColors = listOf(Color(0xFFDC2626), Color(0xFFF97316)),
                    onClick = { onNavigate(Screen.Habits) },
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "MONTH SPENT",
                    value = "₹${totalExpense.toInt()}",
                    subtitle = "Budget Tracker",
                    icon = Icons.Default.AccountBalanceWallet,
                    gradientColors = listOf(TertiaryEmerald, Color(0xFF10B981)),
                    onClick = { onNavigate(Screen.Finances) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // --- 3. Today's Urgent Schedule & Task Checklist ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S ACTION ITEMS",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = { showAddAssignmentDialog = true },
                    modifier = Modifier.testTag("add_task_header_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Task")
                }
            }

            if (upcomingTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .frostedGlass(shape = RoundedCornerShape(18.dp))
                ) {
                    Text(
                        text = "🎉 All tasks caught up! Relax or log a study session.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    upcomingTasks.take(4).forEach { task ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .frostedGlass(shape = RoundedCornerShape(18.dp), elevation = 2.dp)
                                .testTag("dashboard_task_card_${task.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = task.isComplete,
                                    onCheckedChange = { viewModel.toggleAssignment(task) },
                                    modifier = Modifier.testTag("task_checkbox_${task.id}")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (task.type == "EXAM") {
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
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    if (!task.description.isNullOrEmpty()) {
                                        Text(
                                            text = task.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                                AssistChip(
                                    onClick = {},
                                    label = { Text(task.priority) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        labelColor = when (task.priority) {
                                            "HIGH" -> StatusRed
                                            "MEDIUM" -> AccentAmber
                                            else -> TertiaryEmerald
                                        }
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 4. Daily Habit Quick Check-Ins ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DAILY HABIT STREAKS",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = { onNavigate(Screen.Habits) }) {
                    Text("View All")
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(habitsWithStreaks) { habitWithStreak ->
                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .frostedGlass(
                                shape = RoundedCornerShape(18.dp),
                                elevation = 2.dp,
                                alpha = if (habitWithStreak.isCompletedToday) 0.95f else 0.80f
                            )
                            .clickable { viewModel.toggleHabitToday(habitWithStreak) }
                            .testTag("habit_card_${habitWithStreak.habit.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(habitWithStreak.habit.icon, fontSize = 24.sp)
                                if (habitWithStreak.isCompletedToday) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Done",
                                        tint = PrimaryIndigo,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Text(
                                text = habitWithStreak.habit.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1
                            )
                            Text(
                                text = "🔥 ${habitWithStreak.currentStreak} day streak",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (habitWithStreak.currentStreak > 0) AccentAmber else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // --- 5. Study Momentum Heatmap ---
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

        // --- 6. Quick Module Grid Navigation ---
        item {
            Text(
                text = "STUDENT TOOLKIT & MODULES",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                quickNavTiles.chunked(3).forEach { rowTiles ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowTiles.forEach { tile ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .frostedGlass(shape = RoundedCornerShape(18.dp), elevation = 2.dp)
                                    .clickable { onNavigate(tile.screen) }
                                    .testTag("quick_nav_${tile.title.lowercase().replace(' ', '_')}")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(tile.color.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = tile.icon,
                                            contentDescription = null,
                                            tint = tile.color,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = tile.title,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = tile.subtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Dialogs ---
    if (showStudyTimerDialog) {
        StudyTimerDialog(
            secondsLeft = timerSecondsLeft,
            isRunning = isTimerRunning,
            selectedSubject = selectedTimerSubject,
            onStart = { viewModel.startTimer() },
            onPause = { viewModel.pauseTimer() },
            onReset = { mins -> viewModel.resetTimer(mins) },
            onDismiss = { showStudyTimerDialog = false }
        )
    }

    if (showAddAssignmentDialog) {
        val subjects by viewModel.allSubjects.collectAsStateWithLifecycle()
        AddAssignmentDialog(
            subjects = subjects,
            onDismiss = { showAddAssignmentDialog = false },
            onConfirm = { subId, title, desc, type, due, prio, hrs, wt ->
                viewModel.addAssignment(subId, title, desc, type, due, prio, hrs, wt)
                showAddAssignmentDialog = false
            }
        )
    }
}
