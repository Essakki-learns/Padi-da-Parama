package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.StatusRed
import com.example.ui.theme.frostedGlass
import com.example.ui.viewmodel.StudentViewModel

@Composable
fun SettingsScreen(
    viewModel: StudentViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()

    var name by remember(profile) { mutableStateOf(profile?.name ?: "") }
    var email by remember(profile) { mutableStateOf(profile?.email ?: "") }
    var college by remember(profile) { mutableStateOf(profile?.college ?: "") }
    var major by remember(profile) { mutableStateOf(profile?.major ?: "") }
    var semester by remember(profile) { mutableStateOf(profile?.semester ?: "Semester 6") }
    var targetGpa by remember(profile) { mutableStateOf(profile?.targetGpa?.toString() ?: "9.5") }
    var currentGpa by remember(profile) { mutableStateOf(profile?.currentGpa?.toString() ?: "9.14") }

    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showSavedSnackbar by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .testTag("settings_screen_scroll"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Profile Header Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .frostedGlass(shape = RoundedCornerShape(22.dp), elevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "Profile avatar",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = name.ifEmpty { "Student Profile" },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${college.ifEmpty { "College" }} • ${semester.ifEmpty { "Year" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "GPA: $currentGpa (Target: $targetGpa)",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryIndigo
                        )
                    }
                }
            }

            // Student Information Form
            Text(
                text = "STUDENT PROFILE DETAILS",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth().testTag("student_name_field")
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = college,
                onValueChange = { college = it },
                label = { Text("College / University") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = major,
                onValueChange = { major = it },
                label = { Text("Major / Department") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = semester,
                    onValueChange = { semester = it },
                    label = { Text("Semester") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = currentGpa,
                    onValueChange = { currentGpa = it },
                    label = { Text("Current GPA") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = targetGpa,
                    onValueChange = { targetGpa = it },
                    label = { Text("Target GPA") },
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = {
                    viewModel.updateProfile(
                        name = name.trim(),
                        email = email.trim(),
                        college = college.trim(),
                        major = major.trim(),
                        year = 3,
                        targetGpa = targetGpa.toDoubleOrNull() ?: 9.5,
                        currentGpa = currentGpa.toDoubleOrNull() ?: 9.14,
                        semester = semester.trim()
                    )
                    showSavedSnackbar = true
                },
                modifier = Modifier.fillMaxWidth().testTag("save_profile_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Profile Changes")
            }

            if (showSavedSnackbar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .frostedGlass(shape = RoundedCornerShape(14.dp))
                ) {
                    Text(
                        text = "✓ Profile details updated successfully!",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Data Operations
            Text(
                text = "DATABASE & SAMPLE DATA",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .frostedGlass(shape = RoundedCornerShape(20.dp), elevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Sample Data & Reset", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text(
                        "Reload comprehensive sample courses, syllabus trees, upcoming assignments, habit streaks, goals, books, and budget transactions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedButton(
                        onClick = { showResetConfirmDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryIndigo),
                        modifier = Modifier.fillMaxWidth().testTag("seed_sample_data_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reset & Preload University Sample Data")
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Reset to University Sample Data?") },
            text = { Text("This will reload sample subjects, syllabus topics, habits, tasks, and budgets.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetData()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                ) {
                    Text("Reset & Preload")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

