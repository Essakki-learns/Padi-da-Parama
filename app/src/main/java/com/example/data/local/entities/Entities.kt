package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "student_profile")
data class StudentProfileEntity(
    @PrimaryKey val id: Long = 1L,
    val name: String = "Parama",
    val email: String = "parama@university.edu",
    val college: String = "SKCET",
    val year: Int = 3,
    val major: String = "Computer Science & Engineering",
    val targetGpa: Double = 9.5,
    val currentGpa: Double = 8.92,
    val semester: String = "Semester 6"
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val code: String? = null,
    val instructor: String? = null,
    val credits: Int = 3,
    val colorHex: String = "#4338CA",
    val semester: String = "Semester 6",
    val year: String = "Year 3",
    val targetGrade: String = "A+",
    val achievedGrade: String? = null
)

@Entity(
    tableName = "syllabus_nodes",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subjectId"]), Index(value = ["parentId"])]
)
data class SyllabusNodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val subjectId: Long,
    val name: String,
    val type: String = "UNIT", // UNIT, CHAPTER, TOPIC
    val status: String = "NOT_STARTED", // NOT_STARTED, IN_PROGRESS, COMPLETED
    val parentId: Long? = null,
    val orderIndex: Int = 0
)

@Entity(
    tableName = "assignments",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subjectId"]), Index(value = ["dueDate"])]
)
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val subjectId: Long,
    val title: String,
    val description: String? = null,
    val type: String = "ASSIGNMENT", // ASSIGNMENT, EXAM
    val dueDate: Long, // Epoch millis
    val priority: String = "MEDIUM", // LOW, MEDIUM, HIGH
    val estimatedHours: Double = 2.0,
    val weightage: Double = 15.0, // Exam percentage or assignment weight
    val syllabusCoverage: String? = null,
    val isComplete: Boolean = false
)

@Entity(tableName = "journal_entries", indices = [Index(value = ["dateMillis"])])
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String = "",
    val content: String,
    val mood: String = "⚡ Energetic",
    val dateMillis: Long = System.currentTimeMillis(),
    val wentWell: String = "",
    val improve: String = "",
    val gratitude: String = "",
    val tags: String = "", // Comma-separated tags
    val isDraft: Boolean = false
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val content: String,
    val tags: String = "", // Comma-separated tags
    val isArchived: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val author: String? = null,
    val genre: String? = null,
    val status: String = "WANT_TO_READ", // WANT_TO_READ, READING, FINISHED
    val rating: Int = 0, // 1 to 5
    val progress: Int = 0, // 0 to 100%
    val notes: String = "",
    val startedAt: Long? = null,
    val finishedAt: Long? = null,
    val pageCount: Int = 320,
    val currentPage: Int = 0
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val description: String? = null,
    val category: String = "ACADEMIC", // ACADEMIC, FINANCIAL, HEALTH, HOBBY, CAREER, PERSONAL
    val timeframe: String = "SHORT_TERM", // SHORT_TERM, LONG_TERM
    val targetDate: Long? = null,
    val progress: Float = 0f, // 0 to 100
    val isCompleted: Boolean = false
)

@Entity(
    tableName = "goal_subtasks",
    foreignKeys = [
        ForeignKey(
            entity = GoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["goalId"])]
)
data class GoalSubtaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val goalId: Long,
    val title: String,
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val frequency: String = "DAILY", // DAILY, WEEKLY, MONTHLY
    val goal: Int = 1,
    val unit: String = "times",
    val colorHex: String = "#6366F1",
    val icon: String = "🔥"
)

@Entity(
    tableName = "habit_logs",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["habitId", "dateString"], unique = true), Index(value = ["habitId"])]
)
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val habitId: Long,
    val dateString: String, // YYYY-MM-DD
    val value: Int = 1,
    val completed: Boolean = true
)

@Entity(tableName = "transactions", indices = [Index(value = ["dateMillis"]), Index(value = ["category"])])
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val type: String = "EXPENSE", // INCOME, EXPENSE
    val amount: Double,
    val category: String,
    val account: String = "UPI / GPay", // UPI, Bank, Cash, Card
    val paymentMethod: String = "UPI",
    val description: String? = null,
    val dateMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "budgets",
    indices = [Index(value = ["category", "monthString"], unique = true)]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val category: String,
    val limitAmount: Double,
    val monthString: String = "2026-08" // YYYY-MM
)

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val originalName: String,
    val mimeType: String = "application/pdf",
    val sizeBytes: Long = 1024 * 500,
    val subjectId: Long? = null,
    val fileUriOrNotes: String = "",
    val linkedType: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
