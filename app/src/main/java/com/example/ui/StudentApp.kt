package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.components.AppBottomBar
import com.example.ui.components.AppTopBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.ambientBackground
import com.example.ui.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentApp(
    viewModel: StudentViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val screenTitle = when {
        currentRoute == Screen.Dashboard.route -> "Padi da Parama"
        currentRoute == Screen.Academics.route -> "Academics & Syllabus"
        currentRoute?.startsWith("subject_detail") == true -> "Course Details"
        currentRoute == Screen.Assignments.route -> "Tasks & Exams"
        currentRoute == Screen.Habits.route -> "Habit Tracker"
        currentRoute == Screen.Goals.route -> "Goals & Roadmap"
        currentRoute == Screen.Notes.route -> "Study Notes & Wiki"
        currentRoute == Screen.Journal.route -> "Daily Journal"
        currentRoute == Screen.Documents.route -> "Documents & Vault"
        currentRoute == Screen.Books.route -> "Reading Tracker"
        currentRoute == Screen.Finances.route -> "Student Finances"
        currentRoute == Screen.Search.route -> "Global Search"
        currentRoute == Screen.Settings.route -> "Profile & Settings"
        else -> "Padi da Parama"
    }

    val screenSubtitle = when (currentRoute) {
        Screen.Dashboard.route -> "Student Dashboard & Productivity Hub"
        Screen.Academics.route -> "Curriculum, Units & Credits"
        Screen.Assignments.route -> "Deadlines, Priority & Weights"
        Screen.Habits.route -> "Daily Consistency & Streaks"
        Screen.Goals.route -> "Short & Long-term Targets"
        Screen.Notes.route -> "Knowledge Graph & [[Backlinks]]"
        Screen.Journal.route -> "Mindset, Wins & Reflections"
        Screen.Documents.route -> "PDFs, Study Materials & Notes"
        Screen.Books.route -> "Library & Reading Progression"
        Screen.Finances.route -> "Expense Log & Category Budgets"
        Screen.Search.route -> "Unified Search across all modules"
        Screen.Settings.route -> "Academic Year, GPA & System"
        else -> null
    }

    val isRootScreen = currentRoute == Screen.Dashboard.route ||
            currentRoute == Screen.Academics.route ||
            currentRoute == Screen.Assignments.route ||
            currentRoute == Screen.Habits.route ||
            currentRoute == Screen.Finances.route

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .ambientBackground(),
        containerColor = Color.Transparent,
        topBar = {
            AppTopBar(
                title = screenTitle,
                subtitle = screenSubtitle,
                showBack = !isRootScreen,
                onBackClick = { navController.popBackStack() },
                onSearchClick = {
                    if (currentRoute != Screen.Search.route) {
                        navController.navigate(Screen.Search.route)
                    }
                },
                onSettingsClick = {
                    if (currentRoute != Screen.Settings.route) {
                        navController.navigate(Screen.Settings.route)
                    }
                }
            )
        },
        bottomBar = {
            AppBottomBar(
                currentRoute = currentRoute,
                onNavigate = { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(Screen.Dashboard.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onOpenMoreSheet = {}
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> navController.navigate(screen.route) },
                    onOpenSubject = { subjectId ->
                        navController.navigate(Screen.SubjectDetail.createRoute(subjectId))
                    }
                )
            }

            composable(Screen.Academics.route) {
                AcademicsScreen(
                    viewModel = viewModel,
                    onSubjectClick = { subjectId ->
                        navController.navigate(Screen.SubjectDetail.createRoute(subjectId))
                    }
                )
            }

            composable(
                route = Screen.SubjectDetail.route,
                arguments = listOf(navArgument("subjectId") { type = NavType.LongType })
            ) { backStackEntry ->
                val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: 0L
                SubjectDetailScreen(
                    subjectId = subjectId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Assignments.route) {
                AssignmentsScreen(viewModel = viewModel)
            }

            composable(Screen.Habits.route) {
                HabitsScreen(viewModel = viewModel)
            }

            composable(Screen.Goals.route) {
                GoalsScreen(viewModel = viewModel)
            }

            composable(Screen.Notes.route) {
                NotesScreen(viewModel = viewModel)
            }

            composable(Screen.Journal.route) {
                JournalScreen(viewModel = viewModel)
            }

            composable(Screen.Documents.route) {
                DocumentsScreen(viewModel = viewModel)
            }

            composable(Screen.Books.route) {
                BooksScreen(viewModel = viewModel)
            }

            composable(Screen.Finances.route) {
                FinancesScreen(viewModel = viewModel)
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> navController.navigate(screen.route) },
                    onOpenSubject = { subjectId ->
                        navController.navigate(Screen.SubjectDetail.createRoute(subjectId))
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
