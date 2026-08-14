package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.SubjectEntity
import com.example.ui.theme.frostedGlass

// --- Add Subject Dialog ---
@Composable
fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, code: String, instructor: String, credits: Int, colorHex: String, targetGrade: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var instructor by remember { mutableStateOf("") }
    var credits by remember { mutableStateOf("3") }
    var targetGrade by remember { mutableStateOf("A+") }
    var colorHex by remember { mutableStateOf("#4338CA") }

    val colors = listOf("#4338CA", "#059669", "#D97706", "#7C3AED", "#DC2626", "#0891B2")

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .frostedGlass(shape = RoundedCornerShape(24.dp), elevation = 6.dp)
                .padding(16.dp)
                .testTag("add_subject_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Add New Subject", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subject Name *") },
                    modifier = Modifier.fillMaxWidth().testTag("subject_name_input")
                )

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Course Code (e.g. CS8601)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = instructor,
                    onValueChange = { instructor = it },
                    label = { Text("Instructor / Professor") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = credits,
                        onValueChange = { credits = it },
                        label = { Text("Credits") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = targetGrade,
                        onValueChange = { targetGrade = it },
                        label = { Text("Target Grade") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Subject Color Theme", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.forEach { c ->
                        FilterChip(
                            selected = colorHex == c,
                            onClick = { colorHex = c },
                            label = { Text("■", color = androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(c))) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(name.trim(), code.trim(), instructor.trim(), credits.toIntOrNull() ?: 3, colorHex, targetGrade)
                            }
                        },
                        enabled = name.isNotBlank(),
                        modifier = Modifier.testTag("confirm_add_subject_button")
                    ) {
                        Text("Add Subject")
                    }
                }
            }
        }
    }
}

// --- Add Assignment / Exam Dialog ---
@Composable
fun AddAssignmentDialog(
    subjects: List<SubjectEntity>,
    preselectedSubjectId: Long? = null,
    onDismiss: () -> Unit,
    onConfirm: (subjectId: Long, title: String, description: String, type: String, dueDate: Long, priority: String, hours: Double, weight: Double) -> Unit
) {
    var selectedSubjectId by remember { mutableStateOf(preselectedSubjectId ?: subjects.firstOrNull()?.id ?: 0L) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("ASSIGNMENT") } // ASSIGNMENT, EXAM
    var priority by remember { mutableStateOf("MEDIUM") }
    var daysAhead by remember { mutableStateOf("3") }
    var hours by remember { mutableStateOf("2.5") }
    var weight by remember { mutableStateOf("15") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .frostedGlass(shape = RoundedCornerShape(24.dp), elevation = 6.dp)
                .padding(16.dp)
                .testTag("add_assignment_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (type == "EXAM") "Schedule Examination" else "Add Assignment / Task",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Type selector
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = type == "ASSIGNMENT",
                        onClick = { type = "ASSIGNMENT" },
                        label = { Text("📝 Assignment") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = type == "EXAM",
                        onClick = { type = "EXAM" },
                        label = { Text("🎓 Exam / Test") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title *") },
                    modifier = Modifier.fillMaxWidth().testTag("assignment_title_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Notes / Syllabus coverage") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                if (subjects.isNotEmpty()) {
                    Text("Select Subject", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        subjects.take(3).forEach { sub ->
                            FilterChip(
                                selected = selectedSubjectId == sub.id,
                                onClick = { selectedSubjectId = sub.id },
                                label = { Text(sub.name.take(12)) }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = daysAhead,
                        onValueChange = { daysAhead = it },
                        label = { Text("Due in Days") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight %") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("LOW", "MEDIUM", "HIGH").forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val dueMillis = System.currentTimeMillis() + ((daysAhead.toLongOrNull() ?: 3) * 24 * 60 * 60 * 1000L)
                                onConfirm(
                                    selectedSubjectId,
                                    title.trim(),
                                    description.trim(),
                                    type,
                                    dueMillis,
                                    priority,
                                    hours.toDoubleOrNull() ?: 2.0,
                                    weight.toDoubleOrNull() ?: 15.0
                                )
                            }
                        },
                        enabled = title.isNotBlank(),
                        modifier = Modifier.testTag("confirm_add_assignment_button")
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

// --- Add Habit Dialog ---
@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, frequency: String, goal: Int, unit: String, icon: String, colorHex: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("DAILY") }
    var goal by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf("times") }
    var icon by remember { mutableStateOf("🔥") }
    var colorHex by remember { mutableStateOf("#4338CA") }

    val icons = listOf("🔥", "⏱️", "💻", "📖", "🏋️", "🧘", "💧", "🎯")

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .frostedGlass(shape = RoundedCornerShape(24.dp), elevation = 6.dp)
                .padding(16.dp)
                .testTag("add_habit_dialog")
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Create Habit & Routine", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Habit Name *") },
                    modifier = Modifier.fillMaxWidth().testTag("habit_name_input")
                )

                Text("Icon / Emoji", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    icons.forEach { ic ->
                        FilterChip(
                            selected = icon == ic,
                            onClick = { icon = ic },
                            label = { Text(ic) }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = goal,
                        onValueChange = { goal = it },
                        label = { Text("Daily Target") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit (e.g. mins, pages)") },
                        modifier = Modifier.weight(1.5f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(name.trim(), frequency, goal.toIntOrNull() ?: 1, unit.trim(), icon, colorHex)
                            }
                        },
                        enabled = name.isNotBlank(),
                        modifier = Modifier.testTag("confirm_add_habit_button")
                    ) {
                        Text("Add Habit")
                    }
                }
            }
        }
    }
}

// --- Add Transaction Dialog ---
@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (type: String, amount: Double, category: String, account: String, description: String) -> Unit
) {
    var type by remember { mutableStateOf("EXPENSE") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Food & Canteen") }
    var account by remember { mutableStateOf("UPI / GPay") }
    var description by remember { mutableStateOf("") }

    val categories = listOf("Food & Canteen", "Books & Stationery", "Tech & Subscriptions", "Travel & Metro", "Tuition & Fees", "Other")

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .frostedGlass(shape = RoundedCornerShape(24.dp), elevation = 6.dp)
                .padding(16.dp)
                .testTag("add_transaction_dialog")
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(8.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Log Transaction", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = type == "EXPENSE",
                        onClick = { type = "EXPENSE" },
                        label = { Text("💸 Expense") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = type == "INCOME",
                        onClick = { type = "INCOME" },
                        label = { Text("💰 Income / Allowance") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (₹) *") },
                    modifier = Modifier.fillMaxWidth().testTag("transaction_amount_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Merchant") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Category", style = MaterialTheme.typography.labelMedium)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    categories.chunked(3).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            row.forEach { cat ->
                                FilterChip(
                                    selected = category == cat,
                                    onClick = { category = cat },
                                    label = { Text(cat.take(14), style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = amount.toDoubleOrNull()
                            if (amt != null && amt > 0) {
                                onConfirm(type, amt, category, account, description.trim())
                            }
                        },
                        enabled = (amount.toDoubleOrNull() ?: 0.0) > 0,
                        modifier = Modifier.testTag("confirm_add_transaction_button")
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

