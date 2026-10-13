package com.adityayadav.careerguidancecommunity.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.adityayadav.careerguidancecommunity.ui.screens.*
import com.adityayadav.careerguidancecommunity.viewmodel.QuizViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Quiz : Screen("quiz")
    object Results : Screen("results")
    object Recommendations : Screen("recommendations")
    object Colleges : Screen("colleges")
    object Scholarships : Screen("scholarships")
    object Parental : Screen("parental")
    object Dashboard : Screen("dashboard")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Saved : Screen("saved")
    object CollegeCompare : Screen("compare?ids={ids}") {
        fun createRoute(ids: List<String> = emptyList()) =
            if (ids.isNotEmpty()) "compare?ids=${ids.joinToString(",")}" else "compare"
    }
    object AiMentor : Screen("ai_mentor")
    object ApplicationTracker : Screen("application_tracker")
    object Community : Screen("community")

    object CollegeDetails : Screen("college/{collegeId}") {
        fun createRoute(id: String) = "college/$id"
    }
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {

    // Shared ViewModel (Quiz, Gamification, Recommendations)
    val quizViewModel: QuizViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        // -------------------------------------
        // HOME SCREEN
        // -------------------------------------
        composable(Screen.Home.route) {
            HomeScreen(
                onStartQuiz = { navController.navigate(Screen.Quiz.route) },
                onNavigate = { route -> safeNavigate(navController, route) }
            )
        }

        // -------------------------------------
        // QUIZ
        // -------------------------------------
        composable(Screen.Quiz.route) {
            QuizScreen(
                viewModel = quizViewModel,
                onComplete = {
                    // navigate to recommendations after quiz complete
                    navController.navigate(Screen.Recommendations.route)
                },
                onBackToHome = { navController.navigate(Screen.Home.route) }
            )
        }

        // -------------------------------------
        // RESULTS
        // -------------------------------------
        composable(Screen.Results.route) {
            ResultsScreen(
                viewModel = quizViewModel,
                onNavigate = { route -> safeNavigate(navController, route) }
            )
        }

        // -------------------------------------
        // RECOMMENDATIONS
        // -------------------------------------
        composable(Screen.Recommendations.route) {
            CareerRecommendationScreen(
                viewModel = quizViewModel,
                onNavigate = { route -> safeNavigate(navController, route) }
            )
        }

        // -------------------------------------
        // PROFILE
        // -------------------------------------
        composable(Screen.Profile.route) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }
        // -------------------------------------
        // SETTINGS
        // -------------------------------------
        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() }, quizViewModel = quizViewModel)
        }
        // -------------------------------------
        // COLLEGES (new ViewModel inside screen)
        // -------------------------------------
        composable(Screen.Colleges.route) {
            CollegesScreen(
                onExploreColleges = {
                    // gamification updates
                    quizViewModel.markExploredColleges()
                    quizViewModel.addPoints(30)
                    quizViewModel.addBadge("College Explorer")
                },
                onNavigate = { route -> safeNavigate(navController, route) }
            )
        }

        // -------------------------------------
        // COLLEGE DETAILS (uses argument collegeId)
        // -------------------------------------
        composable(
            route = Screen.CollegeDetails.route,
            arguments = listOf(navArgument("collegeId") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("collegeId")
            CollegeDetailsScreen(
                collegeId = id,
                onBack = { navController.popBackStack() },
                onNavigate = { route -> safeNavigate(navController, route) }
            )
        }

        // -------------------------------------
        // SCHOLARSHIPS
        // -------------------------------------
        composable(Screen.Scholarships.route) {
            // reward user for visiting scholarships
            quizViewModel.markCheckedScholarships()
            quizViewModel.addPoints(40)
            quizViewModel.addBadge("Scholarship Seeker")

            ScholarshipScreen(
                onBack = { navController.popBackStack() },
                onNavigate = { route -> safeNavigate(navController, route) }
            )
        }

        // -------------------------------------
        // SAVED OPPORTUNITIES
        // -------------------------------------
        composable(Screen.Saved.route) {
            SavedScreen(
                onBack = { navController.popBackStack() },
                onNavigate = { route -> safeNavigate(navController, route) }
            )
        }

        // -------------------------------------
        // COMPARE COLLEGES
        // -------------------------------------
        composable(
            route = "compare?ids={ids}",
            arguments = listOf(
                navArgument("ids") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { entry ->
            val idsParam = entry.arguments?.getString("ids")
            val ids = idsParam?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
            CollegeComparisonScreen(
                initialIds = ids,
                onBack = { navController.popBackStack() },
                onNavigateToDetails = { id -> navController.navigate("college/$id") }
            )
        }
        composable("compare") {
            CollegeComparisonScreen(
                initialIds = emptyList(),
                onBack = { navController.popBackStack() },
                onNavigateToDetails = { id -> navController.navigate("college/$id") }
            )
        }

        // -------------------------------------
        // PARENTAL AWARENESS
        // -------------------------------------
        composable(Screen.Parental.route) {
            ParentalScreen(
                onNavigate = { route -> safeNavigate(navController, route) }
            )
        }

        // -------------------------------------
        // GAMIFICATION DASHBOARD
        // -------------------------------------
        composable(Screen.Dashboard.route) {
            GamificationDashboard(
                viewModel = quizViewModel,
                onNavigate = { route -> safeNavigate(navController, route) },
                onBack = { navController.popBackStack() }
            )
        }

        // -------------------------------------
        // AI CAREER MENTOR
        // -------------------------------------
        composable(Screen.AiMentor.route) {
            AiCareerMentorScreen(
                onBack = { navController.popBackStack() },
                onNavigate = { route -> safeNavigate(navController, route) }
            )
        }

        // -------------------------------------
        // SCHOLARSHIP APPLICATION TRACKER
        // -------------------------------------
        composable(Screen.ApplicationTracker.route) {
            ApplicationTrackerScreen(
                onBack = { navController.popBackStack() },
                onNavigate = { route -> safeNavigate(navController, route) }
            )
        }

        // -------------------------------------
        // PEER COMMUNITY & MENTORSHIP Q&A
        // -------------------------------------
        composable(Screen.Community.route) {
            CommunityScreen(
                onBack = { navController.popBackStack() },
                onNavigate = { route -> safeNavigate(navController, route) }
            )
        }
    }
}

/** Helper to avoid accidental empty-route navigation calls. */
private fun safeNavigate(navController: NavHostController, route: String) {
    if (route.isNotBlank()) navController.navigate(route)
}
