package com.example.swo.domain.incidents

import androidx.compose.ui.graphics.Color
import com.example.swo.ui.theme.StatusCancelled
import com.example.swo.ui.theme.StatusError
import com.example.swo.ui.theme.StatusInfo
import com.example.swo.ui.theme.StatusSuccess

data class Incident(
    val id: String,
    val title: String,
    val description: String,
    val status: IncidentStatus,
    val priority: IncidentPriority,
    val createdAt: String,
    val assignedTo: String? = null,
    val reportedBy: String? = null,
    val projectName: String? = null,
    val ciudad: String? = null,
    val momentoError: String? = null,
    val entorno: String? = null,
    val requiereVisitaPresencial: Boolean = false,
    val direccion: String? = null
)

enum class IncidentStatus(val label: String, val color: Color) {
    OPEN("Abierto", StatusError),
    IN_PROGRESS("En Progreso", StatusInfo),
    RESOLVED("Resuelto", StatusSuccess),
    CANCELLED("Cancelado", StatusCancelled)
}

enum class IncidentPriority {
    LOW, MEDIUM, HIGH, CRITICAL
}
