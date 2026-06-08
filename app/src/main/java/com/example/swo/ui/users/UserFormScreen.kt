package com.example.swo.ui.users

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swo.core.utils.FormValidator
import com.example.swo.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFormScreen(
    viewModel: UserViewModel,
    userId: String? = null,
    onBack: () -> Unit
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val existingUser = if (userId != null) allUsers.find { it.id == userId } else null
    val isEditing = userId != null

    // ── Field state ───────────────────────────────────────────────────────────
    var name     by remember(existingUser) { mutableStateOf(existingUser?.name  ?: "") }
    var email    by remember(existingUser) { mutableStateOf(existingUser?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var role     by remember(existingUser) { mutableStateOf(existingUser?.role?.label ?: "Cliente") }
    var passwordVisible by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    // ── Validation errors (null = ok) ─────────────────────────────────────────
    var nameError     by remember { mutableStateOf<String?>(null) }
    var emailError    by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val passwordStrength = if (password.isNotBlank()) FormValidator.passwordStrength(password) else null

    // Form is valid when all validated fields pass AND required fields aren't blank
    val isFormValid = nameError == null && name.isNotBlank() &&
        emailError == null && email.isNotBlank() &&
        passwordError == null && (isEditing || password.isNotBlank())

    // ── Snackbar ──────────────────────────────────────────────────────────────
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.snackbarEvent) {
        uiState.snackbarEvent?.let { event ->
            scope.launch {
                snackbarHostState.showSnackbar(event.message, duration = SnackbarDuration.Short)
            }
            viewModel.clearSnackbar()
            if (!event.isError) onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Editar Usuario" else "Nuevo Usuario",
                        color = Color.White, fontWeight = FontWeight.Bold
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
                SwoSnackbar(data = data, isError = uiState.snackbarEvent?.isError == true)
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
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                if (isEditing) "Actualiza la información del usuario"
                else "Completa los datos para crear un nuevo usuario",
                color = Color.Gray, fontSize = 14.sp
            )

            // ── Name ─────────────────────────────────────────────────────────
            OutlinedTextField(
                value = name,
                onValueChange = {
                    if (it.length <= 100) {
                        name = it
                        nameError = FormValidator.validateName(it)
                    }
                },
                label = { Text("Nombre Completo *") },
                leadingIcon = { Icon(Icons.Default.Person, null, tint = if (nameError != null) MaterialTheme.colorScheme.error else SwoAccent) },
                supportingText = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(nameError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        Text("${name.length}/100", color = Color.Gray, fontSize = 11.sp)
                    }
                },
                isError = nameError != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = validationColors(nameError != null)
            )

            // ── Email ─────────────────────────────────────────────────────────
            OutlinedTextField(
                value = email,
                onValueChange = {
                    if (it.length <= 150) {
                        email = it
                        emailError = FormValidator.validateEmail(it)
                    }
                },
                label = { Text("Correo Electrónico *") },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = if (emailError != null) MaterialTheme.colorScheme.error else SwoAccent) },
                supportingText = emailError?.let { msg ->
                    { Text(msg, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
                },
                isError = emailError != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = validationColors(emailError != null)
            )

            // ── Password + Strength ───────────────────────────────────────────
            Column {
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        if (it.length <= 50) {
                            password = it
                            passwordError = FormValidator.validatePassword(it, isEditing)
                        }
                    },
                    label = {
                        Text(
                            if (isEditing) "Nueva Contraseña (opcional)"
                            else "Contraseña *"
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = if (passwordError != null) MaterialTheme.colorScheme.error else SwoAccent) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                null, tint = Color.Gray
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    supportingText = passwordError?.let { msg ->
                        { Text(msg, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
                    },
                    isError = passwordError != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = validationColors(passwordError != null)
                )

                // Password strength indicator
                AnimatedVisibility(
                    visible = password.isNotBlank() && passwordStrength != null,
                    enter = fadeIn(tween(200)) + expandVertically(),
                    exit = fadeOut(tween(200)) + shrinkVertically()
                ) {
                    passwordStrength?.let { strength ->
                        Column(modifier = Modifier.padding(top = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Seguridad:", color = Color.Gray, fontSize = 12.sp)
                                Text(
                                    strength.label,
                                    color = Color(strength.colorHex),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFF1E2A36))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(strength.fraction).fillMaxHeight()
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color(strength.colorHex))
                                )
                            }
                        }
                    }
                }
            }

            // ── Role selector ─────────────────────────────────────────────────
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = role, onValueChange = {}, readOnly = true,
                    label = { Text("Rol Asignado *") },
                    leadingIcon = {
                        Icon(
                            when (role) {
                                "Administrador" -> Icons.Default.AdminPanelSettings
                                "Técnico"       -> Icons.Default.Build
                                else            -> Icons.Default.Person
                            }, null,
                            tint = when (role) {
                                "Administrador" -> Color(0xFFEF4444)
                                "Técnico"       -> Color(0xFFF59E0B)
                                else            -> Color(0xFF3B82F6)
                            }
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(14.dp),
                    colors = validationColors(false)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, containerColor = SurfaceDark) {
                    listOf("Administrador", "Técnico", "Cliente").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = Color.White) },
                            onClick = { role = option; expanded = false },
                            leadingIcon = {
                                Icon(
                                    when (option) {
                                        "Administrador" -> Icons.Default.AdminPanelSettings
                                        "Técnico"       -> Icons.Default.Build
                                        else            -> Icons.Default.Person
                                    }, null,
                                    tint = when (option) {
                                        "Administrador" -> Color(0xFFEF4444)
                                        "Técnico"       -> Color(0xFFF59E0B)
                                        else            -> Color(0xFF3B82F6)
                                    }
                                )
                            }
                        )
                    }
                }
            }

            // ── Role permissions card ─────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, null, tint = SwoLightBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Permisos del rol: $role", color = SwoLightBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    val perms = when (role) {
                        "Administrador" -> listOf(
                            "Acceso total al sistema",
                            "Gestión de usuarios y roles",
                            "Reportes y configuración avanzada"
                        )
                        "Técnico" -> listOf(
                            "Gestión de incidencias asignadas",
                            "Actualizar estados y soluciones",
                            "Ver proyectos y reportes"
                        )
                        else -> listOf(
                            "Crear y ver propias incidencias",
                            "Seguimiento de tickets abiertos",
                            "Acceso al chatbot de soporte"
                        )
                    }
                    perms.forEach { perm ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircleOutline, null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(perm, color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Save button (disabled until form valid) ───────────────────────
            Button(
                onClick = {
                    // Force validate all fields before submit
                    nameError  = FormValidator.validateName(name)
                    emailError = FormValidator.validateEmail(email)
                    passwordError = FormValidator.validatePassword(password, isEditing)

                    if (isFormValid) {
                        val effectivePassword = if (isEditing && password.isBlank())
                            (existingUser?.password ?: "") else password
                        viewModel.saveUser(name, email, effectivePassword, role, userId)
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
                Icon(
                    if (isEditing) Icons.Default.Save else Icons.Default.PersonAdd,
                    null, tint = Color.White, modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    if (isEditing) "Guardar Cambios" else "Crear Usuario",
                    color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp
                )
            }

            if (!isFormValid && (name.isNotBlank() || email.isNotBlank())) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Completa todos los campos correctamente", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun validationColors(isError: Boolean) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = if (isError) MaterialTheme.colorScheme.error else SwoAccent,
    unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.6f) else Color(0xFF3A4A5A),
    focusedLabelColor    = if (isError) MaterialTheme.colorScheme.error else SwoAccent,
    cursorColor          = SwoAccent,
    errorBorderColor     = MaterialTheme.colorScheme.error,
    errorLabelColor      = MaterialTheme.colorScheme.error
)
