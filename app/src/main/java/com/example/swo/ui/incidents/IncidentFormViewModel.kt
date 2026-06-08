package com.example.swo.ui.incidents

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swo.domain.incidents.IncidentRepository
import com.example.swo.domain.model.Incident
import com.example.swo.domain.model.IncidentPriority
import com.example.swo.domain.model.IncidentStatus
import com.example.swo.domain.projects.ProjectRepository
import com.example.swo.domain.users.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class IncidentFormViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository,
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val incidentId: String? = savedStateHandle["incidentId"]

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var status by mutableStateOf(IncidentStatus.OPEN)
    var priority by mutableStateOf(IncidentPriority.MEDIUM)
    var projectName by mutableStateOf("")
    var ciudad by mutableStateOf("")
    var momentoError by mutableStateOf("")
    var entorno by mutableStateOf("Producción")
    var assignedTo by mutableStateOf<String?>(null)
    var reportedBy by mutableStateOf("")
    
    // Linked options fields
    var requiereVisitaPresencial by mutableStateOf(false)
    var direccion by mutableStateOf("")
    
    var isSaving by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var isReadOnly by mutableStateOf(false)

    val users = userRepository.getUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projects = projectRepository.getProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reactive validation logic
    val isFormValid by derivedStateOf {
        title.isNotBlank() && 
        description.isNotBlank() && 
        projectName.isNotBlank() && 
        ciudad.isNotBlank() && 
        momentoError.isNotBlank() &&
        (!requiereVisitaPresencial || direccion.isNotBlank())
    }

    init {
        if (incidentId != null) {
            loadIncident(incidentId)
        }
    }

    private fun loadIncident(id: String) {
        viewModelScope.launch {
            incidentRepository.getIncidents().collect { incidents ->
                incidents.find { it.id == id }?.let { incident ->
                    title = incident.title
                    description = incident.description
                    status = incident.status
                    priority = incident.priority
                    projectName = incident.projectName ?: ""
                    ciudad = incident.ciudad ?: ""
                    momentoError = incident.momentoError ?: ""
                    entorno = incident.entorno ?: "Producción"
                    assignedTo = incident.assignedTo
                    reportedBy = incident.reportedBy ?: ""
                    requiereVisitaPresencial = incident.requiereVisitaPresencial
                    direccion = incident.direccion ?: ""
                    
                    if (status == IncidentStatus.RESOLVED || status == IncidentStatus.CANCELLED) {
                        isReadOnly = true
                    }
                }
            }
        }
    }

    fun saveIncident(onSuccess: () -> Unit) {
        if (isReadOnly) {
            error = "No se puede editar un incidente resuelto o cancelado."
            return
        }
        
        if (!isFormValid) {
            error = "Por favor completa todos los campos obligatorios"
            return
        }

        viewModelScope.launch {
            isSaving = true
            try {
                val incident = Incident(
                    id = incidentId ?: UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    status = status,
                    priority = priority,
                    reportedBy = reportedBy.ifBlank { "Admin" },
                    projectName = projectName,
                    createdAt = "Hoy", // In a real app, use actual date
                    assignedTo = assignedTo,
                    ciudad = ciudad,
                    momentoError = momentoError,
                    entorno = entorno,
                    requiereVisitaPresencial = requiereVisitaPresencial,
                    direccion = direccion.takeIf { requiereVisitaPresencial }
                )

                if (incidentId == null) {
                    incidentRepository.addIncident(incident)
                } else {
                    incidentRepository.updateIncident(incident)
                }
                onSuccess()
            } catch (e: Exception) {
                error = e.message
            } finally {
                isSaving = false
            }
        }
    }
}
