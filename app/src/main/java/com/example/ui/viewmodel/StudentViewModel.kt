package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.*
import com.example.data.repository.StudentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class SubjectWithStats(
    val subject: SubjectEntity,
    val totalSyllabusNodes: Int,
    val completedSyllabusNodes: Int,
    val progressPercentage: Int,
    val pendingAssignmentsCount: Int,
    val upcomingExam: AssignmentEntity?
)

data class GoalWithSubtasks(
    val goal: GoalEntity,
    val subtasks: List<GoalSubtaskEntity>,
    val computedProgress: Float
)

data class HabitWithStreak(
    val habit: HabitEntity,
    val isCompletedToday: Boolean,
    val currentStreak: Int,
    val last7DaysHistory: List<Boolean> // Sun-Sat or last 7 days
)

data class CategoryBudgetSummary(
    val category: String,
    val limit: Double,
    val spent: Double,
    val percentage: Float
)

data class SearchResultItem(
    val id: Long,
    val category: String, // "Subject", "Task/Exam", "Note", "Journal", "Book", "Goal", "Habit", "Finance"
    val title: String,
    val subtitle: String,
    val tag: String? = null
)

class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudentRepository
    private val prefs = application.getSharedPreferences("student_app_prefs", android.content.Context.MODE_PRIVATE)

    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean("is_dark_mode", true))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
        prefs.edit().putBoolean("is_dark_mode", isDark).apply()
    }

    init {
        val database = AppDatabase.getDatabase(application)
        repository = StudentRepository(database.studentDao())
    }

    val profile: StateFlow<StudentProfileEntity?> = repository.profile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allSubjects: StateFlow<List<SubjectEntity>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSyllabusNodes: StateFlow<List<SyllabusNodeEntity>> = repository.allSyllabusNodes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAssignments: StateFlow<List<AssignmentEntity>> = repository.allAssignments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingTasks: StateFlow<List<AssignmentEntity>> = repository.upcomingTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allJournalEntries: StateFlow<List<JournalEntryEntity>> = repository.allJournalEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBooks: StateFlow<List<BookEntity>> = repository.allBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGoals: StateFlow<List<GoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGoalSubtasks: StateFlow<List<GoalSubtaskEntity>> = repository.allGoalSubtasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHabits: StateFlow<List<HabitEntity>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHabitLogs: StateFlow<List<HabitLogEntity>> = repository.allHabitLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBudgets: StateFlow<List<BudgetEntity>> = repository.allBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDocuments: StateFlow<List<DocumentEntity>> = repository.allDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Search Query & Results ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    val searchResults: StateFlow<List<SearchResultItem>> = combine(
        _searchQuery,
        allSubjects,
        allAssignments,
        allNotes,
        allJournalEntries,
        allBooks,
        allGoals,
        allHabits,
        allTransactions
    ) { params ->
        val query = (params[0] as String).trim().lowercase()
        if (query.isEmpty()) return@combine emptyList()

        val subjects = params[1] as List<SubjectEntity>
        val assignments = params[2] as List<AssignmentEntity>
        val notes = params[3] as List<NoteEntity>
        val journal = params[4] as List<JournalEntryEntity>
        val books = params[5] as List<BookEntity>
        val goals = params[6] as List<GoalEntity>
        val habits = params[7] as List<HabitEntity>
        val transactions = params[8] as List<TransactionEntity>

        val results = mutableListOf<SearchResultItem>()

        // Subjects
        subjects.filter { it.name.lowercase().contains(query) || (it.code?.lowercase()?.contains(query) == true) }
            .forEach {
                results.add(
                    SearchResultItem(
                        id = it.id,
                        category = "Subject",
                        title = it.name,
                        subtitle = "${it.code ?: "Course"} • ${it.instructor ?: "Instructor"}",
                        tag = "${it.credits} Credits"
                    )
                )
            }

        // Tasks / Exams
        assignments.filter { it.title.lowercase().contains(query) || (it.description?.lowercase()?.contains(query) == true) }
            .forEach {
                results.add(
                    SearchResultItem(
                        id = it.id,
                        category = if (it.type == "EXAM") "Exam" else "Assignment",
                        title = it.title,
                        subtitle = "Priority: ${it.priority} • ${if (it.isComplete) "Done" else "Pending"}",
                        tag = "${it.weightage}% Weight"
                    )
                )
            }

        // Notes
        notes.filter { it.title.lowercase().contains(query) || it.content.lowercase().contains(query) || it.tags.lowercase().contains(query) }
            .forEach {
                results.add(
                    SearchResultItem(
                        id = it.id,
                        category = "Note",
                        title = it.title,
                        subtitle = it.content.take(80).replace("\n", " "),
                        tag = it.tags.ifEmpty { null }
                    )
                )
            }

        // Journal
        journal.filter { it.title.lowercase().contains(query) || it.content.lowercase().contains(query) || it.gratitude.lowercase().contains(query) }
            .forEach {
                results.add(
                    SearchResultItem(
                        id = it.id,
                        category = "Journal",
                        title = it.title.ifEmpty { "Daily Reflection" },
                        subtitle = it.content.take(80),
                        tag = it.mood
                    )
                )
            }

        // Books
        books.filter { it.title.lowercase().contains(query) || (it.author?.lowercase()?.contains(query) == true) }
            .forEach {
                results.add(
                    SearchResultItem(
                        id = it.id,
                        category = "Book",
                        title = it.title,
                        subtitle = "Author: ${it.author ?: "Unknown"} • Status: ${it.status.replace('_', ' ')}",
                        tag = "${it.progress}% Read"
                    )
                )
            }

        // Goals
        goals.filter { it.title.lowercase().contains(query) || (it.description?.lowercase()?.contains(query) == true) }
            .forEach {
                results.add(
                    SearchResultItem(
                        id = it.id,
                        category = "Goal",
                        title = it.title,
                        subtitle = "${it.category} • ${it.timeframe}",
                        tag = "${it.progress.toInt()}%"
                    )
                )
            }

        // Habits
        habits.filter { it.name.lowercase().contains(query) }
            .forEach {
                results.add(
                    SearchResultItem(
                        id = it.id,
                        category = "Habit",
                        title = "${it.icon} ${it.name}",
                        subtitle = "Goal: ${it.goal} ${it.unit} (${it.frequency})",
                        tag = "Routine"
                    )
                )
            }

        // Transactions
        transactions.filter { it.category.lowercase().contains(query) || (it.description?.lowercase()?.contains(query) == true) }
            .forEach {
                results.add(
                    SearchResultItem(
                        id = it.id,
                        category = "Finance",
                        title = "${it.type}: ₹${it.amount.toInt()} - ${it.category}",
                        subtitle = it.description ?: it.account,
                        tag = it.paymentMethod
                    )
                )
            }

        results
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Computed Subjects With Stats ---
    val subjectsWithStats: StateFlow<List<SubjectWithStats>> = combine(
        allSubjects,
        allSyllabusNodes,
        allAssignments
    ) { subjects, nodes, assignments ->
        subjects.map { subject ->
            val subjectNodes = nodes.filter { it.subjectId == subject.id }
            val completedNodes = subjectNodes.count { it.status == "COMPLETED" }
            val progress = if (subjectNodes.isNotEmpty()) (completedNodes * 100) / subjectNodes.size else 0
            val pendingAssignments = assignments.count { it.subjectId == subject.id && !it.isComplete }
            val nextExam = assignments.firstOrNull { it.subjectId == subject.id && it.type == "EXAM" && !it.isComplete }

            SubjectWithStats(
                subject = subject,
                totalSyllabusNodes = subjectNodes.size,
                completedSyllabusNodes = completedNodes,
                progressPercentage = progress,
                pendingAssignmentsCount = pendingAssignments,
                upcomingExam = nextExam
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Computed Goals With Subtasks ---
    val goalsWithSubtasks: StateFlow<List<GoalWithSubtasks>> = combine(
        allGoals,
        allGoalSubtasks
    ) { goals, subtasks ->
        goals.map { goal ->
            val goalTasks = subtasks.filter { it.goalId == goal.id }
            val computed = if (goalTasks.isNotEmpty()) {
                val done = goalTasks.count { it.isCompleted }
                (done.toFloat() / goalTasks.size.toFloat()) * 100f
            } else {
                goal.progress
            }
            GoalWithSubtasks(
                goal = goal,
                subtasks = goalTasks,
                computedProgress = computed
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Computed Habits With Streaks ---
    val habitsWithStreaks: StateFlow<List<HabitWithStreak>> = combine(
        allHabits,
        allHabitLogs
    ) { habits, logs ->
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdf.format(Date())
        val calendar = Calendar.getInstance()

        // Generate past 7 days strings
        val past7Days = (6 downTo 0).map { daysAgo ->
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysAgo) }
            sdf.format(cal.time)
        }

        habits.map { habit ->
            val habitLogs = logs.filter { it.habitId == habit.id }
            val logDates = habitLogs.map { it.dateString }.toSet()
            val isDoneToday = logDates.contains(todayStr)

            // Compute current streak backwards from today or yesterday
            var streak = 0
            val checkCal = Calendar.getInstance()
            // If not done today, start checking from yesterday
            if (!isDoneToday) {
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
            }
            while (true) {
                val dateKey = sdf.format(checkCal.time)
                if (logDates.contains(dateKey)) {
                    streak++
                    checkCal.add(Calendar.DAY_OF_YEAR, -1)
                } else {
                    break
                }
            }

            val history = past7Days.map { logDates.contains(it) }

            HabitWithStreak(
                habit = habit,
                isCompletedToday = isDoneToday,
                currentStreak = streak,
                last7DaysHistory = history
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Financial Summary & Budget Progress ---
    val budgetSummaries: StateFlow<List<CategoryBudgetSummary>> = combine(
        allBudgets,
        allTransactions
    ) { budgets, transactions ->
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        budgets.map { budget ->
            val spent = transactions
                .filter { it.type == "EXPENSE" && it.category.equals(budget.category, ignoreCase = true) }
                .sumOf { it.amount }
            val pct = if (budget.limitAmount > 0) ((spent / budget.limitAmount) * 100).toFloat() else 0f

            CategoryBudgetSummary(
                category = budget.category,
                limit = budget.limitAmount,
                spent = spent,
                percentage = pct.coerceAtMost(100f)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalExpense: StateFlow<Double> = allTransactions.map { list ->
        list.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalIncome: StateFlow<Double> = allTransactions.map { list ->
        list.filter { it.type == "INCOME" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // --- Study Focus Timer ---
    private val _timerSecondsLeft = MutableStateFlow(25 * 60)
    val timerSecondsLeft: StateFlow<Int> = _timerSecondsLeft.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _selectedTimerSubject = MutableStateFlow<String>("General Study")
    val selectedTimerSubject: StateFlow<String> = _selectedTimerSubject.asStateFlow()

    private var timerJob: Job? = null

    fun setTimerSubject(subjectName: String) {
        _selectedTimerSubject.value = subjectName
    }

    fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value && _timerSecondsLeft.value > 0) {
                delay(1000)
                _timerSecondsLeft.value -= 1
            }
            if (_timerSecondsLeft.value <= 0) {
                _isTimerRunning.value = false
                // Log habit or session if desired
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer(minutes: Int = 25) {
        pauseTimer()
        _timerSecondsLeft.value = minutes * 60
    }

    // --- Actions ---

    // Subject CRUD
    fun addSubject(name: String, code: String, instructor: String, credits: Int, colorHex: String, targetGrade: String) {
        viewModelScope.launch {
            repository.insertSubject(
                SubjectEntity(
                    name = name,
                    code = code.ifEmpty { null },
                    instructor = instructor.ifEmpty { null },
                    credits = credits,
                    colorHex = colorHex,
                    targetGrade = targetGrade
                )
            )
        }
    }

    fun deleteSubject(subject: SubjectEntity) {
        viewModelScope.launch { repository.deleteSubject(subject) }
    }

    // Syllabus
    fun addSyllabusNode(subjectId: Long, name: String, type: String, parentId: Long? = null) {
        viewModelScope.launch {
            repository.insertSyllabusNode(
                SyllabusNodeEntity(
                    subjectId = subjectId,
                    name = name,
                    type = type,
                    status = "NOT_STARTED",
                    parentId = parentId
                )
            )
        }
    }

    fun toggleSyllabusStatus(node: SyllabusNodeEntity) {
        viewModelScope.launch {
            val nextStatus = when (node.status) {
                "NOT_STARTED" -> "IN_PROGRESS"
                "IN_PROGRESS" -> "COMPLETED"
                else -> "NOT_STARTED"
            }
            repository.updateSyllabusStatus(node.id, nextStatus)
        }
    }

    fun deleteSyllabusNode(node: SyllabusNodeEntity) {
        viewModelScope.launch { repository.deleteSyllabusNode(node) }
    }

    // Assignment & Exam
    fun addAssignment(
        subjectId: Long,
        title: String,
        description: String,
        type: String,
        dueDate: Long,
        priority: String,
        estimatedHours: Double,
        weightage: Double
    ) {
        viewModelScope.launch {
            repository.insertAssignment(
                AssignmentEntity(
                    subjectId = subjectId,
                    title = title,
                    description = description.ifEmpty { null },
                    type = type,
                    dueDate = dueDate,
                    priority = priority,
                    estimatedHours = estimatedHours,
                    weightage = weightage,
                    isComplete = false
                )
            )
        }
    }

    fun toggleAssignment(assignment: AssignmentEntity) {
        viewModelScope.launch {
            repository.toggleAssignmentComplete(assignment.id, assignment.isComplete)
        }
    }

    fun deleteAssignment(assignment: AssignmentEntity) {
        viewModelScope.launch { repository.deleteAssignment(assignment) }
    }

    // Habit
    fun toggleHabitToday(habitWithStreak: HabitWithStreak) {
        viewModelScope.launch {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.toggleHabitLog(
                habitId = habitWithStreak.habit.id,
                dateStr = todayStr,
                isCurrentlyCompleted = habitWithStreak.isCompletedToday
            )
        }
    }

    fun addHabit(name: String, frequency: String, goal: Int, unit: String, icon: String, colorHex: String) {
        viewModelScope.launch {
            repository.insertHabit(
                HabitEntity(
                    name = name,
                    frequency = frequency,
                    goal = goal,
                    unit = unit,
                    icon = icon,
                    colorHex = colorHex
                )
            )
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch { repository.deleteHabit(habit) }
    }

    // Goals & Subtasks
    fun addGoal(title: String, description: String, category: String, timeframe: String, targetDaysFromNow: Int) {
        viewModelScope.launch {
            val target = System.currentTimeMillis() + (targetDaysFromNow * 24 * 60 * 60 * 1000L)
            repository.insertGoal(
                GoalEntity(
                    title = title,
                    description = description.ifEmpty { null },
                    category = category,
                    timeframe = timeframe,
                    targetDate = target
                )
            )
        }
    }

    fun addGoalSubtask(goalId: Long, title: String) {
        viewModelScope.launch {
            repository.insertGoalSubtask(
                GoalSubtaskEntity(goalId = goalId, title = title, isCompleted = false)
            )
        }
    }

    fun toggleGoalSubtask(subtask: GoalSubtaskEntity) {
        viewModelScope.launch {
            repository.setSubtaskCompleted(subtask.id, !subtask.isCompleted)
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch { repository.deleteGoal(goal) }
    }

    // Notes
    fun addNote(title: String, content: String, tags: String) {
        viewModelScope.launch {
            repository.insertNote(
                NoteEntity(
                    title = title,
                    content = content,
                    tags = tags,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateNote(note: NoteEntity, newTitle: String, newContent: String, newTags: String) {
        viewModelScope.launch {
            repository.updateNote(
                note.copy(
                    title = newTitle,
                    content = newContent,
                    tags = newTags,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch { repository.deleteNote(note) }
    }

    // Journal
    fun addJournalEntry(title: String, content: String, mood: String, wentWell: String, improve: String, gratitude: String, tags: String) {
        viewModelScope.launch {
            repository.insertJournalEntry(
                JournalEntryEntity(
                    title = title,
                    content = content,
                    mood = mood,
                    dateMillis = System.currentTimeMillis(),
                    wentWell = wentWell,
                    improve = improve,
                    gratitude = gratitude,
                    tags = tags
                )
            )
        }
    }

    fun deleteJournalEntry(entry: JournalEntryEntity) {
        viewModelScope.launch { repository.deleteJournalEntry(entry) }
    }

    // Books
    fun addBook(title: String, author: String, genre: String, totalPages: Int) {
        viewModelScope.launch {
            repository.insertBook(
                BookEntity(
                    title = title,
                    author = author.ifEmpty { null },
                    genre = genre.ifEmpty { null },
                    status = "WANT_TO_READ",
                    pageCount = totalPages,
                    currentPage = 0,
                    progress = 0
                )
            )
        }
    }

    fun updateBookProgress(book: BookEntity, newPage: Int, rating: Int, notes: String) {
        viewModelScope.launch {
            val progress = if (book.pageCount > 0) ((newPage.toFloat() / book.pageCount.toFloat()) * 100).toInt().coerceIn(0, 100) else 0
            val status = when {
                progress >= 100 -> "FINISHED"
                newPage > 0 -> "READING"
                else -> "WANT_TO_READ"
            }
            repository.updateBook(
                book.copy(
                    currentPage = newPage,
                    progress = progress,
                    status = status,
                    rating = rating,
                    notes = notes,
                    finishedAt = if (status == "FINISHED") System.currentTimeMillis() else null
                )
            )
        }
    }

    fun deleteBook(book: BookEntity) {
        viewModelScope.launch { repository.deleteBook(book) }
    }

    // Transactions
    fun addTransaction(type: String, amount: Double, category: String, account: String, description: String) {
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionEntity(
                    type = type,
                    amount = amount,
                    category = category,
                    account = account,
                    description = description.ifEmpty { null },
                    dateMillis = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch { repository.deleteTransaction(transaction) }
    }

    // Documents / Notes attachment
    fun addDocument(title: String, originalName: String, subjectId: Long?, fileNotes: String) {
        viewModelScope.launch {
            repository.insertDocument(
                DocumentEntity(
                    title = title,
                    originalName = originalName,
                    subjectId = subjectId,
                    fileUriOrNotes = fileNotes
                )
            )
        }
    }

    fun deleteDocument(document: DocumentEntity) {
        viewModelScope.launch { repository.deleteDocument(document) }
    }

    // Profile & Reset
    fun setupInitialProfile(name: String, college: String, courseYear: String, targetGpa: Double = 9.0) {
        viewModelScope.launch {
            repository.updateProfile(
                StudentProfileEntity(
                    id = 1L,
                    name = name,
                    email = "",
                    college = college,
                    major = courseYear,
                    year = 1,
                    targetGpa = targetGpa,
                    currentGpa = 0.0,
                    semester = "Semester 1"
                )
            )
        }
    }

    fun updateProfile(name: String, email: String, college: String, major: String, year: Int, targetGpa: Double, currentGpa: Double, semester: String) {
        viewModelScope.launch {
            repository.updateProfile(
                StudentProfileEntity(
                    id = 1L,
                    name = name,
                    email = email,
                    college = college,
                    major = major,
                    year = year,
                    targetGpa = targetGpa,
                    currentGpa = currentGpa,
                    semester = semester
                )
            )
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    fun resetData() {
        viewModelScope.launch {
            repository.resetAllData()
        }
    }
}
