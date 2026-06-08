package com.example.swo.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard/{userName}") {
        fun createRoute(userName: String) = "dashboard/${userName.replace(" ", "_")}"
    }
    object Settings : Screen("settings")
    object Projects : Screen("projects")
    object ProjectForm : Screen("project_form?projectId={projectId}") {
        fun createRoute(projectId: String?) =
            if (projectId != null) "project_form?projectId=$projectId" else "project_form"
    }
    object Users : Screen("users")
    object UserForm : Screen("user_form?userId={userId}") {
        fun createRoute(userId: String?) =
            if (userId != null) "user_form?userId=$userId" else "user_form"
    }
    object Incidents : Screen("incidents")
    object IncidentDetail : Screen("incident_detail/{incidentId}")
    object IncidentForm : Screen("incident_form?incidentId={incidentId}")
    object Login : Screen("login")
    object Reports : Screen("reports")
    object Chatbot : Screen("chatbot")
}
