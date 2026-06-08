package com.example.swo.ui.incidents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swo.domain.model.IncidentPriority
import com.example.swo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentFormScreen(
    viewModel: IncidentFormViewModel,
    onBack: () -> Unit
) {
    val users by viewModel.users.collectAsState()
    val projects by viewModel.projects.collectAsState()

    val haptic = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Formulario de Incidente", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Detalles del Incidente",
                color = NeonCyan,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // Campo de Título
            OutlinedTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it },
                label = { Text("Título *") },
                leadingIcon = { Icon(Icons.Default.Title, contentDescription = null, tint = NeonCyan) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                isError = viewModel.title.isBlank() && viewModel.error != null,
                enabled = !viewModel.isReadOnly
            )

            // Campo de Descripción
            OutlinedTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = { Text("Descripción *") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = NeonCyan) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(16.dp),
                isError = viewModel.description.isBlank() && viewModel.error != null,
                enabled = !viewModel.isReadOnly
            )

            // Campo de Ciudad
            OutlinedTextField(
                value = viewModel.ciudad,
                onValueChange = { viewModel.ciudad = it },
                label = { Text("Ciudad *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                isError = viewModel.ciudad.isBlank() && viewModel.error != null,
                enabled = !viewModel.isReadOnly
            )

            // Momento del Error
            OutlinedTextField(
                value = viewModel.momentoError,
                onValueChange = { viewModel.momentoError = it },
                label = { Text("Momento del Error (Ej: Durante el login) *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                isError = viewModel.momentoError.isBlank() && viewModel.error != null,
                enabled = !viewModel.isReadOnly
            )
            
            // Requiere visita presencial (Checkbox animado)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current,
                        enabled = !viewModel.isReadOnly
                    ) {
                        viewModel.requiereVisitaPresencial = !viewModel.requiereVisitaPresencial
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    .padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = viewModel.requiereVisitaPresencial,
                    onCheckedChange = null, // Manejado por el Row para tener un area de click más grande
                    colors = CheckboxDefaults.colors(
                        checkedColor = NeonCyan,
                        uncheckedColor = Color.Gray,
                        checkmarkColor = Color.Black
                    ),
                    enabled = !viewModel.isReadOnly
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Requiere visita presencial", color = Color.White)
            }

            // Animación de despliegue para la dirección
            AnimatedVisibility(
                visible = viewModel.requiereVisitaPresencial,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                OutlinedTextField(
                    value = viewModel.direccion,
                    onValueChange = { viewModel.direccion = it },
                    label = { Text("Dirección *") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = NeonCyan) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    isError = viewModel.direccion.isBlank() && viewModel.error != null,
                    enabled = !viewModel.isReadOnly
                )
            }

            // Entorno (Exposed Dropdown filtrable)
            var entornoExpanded by remember { mutableStateOf(false) }
            val entornos = listOf("Producción", "QA", "Desarrollo")
            var entornoSearchQuery by remember { mutableStateOf(viewModel.entorno) }
            
            ExposedDropdownMenuBox(
                expanded = entornoExpanded,
                onExpandedChange = { if (!viewModel.isReadOnly) entornoExpanded = !entornoExpanded }
            ) {
                OutlinedTextField(
                    value = entornoSearchQuery,
                    onValueChange = { 
                        entornoSearchQuery = it
                        viewModel.entorno = it
                        entornoExpanded = true
                    },
                    label = { Text("Entorno") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = entornoExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !viewModel.isReadOnly
                )
                
                val filteredEntornos = entornos.filter { it.contains(entornoSearchQuery, ignoreCase = true) }
                
                if (filteredEntornos.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = entornoExpanded,
                        onDismissRequest = { entornoExpanded = false }
                    ) {
                        filteredEntornos.forEach { env ->
                            DropdownMenuItem(
                                text = { Text(env) },
                                onClick = {
                                    viewModel.entorno = env
                                    entornoSearchQuery = env
                                    entornoExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Prioridad (Exposed Dropdown filtrable)
            var priorityExpanded by remember { mutableStateOf(false) }
            var prioritySearchQuery by remember { mutableStateOf(viewModel.priority.name) }
            
            ExposedDropdownMenuBox(
                expanded = priorityExpanded,
                onExpandedChange = { if (!viewModel.isReadOnly) priorityExpanded = !priorityExpanded }
            ) {
                OutlinedTextField(
                    value = prioritySearchQuery,
                    onValueChange = {
                        prioritySearchQuery = it
                        priorityExpanded = true
                    },
                    label = { Text("Prioridad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !viewModel.isReadOnly
                )
                
                val filteredPriorities = IncidentPriority.values().filter { it.name.contains(prioritySearchQuery, ignoreCase = true) }
                
                if (filteredPriorities.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false }
                    ) {
                        filteredPriorities.forEach { priority ->
                            DropdownMenuItem(
                                text = { Text(priority.name) },
                                onClick = {
                                    viewModel.priority = priority
                                    prioritySearchQuery = priority.name
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Proyecto (Exposed Dropdown filtrable)
            var projectExpanded by remember { mutableStateOf(false) }
            var projectSearchQuery by remember { mutableStateOf(viewModel.projectName) }
            
            ExposedDropdownMenuBox(
                expanded = projectExpanded,
                onExpandedChange = { if (!viewModel.isReadOnly) projectExpanded = !projectExpanded }
            ) {
                OutlinedTextField(
                    value = projectSearchQuery,
                    onValueChange = {
                        projectSearchQuery = it
                        viewModel.projectName = it
                        projectExpanded = true
                    },
                    label = { Text("Proyecto *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = projectExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    isError = viewModel.projectName.isBlank() && viewModel.error != null,
                    enabled = !viewModel.isReadOnly
                )
                
                val filteredProjects = projects.filter { it.name.contains(projectSearchQuery, ignoreCase = true) }
                
                if (filteredProjects.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = projectExpanded,
                        onDismissRequest = { projectExpanded = false }
                    ) {
                        filteredProjects.forEach { project ->
                            DropdownMenuItem(
                                text = { Text(project.name) },
                                onClick = {
                                    viewModel.projectName = project.name
                                    projectSearchQuery = project.name
                                    projectExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Asignar a (Exposed Dropdown filtrable)
            var userExpanded by remember { mutableStateOf(false) }
            var userSearchQuery by remember { mutableStateOf(viewModel.assignedTo ?: "Sin asignar") }
            
            ExposedDropdownMenuBox(
                expanded = userExpanded,
                onExpandedChange = { if (!viewModel.isReadOnly) userExpanded = !userExpanded }
            ) {
                OutlinedTextField(
                    value = userSearchQuery,
                    onValueChange = {
                        userSearchQuery = it
                        userExpanded = true
                    },
                    label = { Text("Asignar a") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = userExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !viewModel.isReadOnly
                )
                
                val filteredUsers = users.filter { it.name.contains(userSearchQuery, ignoreCase = true) }
                val showSinAsignar = "Sin asignar".contains(userSearchQuery, ignoreCase = true)
                
                if (filteredUsers.isNotEmpty() || showSinAsignar) {
                    ExposedDropdownMenu(
                        expanded = userExpanded,
                        onDismissRequest = { userExpanded = false }
                    ) {
                        if (showSinAsignar) {
                            DropdownMenuItem(
                                text = { Text("Sin asignar") },
                                onClick = {
                                    viewModel.assignedTo = null
                                    userSearchQuery = "Sin asignar"
                                    userExpanded = false
                                }
                            )
                        }
                        filteredUsers.forEach { user ->
                            DropdownMenuItem(
                                text = { Text(user.name) },
                                onClick = {
                                    viewModel.assignedTo = user.name
                                    userSearchQuery = user.name
                                    userExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            if (viewModel.error != null) {
                Text(text = viewModel.error!!, color = Color.Red, fontSize = 14.sp)
            }

            // Botón con estado reactivo y Haptic Feedback
            val isButtonEnabled = viewModel.isFormValid && !viewModel.isSaving && !viewModel.isReadOnly
            val buttonElevation by animateDpAsState(targetValue = if (isButtonEnabled) 8.dp else 0.dp)

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.saveIncident(onSuccess = onBack)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonBlue,
                    disabledContainerColor = Color.DarkGray
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = buttonElevation,
                    pressedElevation = 2.dp,
                    disabledElevation = 0.dp
                ),
                enabled = isButtonEnabled
            ) {
                if (viewModel.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Guardar Incidente", 
                        color = if (isButtonEnabled) Color.White else Color.LightGray, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
