package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.local.entities.JournalEntryEntity
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.TertiaryEmerald
import com.example.ui.theme.frostedGlass
import com.example.ui.viewmodel.StudentViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun JournalScreen(
    viewModel: StudentViewModel,
    modifier: Modifier = Modifier
) {
    val allJournalEntries by viewModel.allJournalEntries.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryIndigo,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("add_journal_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Write Reflection")
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
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .frostedGlass(shape = RoundedCornerShape(22.dp), elevation = 3.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Daily Reflection & Mindset",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Track growth, wins, and gratitude",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text("📓", fontSize = 28.sp)
                    }
                }
            }

            if (allJournalEntries.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .frostedGlass(shape = RoundedCornerShape(18.dp))
                    ) {
                        Text(
                            text = "No reflections yet. Tap '+' to write today's journal entry.",
                            modifier = Modifier.padding(20.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(allJournalEntries) { entry ->
                    JournalCardItem(
                        entry = entry,
                        onDelete = { viewModel.deleteJournalEntry(entry) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var mood by remember { mutableStateOf("⚡ Energetic") }
        var wentWell by remember { mutableStateOf("") }
        var improve by remember { mutableStateOf("") }
        var gratitude by remember { mutableStateOf("") }
        var tags by remember { mutableStateOf("College,Focus") }

        val moods = listOf("🚀 Inspired", "⚡ Energetic", "🧘 Calm", "💡 Creative", "😴 Tired")

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .frostedGlass(shape = RoundedCornerShape(24.dp), elevation = 6.dp)
                    .padding(16.dp)
                    .testTag("add_journal_dialog")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Daily Reflection", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                    Text("Today's Vibe / Mood", style = MaterialTheme.typography.labelSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        moods.forEach { m ->
                            FilterChip(
                                selected = mood == m,
                                onClick = { mood = m },
                                label = { Text(m, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = wentWell,
                        onValueChange = { wentWell = it },
                        label = { Text("🌟 What went well today?") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = improve,
                        onValueChange = { improve = it },
                        label = { Text("🔄 What can I improve tomorrow?") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = gratitude,
                        onValueChange = { gratitude = it },
                        label = { Text("🙏 What am I grateful for?") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.addJournalEntry(
                                    title.trim(),
                                    wentWell.ifEmpty { "Daily reflection" },
                                    mood,
                                    wentWell.trim(),
                                    improve.trim(),
                                    gratitude.trim(),
                                    tags.trim()
                                )
                                showAddDialog = false
                            }
                        ) {
                            Text("Save Entry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JournalCardItem(
    entry: JournalEntryEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sdf = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
    val dateStr = sdf.format(Date(entry.dateMillis))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .frostedGlass(shape = RoundedCornerShape(20.dp), elevation = 2.dp)
            .testTag("journal_card_${entry.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AssistChip(
                        onClick = {},
                        label = { Text(entry.mood) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }

            if (entry.title.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (entry.wentWell.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🌟 Wins: ${entry.wentWell}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (entry.improve.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🔄 Improve: ${entry.improve}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (entry.gratitude.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🙏 Grateful for: ${entry.gratitude}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TertiaryEmerald
                )
            }
        }
    }
}

