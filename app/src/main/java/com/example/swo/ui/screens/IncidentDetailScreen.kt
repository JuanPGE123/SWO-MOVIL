package com.example.swo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.swo.core.designsystem.components.SWOCard
import com.example.swo.domain.model.IncidentPriority
import com.example.swo.domain.model.IncidentStatus
import com.example.swo.ui.theme.NeonBlue
import com.example.swo.ui.theme.NeonCyan
import com.example.swo.ui.theme.StatusSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentDetailScreen(
    navController: NavController,
    incidentId: String?,
    viewModel: IncidentDetailViewModel = hiltViewModel()
) {
    val incident by viewModel.incident.collectAsState()
    val isResolving by viewModel.isResolving.collectAsState()
    
    var showResolveDialog by remember { mutableStateOf(false) }
    var solutionNotes by remember { mutableStateOf("") }

    if (incident == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NeonCyan)
        }
        return
    }

    val currentIncident = incident!!
    val isReadOnly = currentIncident.status == IncidentStatus.RESOLVED || currentIncident.status == IncidentStatus.CANCELLED

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Incidente", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Info
            SWOCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "INC-${currentIncident.id.take(4)}",
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Badge(containerColor = currentIncident.status.color.copy(alpha = 0.2f), contentColor = currentIncident.status.color) {
                            Text(currentIncident.status.label.uppercase())
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = currentIncident.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Datos del Reporte
            SectionTitle("Información del Reporte")
            SWOCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoRow("Reportado por:", currentIncident.reportedBy)
                    InfoRow("Fecha:", currentIncident.createdAt)
                    InfoRow("Prioridad:", currentIncident.priority.label, color = currentIncident.priority.color)
                    InfoRow("Proyecto:", currentIncident.projectName)
                    InfoRow("Entorno:", currentIncident.entorno ?: "N/A")
                    InfoRow("Ciudad:", currentIncident.ciudad ?: "N/A")
                    InfoRow("Asignado a:", currentIncident.assignedTo ?: "Sin asignar")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Descripción
            SectionTitle("Descripción del Fallo")
            SWOCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = currentIncident.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Momento del error: ${currentIncident.momentoError ?: "N/A"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            if (currentIncident.solutionNotes != null && currentIncident.status == IncidentStatus.RESOLVED) {
                Spacer(modifier = Modifier.height(20.dp))
                SectionTitle("Notas de Solución")
                SWOCard(modifier = Modifier.fillMaxWidth(), borderColor = StatusSuccess) {
                    Text(
                        text = currentIncident.solutionNotes!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = StatusSuccess
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Acciones Atómicas
            if (!isReadOnly) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate("incident_form?incidentId=$incidentId") },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(NeonBlue))
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("EDITAR")
                    }

                    Button(
                        onClick = { showResolveDialog = true },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("RESOLVER")
                    }
                }
            }
        }
    }

    if (showResolveDialog) {
        AlertDialog(
            onDismissRequest = { if (!isResolving) showResolveDialog = false },
            title = { Text("Resolver Incidente") },
            text = {
                Column {
                    Text("Ingresa las notas técnicas de la solución:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = solutionNotes,
                        onValueChange = { solutionNotes = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        placeholder = { Text("Ej: Se reinició el servicio de autenticación...") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        viewModel.resolveIncident(solutionNotes) {
                            showResolveDialog = false
                        }
                    },
                    enabled = solutionNotes.isNotBlank() && !isResolving
                ) {
                    if (isResolving) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    } else {
                        Text("Confirmar Cierre")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showResolveDialog = false }, enabled = !isResolving) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = NeonCyan,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun InfoRow(label: String, value: String, color: Color = Color.White) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
    }
}
