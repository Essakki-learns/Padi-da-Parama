package com.example.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Dashboard : Screen("dashboard", "Dashboard")
    object Academics : Screen("academics", "Academics & Syllabus")
    object SubjectDetail : Screen("subject_detail/{subjectId}", "Subject Overview") {
        fun createRoute(subjectId: Long) = "subject_detail/$subjectId"
    }
    object Assignments : Screen("assignments", "Tasks & Exams")
    object Notes : Screen("notes", "Notes & Backlinks")
    object Habits : Screen("habits", "Habits & Streaks")
    object Goals : Screen("goals", "Goals & Roadmap")
    object Books : Screen("books", "Reading Tracker")
    object Finances : Screen("finances", "Finances & Budget")
    object Journal : Screen("journal", "Daily Journal")
    object Search : Screen("search", "Search Everything")
    object Settings : Screen("settings", "Settings & Profile")
}
