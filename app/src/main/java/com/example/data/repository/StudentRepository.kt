package com.example.data.repository

import com.example.data.local.dao.StudentDao
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class StudentRepository(private val dao: StudentDao) {

    // Profile
    val profile: Flow<StudentProfileEntity?> = dao.getProfile()
    suspend fun updateProfile(profile: StudentProfileEntity) = dao.insertProfile(profile)

    // Subjects
    val allSubjects: Flow<List<SubjectEntity>> = dao.getAllSubjects()
    fun getSubject(id: Long): Flow<SubjectEntity?> = dao.getSubjectById(id)
    suspend fun insertSubject(subject: SubjectEntity) = dao.insertSubject(subject)
    suspend fun updateSubject(subject: SubjectEntity) = dao.updateSubject(subject)
    suspend fun deleteSubject(subject: SubjectEntity) = dao.deleteSubject(subject)

    // Syllabus
    fun getSyllabusForSubject(subjectId: Long): Flow<List<SyllabusNodeEntity>> = dao.getSyllabusForSubject(subjectId)
    val allSyllabusNodes: Flow<List<SyllabusNodeEntity>> = dao.getAllSyllabusNodes()
    suspend fun insertSyllabusNode(node: SyllabusNodeEntity) = dao.insertSyllabusNode(node)
    suspend fun updateSyllabusNode(node: SyllabusNodeEntity) = dao.updateSyllabusNode(node)
    suspend fun updateSyllabusStatus(id: Long, status: String) = dao.updateSyllabusStatus(id, status)
    suspend fun deleteSyllabusNode(node: SyllabusNodeEntity) = dao.deleteSyllabusNode(node)

    // Assignments
    val allAssignments: Flow<List<AssignmentEntity>> = dao.getAllAssignments()
    val upcomingTasks: Flow<List<AssignmentEntity>> = dao.getUpcomingTasks()
    fun getAssignmentsForSubject(subjectId: Long): Flow<List<AssignmentEntity>> = dao.getAssignmentsForSubject(subjectId)
    suspend fun insertAssignment(assignment: AssignmentEntity) = dao.insertAssignment(assignment)
    suspend fun updateAssignment(assignment: AssignmentEntity) = dao.updateAssignment(assignment)
    suspend fun toggleAssignmentComplete(id: Long, currentComplete: Boolean) = dao.setAssignmentComplete(id, !currentComplete)
    suspend fun deleteAssignment(assignment: AssignmentEntity) = dao.deleteAssignment(assignment)

    // Journal
    val allJournalEntries: Flow<List<JournalEntryEntity>> = dao.getAllJournalEntries()
    suspend fun insertJournalEntry(entry: JournalEntryEntity) = dao.insertJournalEntry(entry)
    suspend fun updateJournalEntry(entry: JournalEntryEntity) = dao.updateJournalEntry(entry)
    suspend fun deleteJournalEntry(entry: JournalEntryEntity) = dao.deleteJournalEntry(entry)

    // Notes
    val allNotes: Flow<List<NoteEntity>> = dao.getAllNotes()
    suspend fun insertNote(note: NoteEntity) = dao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = dao.updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = dao.deleteNote(note)

    // Books
    val allBooks: Flow<List<BookEntity>> = dao.getAllBooks()
    suspend fun insertBook(book: BookEntity) = dao.insertBook(book)
    suspend fun updateBook(book: BookEntity) = dao.updateBook(book)
    suspend fun deleteBook(book: BookEntity) = dao.deleteBook(book)

    // Goals & Subtasks
    val allGoals: Flow<List<GoalEntity>> = dao.getAllGoals()
    val allGoalSubtasks: Flow<List<GoalSubtaskEntity>> = dao.getAllGoalSubtasks()
    fun getSubtasksForGoal(goalId: Long): Flow<List<GoalSubtaskEntity>> = dao.getSubtasksForGoal(goalId)
    suspend fun insertGoal(goal: GoalEntity) = dao.insertGoal(goal)
    suspend fun updateGoal(goal: GoalEntity) = dao.updateGoal(goal)
    suspend fun deleteGoal(goal: GoalEntity) = dao.deleteGoal(goal)
    suspend fun insertGoalSubtask(subtask: GoalSubtaskEntity) = dao.insertGoalSubtask(subtask)
    suspend fun setSubtaskCompleted(id: Long, isCompleted: Boolean) = dao.setSubtaskCompleted(id, isCompleted)
    suspend fun deleteGoalSubtask(subtask: GoalSubtaskEntity) = dao.deleteGoalSubtask(subtask)

    // Habits & Logs
    val allHabits: Flow<List<HabitEntity>> = dao.getAllHabits()
    val allHabitLogs: Flow<List<HabitLogEntity>> = dao.getAllHabitLogs()
    fun getHabitLogsForDate(dateStr: String): Flow<List<HabitLogEntity>> = dao.getHabitLogsForDate(dateStr)
    suspend fun insertHabit(habit: HabitEntity) = dao.insertHabit(habit)
    suspend fun updateHabit(habit: HabitEntity) = dao.updateHabit(habit)
    suspend fun deleteHabit(habit: HabitEntity) = dao.deleteHabit(habit)
    suspend fun toggleHabitLog(habitId: Long, dateStr: String, isCurrentlyCompleted: Boolean, value: Int = 1) {
        if (isCurrentlyCompleted) {
            dao.deleteHabitLog(habitId, dateStr)
        } else {
            dao.insertHabitLog(HabitLogEntity(habitId = habitId, dateString = dateStr, value = value, completed = true))
        }
    }

    // Transactions & Budgets
    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val allBudgets: Flow<List<BudgetEntity>> = dao.getAllBudgets()
    fun getBudgetsForMonth(monthStr: String): Flow<List<BudgetEntity>> = dao.getBudgetsForMonth(monthStr)
    suspend fun insertTransaction(transaction: TransactionEntity) = dao.insertTransaction(transaction)
    suspend fun updateTransaction(transaction: TransactionEntity) = dao.updateTransaction(transaction)
    suspend fun deleteTransaction(transaction: TransactionEntity) = dao.deleteTransaction(transaction)
    suspend fun insertBudget(budget: BudgetEntity) = dao.insertBudget(budget)
    suspend fun updateBudget(budget: BudgetEntity) = dao.updateBudget(budget)
    suspend fun deleteBudget(budget: BudgetEntity) = dao.deleteBudget(budget)

    // Documents
    val allDocuments: Flow<List<DocumentEntity>> = dao.getAllDocuments()
    suspend fun insertDocument(document: DocumentEntity) = dao.insertDocument(document)
    suspend fun deleteDocument(document: DocumentEntity) = dao.deleteDocument(document)

    // Reset / Seed Sample Data
    suspend fun resetAllData() {
        dao.clearSubjects()
        dao.clearAssignments()
        dao.clearSyllabusNodes()
        dao.clearJournal()
        dao.clearNotes()
        dao.clearBooks()
        dao.clearGoals()
        dao.clearGoalSubtasks()
        dao.clearHabits()
        dao.clearHabitLogs()
        dao.clearTransactions()
        dao.clearBudgets()
        dao.clearDocuments()
        com.example.data.local.DatabasePreloader.seedInitialData(dao)
    }
}
