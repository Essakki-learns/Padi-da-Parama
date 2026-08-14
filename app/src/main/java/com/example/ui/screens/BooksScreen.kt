package com.example.ui.screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.BookEntity
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.TertiaryEmerald
import com.example.ui.theme.frostedGlass
import com.example.ui.viewmodel.StudentViewModel

@Composable
fun BooksScreen(
    viewModel: StudentViewModel,
    modifier: Modifier = Modifier
) {
    val allBooks by viewModel.allBooks.collectAsStateWithLifecycle()
    var selectedStatus by remember { mutableStateOf("ALL") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedBookForUpdate by remember { mutableStateOf<BookEntity?>(null) }

    val filteredBooks = remember(allBooks, selectedStatus) {
        if (selectedStatus == "ALL") allBooks
        else allBooks.filter { it.status == selectedStatus }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = TertiaryEmerald,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("add_book_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Book")
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
            // Status filters
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val tabs = listOf("ALL", "READING", "WANT_TO_READ", "FINISHED")
                    items(tabs) { tab ->
                        FilterChip(
                            selected = selectedStatus == tab,
                            onClick = { selectedStatus = tab },
                            label = { Text(tab.replace('_', ' ')) }
                        )
                    }
                }
            }

            if (filteredBooks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .frostedGlass(shape = RoundedCornerShape(18.dp))
                    ) {
                        Text(
                            text = "No books found in this status.",
                            modifier = Modifier.padding(20.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(filteredBooks) { book ->
                    BookCardItem(
                        book = book,
                        onClick = { selectedBookForUpdate = book },
                        onDelete = { viewModel.deleteBook(book) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var author by remember { mutableStateOf("") }
        var genre by remember { mutableStateOf("Computer Science") }
        var pages by remember { mutableStateOf("350") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .frostedGlass(shape = RoundedCornerShape(24.dp), elevation = 6.dp)
                    .padding(16.dp)
                    .testTag("add_book_dialog")
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Add Book to Library", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Book Title *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Author") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = genre,
                            onValueChange = { genre = it },
                            label = { Text("Genre / Category") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = pages,
                            onValueChange = { pages = it },
                            label = { Text("Total Pages") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    viewModel.addBook(title.trim(), author.trim(), genre.trim(), pages.toIntOrNull() ?: 300)
                                    showAddDialog = false
                                }
                            },
                            enabled = title.isNotBlank()
                        ) {
                            Text("Add Book")
                        }
                    }
                }
            }
        }
    }

    if (selectedBookForUpdate != null) {
        val book = selectedBookForUpdate!!
        var currentPage by remember { mutableStateOf(book.currentPage.toString()) }
        var rating by remember { mutableIntStateOf(book.rating) }
        var notes by remember { mutableStateOf(book.notes) }

        Dialog(onDismissRequest = { selectedBookForUpdate = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .frostedGlass(shape = RoundedCornerShape(24.dp), elevation = 6.dp)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Update Reading Progress", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(book.title, style = MaterialTheme.typography.bodyMedium, color = PrimaryIndigo)

                    OutlinedTextField(
                        value = currentPage,
                        onValueChange = { currentPage = it },
                        label = { Text("Current Page (out of ${book.pageCount})") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Rating (1-5 Stars)", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (r in 1..5) {
                            IconButton(onClick = { rating = r }) {
                                Icon(
                                    imageVector = if (r <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "$r stars",
                                    tint = AccentAmber
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Key Takeaways / Highlights") },
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { selectedBookForUpdate = null }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val pageNum = currentPage.toIntOrNull() ?: book.currentPage
                                viewModel.updateBookProgress(book, pageNum, rating, notes.trim())
                                selectedBookForUpdate = null
                            }
                        ) {
                            Text("Update")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookCardItem(
    book: BookEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (book.status) {
        "FINISHED" -> TertiaryEmerald
        "READING" -> PrimaryIndigo
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .frostedGlass(shape = RoundedCornerShape(20.dp), elevation = 2.dp)
            .clickable { onClick() }
            .testTag("book_card_${book.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = {},
                    label = { Text(book.status.replace('_', ' ')) }
                )

                if (book.rating > 0) {
                    Text(
                        text = "⭐".repeat(book.rating),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!book.author.isNullOrEmpty()) {
                Text(
                    text = "By ${book.author}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Page ${book.currentPage} / ${book.pageCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${book.progress}%",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { book.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = statusColor,
                trackColor = statusColor.copy(alpha = 0.15f)
            )

            if (book.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${book.notes}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

