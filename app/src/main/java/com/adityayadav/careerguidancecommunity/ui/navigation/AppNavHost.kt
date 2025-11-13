package com.adityayadav.careerguidancecommunity.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.adityayadav.careerguidancecommunity.ui.screens.CareerRecommendationScreen
import com.adityayadav.careerguidancecommunity.ui.screens.CollegeDetailsScreen
import com.adityayadav.careerguidancecommunity.ui.screens.CollegesScreen
import com.adityayadav.careerguidancecommunity.ui.screens.GamificationDashboard
import com.adityayadav.careerguidancecommunity.ui.screens.HomeScreen
import com.adityayadav.careerguidancecommunity.ui.screens.ParentalScreen
import com.adityayadav.careerguidancecommunity.ui.screens.QuizScreen
import com.adityayadav.careerguidancecommunity.ui.screens.ResultsScreen
import com.adityayadav.careerguidancecommunity.ui.screens.ScholarshipScreen
import com.adityayadav.careerguidancecommunity.viewmodel.QuizViewModel

sealed class Screen(val route: String) {
    object Home: Screen("home")
    object Quiz: Screen("quiz")
    object Results: Screen("results")
    object Recommendations: Screen("recommendations")
    object Colleges: Screen("colleges")
    object Scholarships: Screen("scholarships")
    object Parental: Screen("parental")
    object Dashboard: Screen("dashboard")
    object CollegeDetails: Screen("college/{collegeId}") {
        fun createRoute(id: String) = "college/$id"
    }
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    // activity-scoped ViewModel (shared state & gamification)
    val quizViewModel: QuizViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        // Home
        composable(Screen.Home.route) {
            HomeScreen(
                onStartQuiz = { navController.navigate(Screen.Quiz.route) },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        // Quiz
        composable(Screen.Quiz.route) {
            QuizScreen(
                viewModel = quizViewModel,
                onComplete = {
                    // submit handled inside QuizScreen or here if you prefer
                    navController.navigate(Screen.Recommendations.route)
                },
                onBackToHome = { navController.navigate(Screen.Home.route) }
            )
        }

        // Results
        composable(Screen.Results.route) {
            ResultsScreen(
                viewModel = quizViewModel,
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        // Recommendations
        composable(Screen.Recommendations.route) {
            CareerRecommendationScreen(
                viewModel = quizViewModel,
                onNavigate = { route -> nav_controller_safe_navigate(navController, route) }
            )
        }

        // Colleges (let the screen create its own CollegesViewModel)
        composable(Screen.Colleges.route) {
            CollegesScreen(
                // do gamification updates when user explores colleges
                onExploreColleges = {
                    quizViewModel.markExploredColleges()
                    quizViewModel.addPoints(30)
                    quizViewModel.addBadge("College Explorer")
                },
                onNavigate = { route -> nav_controller_safe_navigate(navController, route) }
            )
        }

        // Scholarships
        composable(Screen.Scholarships.route) {
            // Optional: reward user for opening scholarship list
            quizViewModel.markCheckedScholarships()
            quizViewModel.addPoints(40)
            quizViewModel.addBadge("Scholarship Seeker")

            ScholarshipScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Parental Awareness
        composable(Screen.Parental.route) {
            ParentalScreen(onNavigate = { route -> nav_controller_safe_navigate(navController, route) })
        }

        // Dashboard
        composable(Screen.Dashboard.route) {
            GamificationDashboard(
                viewModel = quizViewModel,
                onNavigate = { route -> nav_controller_safe_navigate(navController, route) }
            )
        }

        // College details (reads collegeId arg)
        composable(
            route = Screen.CollegeDetails.route,
            arguments = listOf(navArgument("collegeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("collegeId")
            CollegeDetailsScreen(collegeId = id, onBack = { navController.popBackStack() })
        }
    }
}

/**
 * Small helper: navigate only when route is non-empty (prevents accidental empty-route crashes).
 * Keeps navigation calls consistent across the NavHost.
 */
private fun nav_controller_safe_navigate(navController: NavHostController, route: String) {
    if (route.isNotBlank()) navController.navigate(route)
}
