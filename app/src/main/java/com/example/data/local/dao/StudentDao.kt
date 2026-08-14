package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    // --- Profile ---
    @Query("SELECT * FROM student_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<StudentProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: StudentProfileEntity)

    @Update
    suspend fun updateProfile(profile: StudentProfileEntity)

    // --- Subjects ---
    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :subjectId LIMIT 1")
    fun getSubjectById(subjectId: Long): Flow<SubjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)

    // --- Syllabus Nodes ---
    @Query("SELECT * FROM syllabus_nodes WHERE subjectId = :subjectId ORDER BY orderIndex ASC, id ASC")
    fun getSyllabusForSubject(subjectId: Long): Flow<List<SyllabusNodeEntity>>

    @Query("SELECT * FROM syllabus_nodes ORDER BY subjectId ASC, orderIndex ASC")
    fun getAllSyllabusNodes(): Flow<List<SyllabusNodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyllabusNode(node: SyllabusNodeEntity): Long

    @Update
    suspend fun updateSyllabusNode(node: SyllabusNodeEntity)

    @Query("UPDATE syllabus_nodes SET status = :status WHERE id = :id")
    suspend fun updateSyllabusStatus(id: Long, status: String)

    @Delete
    suspend fun deleteSyllabusNode(node: SyllabusNodeEntity)

    // --- Assignments & Exams ---
    @Query("SELECT * FROM assignments ORDER BY isComplete ASC, dueDate ASC")
    fun getAllAssignments(): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE subjectId = :subjectId ORDER BY isComplete ASC, dueDate ASC")
    fun getAssignmentsForSubject(subjectId: Long): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE isComplete = 0 ORDER BY dueDate ASC LIMIT 5")
    fun getUpcomingTasks(): Flow<List<AssignmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentEntity): Long

    @Update
    suspend fun updateAssignment(assignment: AssignmentEntity)

    @Query("UPDATE assignments SET isComplete = :isComplete WHERE id = :id")
    suspend fun setAssignmentComplete(id: Long, isComplete: Boolean)

    @Delete
    suspend fun deleteAssignment(assignment: AssignmentEntity)

    // --- Journal ---
    @Query("SELECT * FROM journal_entries ORDER BY dateMillis DESC")
    fun getAllJournalEntries(): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntryEntity): Long

    @Update
    suspend fun updateJournalEntry(entry: JournalEntryEntity)

    @Delete
    suspend fun deleteJournalEntry(entry: JournalEntryEntity)

    // --- Notes ---
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    // --- Books ---
    @Query("SELECT * FROM books ORDER BY id DESC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long

    @Update
    suspend fun updateBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    // --- Goals & Subtasks ---
    @Query("SELECT * FROM goals ORDER BY isCompleted ASC, id DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("SELECT * FROM goal_subtasks WHERE goalId = :goalId ORDER BY orderIndex ASC")
    fun getSubtasksForGoal(goalId: Long): Flow<List<GoalSubtaskEntity>>

    @Query("SELECT * FROM goal_subtasks")
    fun getAllGoalSubtasks(): Flow<List<GoalSubtaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoalSubtask(subtask: GoalSubtaskEntity): Long

    @Update
    suspend fun updateGoalSubtask(subtask: GoalSubtaskEntity)

    @Query("UPDATE goal_subtasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun setSubtaskCompleted(id: Long, isCompleted: Boolean)

    @Delete
    suspend fun deleteGoalSubtask(subtask: GoalSubtaskEntity)

    // --- Habits & Logs ---
    @Query("SELECT * FROM habits ORDER BY id ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("SELECT * FROM habit_logs WHERE dateString = :dateString")
    fun getHabitLogsForDate(dateString: String): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs")
    fun getAllHabitLogs(): Flow<List<HabitLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitLog(log: HabitLogEntity)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND dateString = :dateString")
    suspend fun deleteHabitLog(habitId: Long, dateString: String)

    // --- Transactions & Budgets ---
    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM budgets WHERE monthString = :monthString")
    fun getBudgetsForMonth(monthString: String): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    // --- Documents ---
    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity): Long

    @Delete
    suspend fun deleteDocument(document: DocumentEntity)

    // Clear all for demo reset
    @Query("DELETE FROM student_profile")
    suspend fun clearProfile()
    @Query("DELETE FROM subjects")
    suspend fun clearSubjects()
    @Query("DELETE FROM assignments")
    suspend fun clearAssignments()
    @Query("DELETE FROM syllabus_nodes")
    suspend fun clearSyllabusNodes()
    @Query("DELETE FROM journal_entries")
    suspend fun clearJournal()
    @Query("DELETE FROM notes")
    suspend fun clearNotes()
    @Query("DELETE FROM books")
    suspend fun clearBooks()
    @Query("DELETE FROM goals")
    suspend fun clearGoals()
    @Query("DELETE FROM goal_subtasks")
    suspend fun clearGoalSubtasks()
    @Query("DELETE FROM habits")
    suspend fun clearHabits()
    @Query("DELETE FROM habit_logs")
    suspend fun clearHabitLogs()
    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()
    @Query("DELETE FROM budgets")
    suspend fun clearBudgets()
    @Query("DELETE FROM documents")
    suspend fun clearDocuments()
}
