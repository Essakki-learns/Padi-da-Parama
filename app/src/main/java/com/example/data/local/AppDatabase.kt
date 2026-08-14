package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.StudentDao
import com.example.data.local.entities.*

@Database(
    entities = [
        StudentProfileEntity::class,
        SubjectEntity::class,
        SyllabusNodeEntity::class,
        AssignmentEntity::class,
        JournalEntryEntity::class,
        NoteEntity::class,
        BookEntity::class,
        GoalEntity::class,
        GoalSubtaskEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        DocumentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_dashboard_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
