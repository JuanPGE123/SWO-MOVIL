package com.example.swo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.swo.ui.dashboard.DashboardScreen
import com.example.swo.ui.dashboard.SettingsScreen
import com.example.swo.ui.projects.ProjectFormScreen
import com.example.swo.ui.projects.ProjectViewModel
import com.example.swo.ui.screens.ProjectsScreen
import com.example.swo.ui.reports.ReportsScreen
import com.example.swo.ui.reports.ReportsViewModel
import com.example.swo.ui.chatbot.ChatbotScreen
import com.example.swo.ui.chatbot.ChatViewModel
import com.example.swo.ui.screens.LoginScreen
import com.example.swo.ui.screens.IncidentsScreen
import com.example.swo.ui.screens.IncidentDetailScreen
import com.example.swo.ui.incidents.IncidentFormScreen
import com.example.swo.ui.incidents.IncidentFormViewModel
import com.example.swo.ui.users.UserFormScreen
import com.example.swo.ui.users.UserViewModel
import com.example.swo.ui.users.UsersScreen
import com.example.swo.core.security.BiometricAuthManager

@Composable
fun NavGraph(navController: NavHostController, biometricManager: BiometricAuthManager) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                biometricManager = biometricManager,
                onLoginSuccess = { user ->
                    navController.navigate(Screen.Dashboard.createRoute(user.name)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Dashboard.route,
            arguments = listOf(navArgument("userName") {
                type = NavType.StringType
                defaultValue = "Usuario"
            })
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName")
                ?.replace("_", " ") ?: "Usuario"
            DashboardScreen(
                userName = userName,
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToIncidents = { navController.navigate(Screen.Incidents.route) },
                onNavigateToProjects = { navController.navigate(Screen.Projects.route) },
                onNavigateToUsers = { navController.navigate(Screen.Users.route) },
                onNavigateToReports = { navController.navigate(Screen.Reports.route) },
                onNavigateToChatbot = { navController.navigate(Screen.Chatbot.route) }
            )
        }

        composable(Screen.Projects.route) {
            val viewModel: ProjectViewModel = hiltViewModel()
            ProjectsScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.Incidents.route) {
            IncidentsScreen(navController = navController)
        }

        composable(Screen.IncidentDetail.route) { backStackEntry ->
            val incidentId = backStackEntry.arguments?.getString("incidentId")
            IncidentDetailScreen(navController = navController, incidentId = incidentId)
        }

        composable(
            route = Screen.IncidentForm.route,
            arguments = listOf(navArgument("incidentId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            val viewModel: IncidentFormViewModel = hiltViewModel()
            IncidentFormScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.ProjectForm.route,
            arguments = listOf(navArgument("projectId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")
            val viewModel: ProjectViewModel = hiltViewModel()
            ProjectFormScreen(
                viewModel = viewModel,
                projectId = projectId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Users.route) {
            val viewModel: UserViewModel = hiltViewModel()
            UsersScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToUserForm = { userId ->
                    navController.navigate(Screen.UserForm.createRoute(userId))
                }
            )
        }

        composable(
            route = Screen.UserForm.route,
            arguments = listOf(navArgument("userId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            val viewModel: UserViewModel = hiltViewModel()
            UserFormScreen(
                viewModel = viewModel,
                userId = userId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Reports.route) {
            val viewModel: ReportsViewModel = hiltViewModel()
            ReportsScreen(onBack = { navController.popBackStack() }, viewModel = viewModel)
        }

        composable(Screen.Chatbot.route) {
            val viewModel: ChatViewModel = hiltViewModel()
            ChatbotScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }
}
