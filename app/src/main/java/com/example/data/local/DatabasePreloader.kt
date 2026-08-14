package com.example.data.local

import com.example.data.local.dao.StudentDao
import com.example.data.local.entities.*
import java.text.SimpleDateFormat
import java.util.*

object DatabasePreloader {

    suspend fun seedInitialData(dao: StudentDao) {
        // Profile
        dao.insertProfile(
            StudentProfileEntity(
                id = 1L,
                name = "Parama Sundaram",
                email = "parama.s@skcet.ac.in",
                college = "SKCET Coimbatore",
                year = 3,
                major = "Computer Science & Engineering",
                targetGpa = 9.5,
                currentGpa = 9.14,
                semester = "Semester 6"
            )
        )

        // Subjects
        val sub1Id = dao.insertSubject(
            SubjectEntity(
                name = "Cloud Computing & DevOps",
                code = "CS8601",
                instructor = "Dr. S. K. Ramesh",
                credits = 4,
                colorHex = "#4338CA",
                semester = "Semester 6",
                targetGrade = "O (Outstanding)",
                achievedGrade = "A+"
            )
        )

        val sub2Id = dao.insertSubject(
            SubjectEntity(
                name = "Machine Learning & Neural Networks",
                code = "CS8602",
                instructor = "Prof. Ananya Sen",
                credits = 4,
                colorHex = "#059669",
                semester = "Semester 6",
                targetGrade = "O (Outstanding)",
                achievedGrade = null
            )
        )

        val sub3Id = dao.insertSubject(
            SubjectEntity(
                name = "Mobile Application Development",
                code = "CS8603",
                instructor = "Dr. Vigneshwar M",
                credits = 3,
                colorHex = "#D97706",
                semester = "Semester 6",
                targetGrade = "A+",
                achievedGrade = null
            )
        )

        val sub4Id = dao.insertSubject(
            SubjectEntity(
                name = "Distributed Database Systems",
                code = "CS8604",
                instructor = "Prof. Karthikeyan",
                credits = 3,
                colorHex = "#7C3AED",
                semester = "Semester 6",
                targetGrade = "A+",
                achievedGrade = null
            )
        )

        // Syllabus Nodes for Cloud Computing
        val u1 = dao.insertSyllabusNode(
            SyllabusNodeEntity(
                subjectId = sub1Id,
                name = "Unit 1: Virtualization & Hypervisors",
                type = "UNIT",
                status = "COMPLETED",
                orderIndex = 1
            )
        )
        dao.insertSyllabusNode(
            SyllabusNodeEntity(
                subjectId = sub1Id,
                name = "KVM vs Xen Architectural Analysis",
                type = "TOPIC",
                status = "COMPLETED",
                parentId = u1,
                orderIndex = 2
            )
        )
        dao.insertSyllabusNode(
            SyllabusNodeEntity(
                subjectId = sub1Id,
                name = "Hardware-Assisted Virtualization (Intel VT-x)",
                type = "TOPIC",
                status = "COMPLETED",
                parentId = u1,
                orderIndex = 3
            )
        )

        val u2 = dao.insertSyllabusNode(
            SyllabusNodeEntity(
                subjectId = sub1Id,
                name = "Unit 2: Containerization & Kubernetes",
                type = "UNIT",
                status = "IN_PROGRESS",
                orderIndex = 4
            )
        )
        dao.insertSyllabusNode(
            SyllabusNodeEntity(
                subjectId = sub1Id,
                name = "Docker Engine Cgroups & Namespaces",
                type = "TOPIC",
                status = "COMPLETED",
                parentId = u2,
                orderIndex = 5
            )
        )
        dao.insertSyllabusNode(
            SyllabusNodeEntity(
                subjectId = sub1Id,
                name = "K8s Pod Scheduling & ReplicaSets",
                type = "TOPIC",
                status = "IN_PROGRESS",
                parentId = u2,
                orderIndex = 6
            )
        )
        dao.insertSyllabusNode(
            SyllabusNodeEntity(
                subjectId = sub1Id,
                name = "Ingress Controllers & Service Meshes",
                type = "TOPIC",
                status = "NOT_STARTED",
                parentId = u2,
                orderIndex = 7
            )
        )

        val u3 = dao.insertSyllabusNode(
            SyllabusNodeEntity(
                subjectId = sub1Id,
                name = "Unit 3: CI/CD Pipelines & Terraform",
                type = "UNIT",
                status = "NOT_STARTED",
                orderIndex = 8
            )
        )

        // Syllabus for ML
        val mlu1 = dao.insertSyllabusNode(
            SyllabusNodeEntity(
                subjectId = sub2Id,
                name = "Unit 1: Supervised Learning & Gradient Descent",
                type = "UNIT",
                status = "COMPLETED",
                orderIndex = 1
            )
        )
        dao.insertSyllabusNode(
            SyllabusNodeEntity(
                subjectId = sub2Id,
                name = "Cost Function Optimization & Learning Rates",
                type = "TOPIC",
                status = "COMPLETED",
                parentId = mlu1,
                orderIndex = 2
            )
        )
        val mlu2 = dao.insertSyllabusNode(
            SyllabusNodeEntity(
                subjectId = sub2Id,
                name = "Unit 2: Convolutional Neural Networks (CNN)",
                type = "UNIT",
                status = "IN_PROGRESS",
                orderIndex = 3
            )
        )
        dao.insertSyllabusNode(
            SyllabusNodeEntity(
                subjectId = sub2Id,
                name = "Backpropagation Through Time & ResNet Skip Connections",
                type = "TOPIC",
                status = "IN_PROGRESS",
                parentId = mlu2,
                orderIndex = 4
            )
        )

        // Assignments & Exams
        val now = System.currentTimeMillis()
        val dayMillis = 24 * 60 * 60 * 1000L

        dao.insertAssignment(
            AssignmentEntity(
                subjectId = sub1Id,
                title = "K8s Microservices Deployment Lab",
                description = "Deploy a 3-tier microservice on Minikube with Helm charts and load balancer configuration.",
                type = "ASSIGNMENT",
                dueDate = now + (2 * dayMillis),
                priority = "HIGH",
                estimatedHours = 3.5,
                weightage = 10.0,
                isComplete = false
            )
        )

        dao.insertAssignment(
            AssignmentEntity(
                subjectId = sub2Id,
                title = "Mid-Term Examination: Neural Architectures",
                description = "Covers Units 1 & 2: Loss formulations, Backprop, Convolution math, Attention mechanism.",
                type = "EXAM",
                dueDate = now + (5 * dayMillis),
                priority = "HIGH",
                estimatedHours = 12.0,
                weightage = 30.0,
                isComplete = false
            )
        )

        dao.insertAssignment(
            AssignmentEntity(
                subjectId = sub3Id,
                title = "Compose Navigation & Room Architecture Assignment",
                description = "Implement single-activity modern Jetpack Compose architecture with Room StateFlow observation.",
                type = "ASSIGNMENT",
                dueDate = now + (4 * dayMillis),
                priority = "MEDIUM",
                estimatedHours = 4.0,
                weightage = 15.0,
                isComplete = true
            )
        )

        dao.insertAssignment(
            AssignmentEntity(
                subjectId = sub4Id,
                title = "Two-Phase Commit Protocol Analysis",
                description = "Comparative case study on Paxos vs Raft consensus algorithms.",
                type = "ASSIGNMENT",
                dueDate = now + (8 * dayMillis),
                priority = "LOW",
                estimatedHours = 2.5,
                weightage = 10.0,
                isComplete = false
            )
        )

        // Habits
        val h1 = dao.insertHabit(
            HabitEntity(
                name = "Deep Study Focus (2 Pomodoros)",
                frequency = "DAILY",
                goal = 50,
                unit = "mins",
                colorHex = "#4338CA",
                icon = "⏱️"
            )
        )
        val h2 = dao.insertHabit(
            HabitEntity(
                name = "Solve 2 LeetCode Problems",
                frequency = "DAILY",
                goal = 2,
                unit = "problems",
                colorHex = "#059669",
                icon = "💻"
            )
        )
        val h3 = dao.insertHabit(
            HabitEntity(
                name = "Read Research Papers / Book",
                frequency = "DAILY",
                goal = 20,
                unit = "pages",
                colorHex = "#D97706",
                icon = "📖"
            )
        )
        val h4 = dao.insertHabit(
            HabitEntity(
                name = "Gym & Physical Training",
                frequency = "DAILY",
                goal = 45,
                unit = "mins",
                colorHex = "#DC2626",
                icon = "🏋️"
            )
        )

        // Habit logs for today and recent days
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdf.format(Date(now))
        val yesterdayStr = sdf.format(Date(now - dayMillis))
        val twoDaysAgoStr = sdf.format(Date(now - 2 * dayMillis))
        val threeDaysAgoStr = sdf.format(Date(now - 3 * dayMillis))

        dao.insertHabitLog(HabitLogEntity(habitId = h1, dateString = todayStr, value = 50, completed = true))
        dao.insertHabitLog(HabitLogEntity(habitId = h1, dateString = yesterdayStr, value = 50, completed = true))
        dao.insertHabitLog(HabitLogEntity(habitId = h1, dateString = twoDaysAgoStr, value = 50, completed = true))
        dao.insertHabitLog(HabitLogEntity(habitId = h2, dateString = todayStr, value = 2, completed = true))
        dao.insertHabitLog(HabitLogEntity(habitId = h2, dateString = yesterdayStr, value = 2, completed = true))
        dao.insertHabitLog(HabitLogEntity(habitId = h3, dateString = yesterdayStr, value = 20, completed = true))
        dao.insertHabitLog(HabitLogEntity(habitId = h4, dateString = threeDaysAgoStr, value = 45, completed = true))

        // Goals
        val g1 = dao.insertGoal(
            GoalEntity(
                title = "Maintain 9.5+ GPA for 6th Semester",
                description = "Ace internal assessments and lab evaluations in Cloud and ML courses.",
                category = "ACADEMIC",
                timeframe = "SHORT_TERM",
                targetDate = now + (60 * dayMillis),
                progress = 66.6f,
                isCompleted = false
            )
        )
        dao.insertGoalSubtask(GoalSubtaskEntity(goalId = g1, title = "Score 95%+ in Internal Assessment 1", isCompleted = true, orderIndex = 1))
        dao.insertGoalSubtask(GoalSubtaskEntity(goalId = g1, title = "Complete Cloud Computing Lab Experiments", isCompleted = true, orderIndex = 2))
        dao.insertGoalSubtask(GoalSubtaskEntity(goalId = g1, title = "Finish ML Neural Net Mini Project", isCompleted = false, orderIndex = 3))

        val g2 = dao.insertGoal(
            GoalEntity(
                title = "Crack Product Company Software Engineering Internship",
                description = "Master DSA patterns, system design fundamentals, and mock interviews.",
                category = "CAREER",
                timeframe = "LONG_TERM",
                targetDate = now + (120 * dayMillis),
                progress = 50f,
                isCompleted = false
            )
        )
        dao.insertGoalSubtask(GoalSubtaskEntity(goalId = g2, title = "Complete NeetCode 150 Blind 75", isCompleted = true, orderIndex = 1))
        dao.insertGoalSubtask(GoalSubtaskEntity(goalId = g2, title = "Build full-stack Android & Cloud App", isCompleted = true, orderIndex = 2))
        dao.insertGoalSubtask(GoalSubtaskEntity(goalId = g2, title = "Conduct 5 Peer Mock Interviews", isCompleted = false, orderIndex = 3))
        dao.insertGoalSubtask(GoalSubtaskEntity(goalId = g2, title = "Resume Review with College Seniors", isCompleted = false, orderIndex = 4))

        // Books
        dao.insertBook(
            BookEntity(
                title = "Designing Data-Intensive Applications",
                author = "Martin Kleppmann",
                genre = "Systems & Computer Science",
                status = "READING",
                rating = 5,
                progress = 62,
                pageCount = 560,
                currentPage = 348,
                notes = "Brilliant breakdown of replication lag, consensus algorithms, and stream processing architectures.",
                startedAt = now - (14 * dayMillis)
            )
        )
        dao.insertBook(
            BookEntity(
                title = "Deep Work: Rules for Focused Success",
                author = "Cal Newport",
                genre = "Productivity & Self-Growth",
                status = "FINISHED",
                rating = 5,
                progress = 100,
                pageCount = 304,
                currentPage = 304,
                notes = "Key takeaway: Monastic and bimodal scheduling prevents attention residue and 10x output.",
                finishedAt = now - (5 * dayMillis)
            )
        )
        dao.insertBook(
            BookEntity(
                title = "Clean Architecture",
                author = "Robert C. Martin",
                genre = "Software Engineering",
                status = "WANT_TO_READ",
                rating = 0,
                progress = 0,
                pageCount = 432,
                currentPage = 0,
                notes = "Recommended by college mentor for building scalable decoupled layers."
            )
        )

        // Notes with [[backlinks]]
        dao.insertNote(
            NoteEntity(
                title = "Kubernetes Pod Scheduling Architecture",
                content = "Pods in K8s are scheduled via kube-scheduler based on node affinity, taints, and resource limits.\nRelated to [[Cloud Computing & DevOps]] and [[Docker Engine Cgroups]].\nImportant for upcoming lab test.",
                tags = "Cloud,Kubernetes,DevOps"
            )
        )
        dao.insertNote(
            NoteEntity(
                title = "Gradient Descent & Adam Optimizer Equations",
                content = "Adam combines AdaGrad and RMSProp with momentum estimation:\nm_t = beta1 * m_{t-1} + (1-beta1) * g_t\nv_t = beta2 * v_{t-1} + (1-beta2) * g_t^2\nRefer to [[Machine Learning & Neural Networks]].",
                tags = "AI,Math,NeuralNetworks"
            )
        )
        dao.insertNote(
            NoteEntity(
                title = "Semester 6 Exam Preparation Roadmap",
                content = "Target: 9.5+ GPA.\n1. Review Unit 1 & 2 syllabus notes in [[Cloud Computing & DevOps]].\n2. Complete [[Designing Data-Intensive Applications]] chapters on transactions.",
                tags = "Exams,Strategy,Roadmap"
            )
        )

        // Journal
        dao.insertJournalEntry(
            JournalEntryEntity(
                title = "Productive Day at the Tech Lab",
                content = "Spent 4 hours in the cloud computing lab finishing the Kubernetes deployment setup. The teacher appreciated the clean YAML configuration.",
                mood = "🚀 Inspired",
                dateMillis = now - (1 * dayMillis),
                wentWell = "Configured multi-pod ingress without errors and helped my classmate with Docker network bridge.",
                improve = "Start the ML assignment earlier to avoid last-minute rush.",
                gratitude = "Grateful for good coffee and an uninterrupted quiet study room in the library.",
                tags = "College,Coding,Wins"
            )
        )
        dao.insertJournalEntry(
            JournalEntryEntity(
                title = "Weekly Review & Momentum Reset",
                content = "Completed 12 LeetCode questions this week and maintained a 4-day habit streak. Need to prioritize sleep schedule.",
                mood = "⚡ Energetic",
                dateMillis = now,
                wentWell = "Finished the Android Room persistence layer smoothly.",
                improve = "Reduce social media screen time before bed.",
                gratitude = "Supportive project team members and great professor guidance.",
                tags = "Reflections,Weekly"
            )
        )

        // Finances & Budgets
        val monthStr = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date(now))
        dao.insertBudget(BudgetEntity(category = "Food & Canteen", limitAmount = 4500.0, monthString = monthStr))
        dao.insertBudget(BudgetEntity(category = "Books & Stationery", limitAmount = 2000.0, monthString = monthStr))
        dao.insertBudget(BudgetEntity(category = "Tech & Subscriptions", limitAmount = 1500.0, monthString = monthStr))
        dao.insertBudget(BudgetEntity(category = "Travel & Metro", limitAmount = 1200.0, monthString = monthStr))

