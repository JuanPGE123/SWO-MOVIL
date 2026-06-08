package com.example.swo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.swo.ui.screens.ChatbotScreen
import com.example.swo.ui.screens.DashboardScreen
import com.example.swo.ui.screens.HomeScreen
import com.example.swo.ui.screens.IncidentDetailScreen
import com.example.swo.ui.screens.IncidentsScreen
import com.example.swo.ui.screens.LoginScreen
import com.example.swo.ui.screens.ReportsScreen
import com.example.swo.ui.screens.ProjectsScreen
import com.example.swo.ui.screens.UserManagementScreen
import com.example.swo.ui.navigation.NavGraph
import com.example.swo.core.security.BiometricAuthManager
import com.example.swo.ui.theme.SWOTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.swo.data.local.DatabaseSeeder
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var biometricManager: BiometricAuthManager

    @Inject
    lateinit var databaseSeeder: DatabaseSeeder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            databaseSeeder.seedDatabase()
        }

        enableEdgeToEdge()
        setContent {
            SWOTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController, biometricManager = biometricManager)
            }
        }
    }
}

