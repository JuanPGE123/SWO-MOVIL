package com.example.swo.ui.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swo.core.utils.FormValidator
import com.example.swo.domain.model.User
import com.example.swo.domain.model.UserRole
import com.example.swo.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectFormScreen(
    viewModel: ProjectViewModel,
    projectId: String? = null,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val existingProject = state.projects.find { it.id == projectId }
    val isEditing = projectId != null

    var name by remember(existingProject) { mutableStateOf(existingProject?.name ?: "") }
    var description by remember(existingProject) { mutableStateOf(existingProject?.description ?: "") }
    var status by remember(existingProject) { mutableStateOf(existingProject?.status ?: "Activo") }
    var selectedEngineers by remember(existingProject) {
        mutableStateOf(existingProject?.assignedEngineers?.toSet() ?: emptySet())
    }

    // Validation
    var nameError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    val isFormValid = nameError == null && name.isNotBlank() && descriptionError == null

    // Eligible users: ADMIN and TECHNICIAN
    val eligibleUsers = state.users.filter { it.role == UserRole.ADMIN || it.role == UserRole.TECHNICIAN }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            scope.launch { snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short) }
            viewModel.clearMessages()
            onBack()
        }
    }
    LaunchedEffect(state.error) {
        state.error?.let {
            scope.launch { snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short) }
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Editar Proyecto" else "Nuevo Proyecto",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(12.dp),
                    shape = RoundedCornerShape(14.dp),
                    containerColor = if (state.error != null) Color(0xFFEF4444) else Color(0xFF10B981),
                    contentColor = Color.White
                ) { Text(data.visuals.message) }
            }
        },
        containerColor = DarkBackground_Slate
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Name
            OutlinedTextField(
                value = name,
                onValueChange = {
                    if (it.length <= 80) { name = it; nameError = FormValidator.validateProjectName(it) }
                },
                label = { Text("Nombre del Proyecto *") },
                leadingIcon = { Icon(Icons.Default.RocketLaunch, null, tint = if (nameError != null) MaterialTheme.colorScheme.error else SwoAccent) },
                supportingText = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(nameError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        Text("${name.length}/80", color = Color.Gray, fontSize = 11.sp)
                    }
                },
                isError = nameError != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (nameError != null) MaterialTheme.colorScheme.error else SwoAccent,
                    unfocusedBorderColor = if (nameError != null) MaterialTheme.colorScheme.error.copy(alpha = 0.6f) else Color(0xFF3A4A5A),
                    focusedLabelColor = if (nameError != null) MaterialTheme.colorScheme.error else SwoAccent,
                    cursorColor = SwoAccent
                )
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = {
                    if (it.length <= 500) { description = it; descriptionError = FormValidator.validateDescription(it) }
                },
                label = { Text("Descripción") },
                leadingIcon = { Icon(Icons.Default.Description, null, tint = SwoAccent) },
                supportingText = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(descriptionError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        Text("${description.length}/500", color = Color.Gray, fontSize = 11.sp)
                    }
                },
                isError = descriptionError != null,
                modifier = Modifier.fillMaxWidth().height(130.dp),
                shape = RoundedCornerShape(14.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SwoAccent,
                    unfocusedBorderColor = Color(0xFF3A4A5A),
                    focusedLabelColor = SwoAccent,
                    cursorColor = SwoAccent
                )
            )

            // Status
            Column {
                Text("Estado del proyecto", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(
                        "Activo" to Color(0xFF10B981),
                        "Pausado" to Color(0xFFF59E0B),
                        "Completado" to Color(0xFF3B82F6)
                    ).forEach { (label, color) ->
                        FilterChip(
                            selected = status == label,
                            onClick = { status = label },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.2f),
                                selectedLabelColor = color,
                                containerColor = SurfaceDark,
                                labelColor = Color.Gray
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = status == label,
                                selectedBorderColor = color.copy(alpha = 0.5f),
                                borderColor = Color(0xFF2A3A4A)
                            )
                        )
                    }
                }
            }

            // Engineer assignment
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ingenieros Asignados", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    if (selectedEngineers.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SwoAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("${selectedEngineers.size} seleccionados", color = SwoAccent, fontSize = 12.sp)
                        }
                    }
                }
                Text(
                    "Selecciona administradores y técnicos para el proyecto",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                if (eligibleUsers.isEmpty()) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("No hay técnicos o administradores disponibles", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                } else {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                    ) {
                        Column {
                            eligibleUsers.forEachIndexed { index, user ->
                                EngineerToggleRow(
                                    user = user,
                                    isSelected = user.id in selectedEngineers,
                                    onToggle = { checked ->
                                        selectedEngineers = if (checked)
                                            selectedEngineers + user.id
                                        else
                                            selectedEngineers - user.id
                                    }
                                )
                                if (index < eligibleUsers.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color = Color(0xFF1E2A36)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    nameError = FormValidator.validateProjectName(name)
                    descriptionError = FormValidator.validateDescription(description)
                    if (isFormValid) {
                        viewModel.saveProject(
                            id = projectId,
                            name = name.trim(),
                            description = description.trim(),
                            status = status,
                            assignedEngineers = selectedEngineers.toList()
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SwoAccent,
                    disabledContainerColor = SwoAccent.copy(alpha = 0.35f)
                ),
                enabled = isFormValid
            ) {
                Icon(if (isEditing) Icons.Default.Save else Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    if (isEditing) "Guardar Cambios" else "Crear Proyecto",
                    color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun EngineerToggleRow(user: User, isSelected: Boolean, onToggle: (Boolean) -> Unit) {
    val roleColor = when (user.role) {
        UserRole.ADMIN -> Color(0xFFEF4444)
        UserRole.TECHNICIAN -> Color(0xFFF59E0B)
        else -> Color.Gray
    }
    val initial = user.name.firstOrNull()?.uppercase() ?: "?"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(roleColor.copy(alpha = 0.5f), roleColor)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(initial, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(user.name, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(user.email, color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(roleColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(user.role.label, color = roleColor, fontSize = 10.sp)
                }
            }
        }

        Checkbox(
            checked = isSelected,
            onCheckedChange = onToggle,
            colors = CheckboxDefaults.colors(
                checkedColor = SwoAccent,
                uncheckedColor = Color.Gray,
                checkmarkColor = Color.Black
            )
        )
    }
}
