package com.example.swo.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.swo.core.designsystem.components.SWOCard
import com.example.swo.domain.model.Project
import com.example.swo.domain.model.User
import com.example.swo.domain.model.UserRole
import com.example.swo.ui.components.ProjectCardShimmer
import com.example.swo.ui.navigation.Screen
import com.example.swo.ui.projects.ProjectViewModel
import com.example.swo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(navController: NavController, viewModel: ProjectViewModel) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var projectToDelete by remember { mutableStateOf<Project?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearMessages()
        }
    }

    val filteredProjects = if (searchQuery.isBlank()) state.projects
    else state.projects.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
            it.description.contains(searchQuery, ignoreCase = true)
    }

    // Delete confirmation dialog
    projectToDelete?.let { project ->
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            icon = { Icon(Icons.Default.DeleteForever, null, tint = Color(0xFFEF4444)) },
            title = { Text("Eliminar proyecto", color = Color.White) },
            text = { Text("¿Eliminar \"${project.name}\"? Esta acción no se puede deshacer.", color = Color.Gray) },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteProject(project.id); projectToDelete = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) { Text("Eliminar") }
            },
            dismissButton = {
                OutlinedButton(onClick = { projectToDelete = null }) { Text("Cancelar") }
            },
            containerColor = SurfaceDark
        )
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(DarkBackground_Slate)) {
                TopAppBar(
                    title = {
                        Column {
                            Text("Proyectos", color = Color.White, fontWeight = FontWeight.Bold)
                            Text(
                                "${state.projects.size} proyecto${if (state.projects.size != 1) "s" else ""}",
                                color = Color.Gray, fontSize = 12.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar proyectos…", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    trailingIcon = {
                        AnimatedVisibility(visible = searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, null, tint = Color.Gray)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SwoAccent,
                        unfocusedBorderColor = Color(0xFF2A3A4A),
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        cursorColor = SwoAccent
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.ProjectForm.createRoute(null)) },
                containerColor = SwoAccent,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val isError = state.error != null
                Snackbar(
                    modifier = Modifier.padding(12.dp),
                    shape = RoundedCornerShape(14.dp),
                    containerColor = if (isError) Color(0xFFEF4444) else Color(0xFF10B981),
                    contentColor = Color.White
                ) { Text(data.visuals.message) }
            }
        },
        containerColor = DarkBackground_Slate
    ) { padding ->
        when {
            state.isLoading -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) { items(4) { ProjectCardShimmer() } }
            }
            filteredProjects.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(
                            if (searchQuery.isNotBlank()) Icons.Default.SearchOff else Icons.Default.RocketLaunch,
                            null, tint = Color.Gray, modifier = Modifier.size(72.dp)
                        )
                        Text(
                            if (searchQuery.isNotBlank()) "Sin proyectos que coincidan" else "No hay proyectos. ¡Crea el primero!",
                            color = Color.Gray, fontSize = 16.sp
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProjects, key = { it.id }) { project ->
                        ProjectItem(
                            project = project,
                            users = state.users,
                            onEdit = { navController.navigate(Screen.ProjectForm.createRoute(project.id)) },
                            onDelete = { projectToDelete = project },
                            onEngineerClick = { userId ->
                                navController.navigate(Screen.UserForm.createRoute(userId))
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun ProjectItem(
    project: Project,
    users: List<User>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onEngineerClick: (userId: String) -> Unit
) {
    val statusColor = when (project.status) {
        "Activo"     -> Color(0xFF10B981)
        "Pausado"    -> Color(0xFFF59E0B)
        "Completado" -> Color(0xFF3B82F6)
        else         -> Color.Gray
    }
    val statusIcon = when (project.status) {
        "Activo"     -> Icons.Default.PlayArrow
        "Pausado"    -> Icons.Default.Pause
        "Completado" -> Icons.Default.CheckCircle
        else         -> Icons.Default.Circle
    }

    // Resolve user IDs → User objects
    val assignedUsers = project.assignedEngineers.mapNotNull { id -> users.find { it.id == id } }

    SWOCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.RocketLaunch, null, tint = statusColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(project.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        Text(project.description, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(statusIcon, null, tint = statusColor, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(project.status, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFF1E2A36))

            // Engineers section (clickable chips → navigate to user profile)
            if (assignedUsers.isNotEmpty()) {
                Text("Equipo asignado:", color = Color.Gray, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val visibleUsers = assignedUsers.take(4)
                    visibleUsers.forEach { user ->
                        EngineerChip(user = user, onClick = { onEngineerClick(user.id) })
                    }
                    if (assignedUsers.size > 4) {
                        Box(
                            modifier = Modifier.size(32.dp).clip(CircleShape)
                                .background(Color(0xFF2A3A4A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+${assignedUsers.size - 4}", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFF1E2A36))
            }

            // Footer: metrics + actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MetricBadge(Icons.Default.Engineering, "${assignedUsers.size} Ing.", SwoAccent)
                    MetricBadge(
                        Icons.Default.BugReport,
                        "${project.activeIncidentsCount} Inc.",
                        if (project.activeIncidentsCount > 0) StatusError else Color.Gray
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Edit, null, tint = SwoAccent, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun EngineerChip(user: User, onClick: () -> Unit) {
    val roleColor = when (user.role) {
        UserRole.ADMIN      -> Color(0xFFEF4444)
        UserRole.TECHNICIAN -> Color(0xFFF59E0B)
        UserRole.CLIENT     -> Color(0xFF3B82F6)
    }
    val initial = user.name.firstOrNull()?.uppercase() ?: "?"
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(roleColor.copy(alpha = 0.5f), roleColor)))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(initial, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
    }
}

@Composable
fun MetricBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, color = color)
    }
}
