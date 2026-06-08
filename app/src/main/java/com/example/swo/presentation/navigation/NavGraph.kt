package com.example.swo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.swo.presentation.features.incidents.IncidentsScreen
import com.example.swo.presentation.features.projects.ProjectsScreen
import com.example.swo.presentation.features.users.UsersScreen

sealed class Screen(val route: String) {
    object Incidents : Screen("incidents_list")
    object Users : Screen("users_list")
    object Projects : Screen("projects_list")
}

@Composable
fun SWONavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Incidents.route
    ) {
        composable(Screen.Incidents.route) {
            IncidentsScreen(onNavigateToDetail = { /* TODO */ })
        }
        composable(Screen.Users.route) {
            UsersScreen()
        }
        composable(Screen.Projects.route) {
            ProjectsScreen()
        }
    }
}
