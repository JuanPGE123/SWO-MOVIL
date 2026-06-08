package com.example.swo.ui.users

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.swo.domain.model.User
import com.example.swo.domain.model.UserRole
import com.example.swo.ui.components.UserCardShimmer
import com.example.swo.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    viewModel: UserViewModel,
    onBack: () -> Unit,
    onNavigateToUserForm: (userId: String?) -> Unit
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val displayedUsers by viewModel.displayedUsers.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val roleFilter by viewModel.roleFilter.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var userToDelete by remember { mutableStateOf<User?>(null) }

    // Snackbar driven by ViewModel events
    LaunchedEffect(uiState.snackbarEvent) {
        uiState.snackbarEvent?.let { event ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = event.message,
                    duration = SnackbarDuration.Short
                )
            }
            viewModel.clearSnackbar()
        }
    }

    // Delete confirmation
    userToDelete?.let { user ->
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            icon = { Icon(Icons.Default.DeleteForever, null, tint = Color(0xFFEF4444)) },
            title = { Text("Eliminar usuario", color = Color.White) },
            text = {
                Text(
                    "¿Eliminar a ${user.name}? Esta acción no se puede deshacer.",
                    color = Color.Gray
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteUser(user.id); userToDelete = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) { Text("Eliminar") }
            },
            dismissButton = {
                OutlinedButton(onClick = { userToDelete = null }) { Text("Cancelar") }
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
                            Text("Usuarios del Sistema", color = Color.White, fontWeight = FontWeight.Bold)
                            Text(
                                "${allUsers.size} usuario${if (allUsers.size != 1) "s" else ""} registrados",
                                color = Color.Gray, fontSize = 12.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearch(it) },
                    placeholder = { Text("Buscar por nombre o correo…", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    trailingIcon = {
                        AnimatedVisibility(visible = searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.updateSearch("") }) {
                                Icon(Icons.Default.Close, null, tint = Color.Gray)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SwoAccent,
                        unfocusedBorderColor = Color(0xFF2A3A4A),
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        cursorColor = SwoAccent
                    )
                )
                // Role filter chips
                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        RoleChip("Todos (${allUsers.size})", roleFilter == null, SwoAccent) {
                            viewModel.updateRoleFilter(null)
                        }
                    }
                    item {
                        val c = allUsers.count { it.role == UserRole.ADMIN }
                        RoleChip("Admin ($c)", roleFilter == UserRole.ADMIN, Color(0xFFEF4444)) {
                            viewModel.updateRoleFilter(UserRole.ADMIN)
                        }
                    }
                    item {
                        val c = allUsers.count { it.role == UserRole.TECHNICIAN }
                        RoleChip("Técnicos ($c)", roleFilter == UserRole.TECHNICIAN, Color(0xFFF59E0B)) {
                            viewModel.updateRoleFilter(UserRole.TECHNICIAN)
                        }
                    }
                    item {
                        val c = allUsers.count { it.role == UserRole.CLIENT }
                        RoleChip("Clientes ($c)", roleFilter == UserRole.CLIENT, Color(0xFF3B82F6)) {
                            viewModel.updateRoleFilter(UserRole.CLIENT)
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToUserForm(null) },
                containerColor = SwoAccent,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.PersonAdd, null, tint = Color.White)
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val isError = uiState.snackbarEvent?.isError == true
                SwoSnackbar(data = data, isError = isError)
            }
        },
        containerColor = DarkBackground_Slate
    ) { padding ->
        when {
            // Loading: first emission not yet arrived
            displayedUsers == null -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(6) { UserCardShimmer() }
                }
            }

            // Empty state
            displayedUsers!!.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            if (searchQuery.isNotBlank() || roleFilter != null)
                                Icons.Default.SearchOff else Icons.Default.Group,
                            null, tint = Color.Gray,
                            modifier = Modifier.size(72.dp)
                        )
                        Text(
                            if (searchQuery.isNotBlank() || roleFilter != null)
                                "Sin resultados para la búsqueda"
                            else "No hay usuarios registrados",
                            color = Color.Gray, fontSize = 16.sp
                        )
                        if (searchQuery.isNotBlank() || roleFilter != null) {
                            TextButton(onClick = {
                                viewModel.updateSearch("")
                                viewModel.updateRoleFilter(null)
                            }) {
                                Text("Limpiar filtros", color = SwoAccent)
                            }
                        }
                    }
                }
            }

            // List
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (roleFilter == null && searchQuery.isBlank()) {
                        UserRole.values().forEach { role ->
                            val group = displayedUsers!!.filter { it.role == role }
                            if (group.isNotEmpty()) {
                                item(key = "header_${role.name}") {
                                    RoleGroupHeader(role = role, count = group.size)
                                }
                                items(group, key = { it.id }) { user ->
                                    UserCard(
                                        user = user,
                                        onEdit = { onNavigateToUserForm(user.id) },
                                        onDelete = { userToDelete = user },
                                        onToggleActive = { viewModel.toggleUserStatus(user.id, !user.isActive) }
                                    )
                                }
                            }
                        }
                    } else {
                        items(displayedUsers!!, key = { it.id }) { user ->
                            UserCard(
                                user = user,
                                onEdit = { onNavigateToUserForm(user.id) },
                                onDelete = { userToDelete = user },
                                onToggleActive = { viewModel.toggleUserStatus(user.id, !user.isActive) }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// ── Custom Snackbar ───────────────────────────────────────────────────────────

@Composable
fun SwoSnackbar(data: SnackbarData, isError: Boolean) {
    val bgColor = if (isError) Color(0xFFEF4444) else Color(0xFF10B981)
    val icon = if (isError) Icons.Default.ErrorOutline else Icons.Default.CheckCircle
    Snackbar(
        modifier = Modifier.padding(12.dp),
        shape = RoundedCornerShape(14.dp),
        containerColor = bgColor,
        contentColor = Color.White,
        action = data.visuals.actionLabel?.let {
            { TextButton(onClick = { data.performAction() }) { Text(it, color = Color.White) } }
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(data.visuals.message, color = Color.White, fontSize = 14.sp)
        }
    }
}

// ── Sub-composables ───────────────────────────────────────────────────────────

@Composable
private fun RoleGroupHeader(role: UserRole, count: Int) {
    val (color, icon) = when (role) {
        UserRole.ADMIN      -> Color(0xFFEF4444) to Icons.Default.AdminPanelSettings
        UserRole.TECHNICIAN -> Color(0xFFF59E0B) to Icons.Default.Build
        UserRole.CLIENT     -> Color(0xFF3B82F6) to Icons.Default.Person
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("${role.label}s", color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier.clip(CircleShape).background(color.copy(alpha = 0.15f))
                .padding(horizontal = 7.dp, vertical = 2.dp)
        ) { Text("$count", color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider(modifier = Modifier.width(80.dp), color = color.copy(alpha = 0.25f))
    }
}

@Composable
private fun UserCard(
    user: User,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleActive: () -> Unit
) {
    val roleColor = when (user.role) {
        UserRole.ADMIN      -> Color(0xFFEF4444)
        UserRole.TECHNICIAN -> Color(0xFFF59E0B)
        UserRole.CLIENT     -> Color(0xFF3B82F6)
    }
    val initial = user.name.firstOrNull()?.uppercase() ?: "?"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape).background(
                    Brush.linearGradient(
                        if (user.isActive) listOf(roleColor.copy(alpha = 0.5f), roleColor)
                        else listOf(Color.Gray.copy(alpha = 0.3f), Color.DarkGray)
                    )
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(initial, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        user.name,
                        color = if (user.isActive) Color.White else Color.Gray,
                        fontWeight = FontWeight.SemiBold, fontSize = 15.sp
                    )
                    if (!user.isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                .background(Color.Gray.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) { Text("Inactivo", color = Color.Gray, fontSize = 10.sp) }
                    }
                }
                Text(user.email, color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                        .background(roleColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) { Text(user.role.label, color = roleColor, fontSize = 11.sp, fontWeight = FontWeight.Medium) }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.Edit, null, tint = SwoAccent, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onToggleActive, modifier = Modifier.size(34.dp)) {
                    Icon(
                        if (user.isActive) Icons.Default.Block else Icons.Default.CheckCircle,
                        null,
                        tint = if (user.isActive) Color(0xFFF59E0B) else Color(0xFF10B981),
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.Delete, null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun RoleChip(label: String, selected: Boolean, color: Color, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 13.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = color.copy(alpha = 0.18f),
            selectedLabelColor = color,
            containerColor = SurfaceDark,
            labelColor = Color.Gray
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            selectedBorderColor = color.copy(alpha = 0.5f),
            borderColor = Color(0xFF2A3A4A)
        )
    )
}