        dao.insertTransaction(
            TransactionEntity(
                type = "EXPENSE",
                amount = 180.0,
                category = "Food & Canteen",
                account = "UPI / GPay",
                description = "Campus Cafeteria Lunch & Filter Coffee",
                dateMillis = now - (2 * 60 * 60 * 1000L)
            )
        )
        dao.insertTransaction(
            TransactionEntity(
                type = "EXPENSE",
                amount = 650.0,
                category = "Books & Stationery",
                account = "UPI / GPay",
                description = "Engineering Notebooks & Graph Paper",
                dateMillis = now - (1 * dayMillis)
            )
        )
        dao.insertTransaction(
            TransactionEntity(
                type = "EXPENSE",
                amount = 499.0,
                category = "Tech & Subscriptions",
                account = "Card",
                description = "GitHub Copilot Student Renewal",
                dateMillis = now - (3 * dayMillis)
            )
        )
        dao.insertTransaction(
            TransactionEntity(
                type = "INCOME",
                amount = 8000.0,
                category = "Monthly Allowance",
                account = "Bank Account",
                description = "Monthly College Stipend / Allowance",
                dateMillis = now - (10 * dayMillis)
            )
        )

        // Documents
        dao.insertDocument(
            DocumentEntity(
                title = "CS8601 Cloud Computing Lab Manual 2026",
                originalName = "Cloud_Lab_Manual_SKCET.pdf",
                mimeType = "application/pdf",
                sizeBytes = 1024 * 1024 * 3,
                subjectId = sub1Id,
                fileUriOrNotes = "Official syllabus and step-by-step experiment instructions."
            )
        )
        dao.insertDocument(
            DocumentEntity(
                title = "ML Formula Cheat Sheet & Backprop Equations",
                originalName = "ML_Formulas_Summary.pdf",
                mimeType = "application/pdf",
                sizeBytes = 1024 * 850,
                subjectId = sub2Id,
                fileUriOrNotes = "Comprehensive summary of matrix derivatives, cost functions, and optimizers."
            )
        )
    }
}
