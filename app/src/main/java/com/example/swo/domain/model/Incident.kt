package com.example.swo.domain.model

import androidx.compose.ui.graphics.Color

enum class IncidentStatus(val label: String, val color: Color) {
    OPEN("Abierto", Color(0xFFEF4444)),
    IN_PROGRESS("En Progreso", Color(0xFF3B82F6)),
    RESOLVED("Resuelto", Color(0xFF10B981)),
    CANCELLED("Cancelado", Color(0xFF64748B))
}

enum class IncidentPriority(val label: String, val color: Color) {
    LOW("Baja", Color(0xFF10B981)),
    MEDIUM("Media", Color(0xFFF59E0B)),
    HIGH("Alta", Color(0xFFF97316)),
    CRITICAL("Crítica", Color(0xFFEF4444))
}

data class Incident(
    val id: String,
    val title: String,
    val description: String,
    val status: IncidentStatus,
    val priority: IncidentPriority,
    val reportedBy: String,
    val projectName: String,
    val createdAt: String,
    val assignedTo: String? = null,
    val solutionNotes: String? = null,
    val ciudad: String? = null,
    val momentoError: String? = null,
    val entorno: String? = null,
    val requiereVisitaPresencial: Boolean = false,
    val direccion: String? = null,
    val timeline: List<TimelineEvent> = emptyList(),
    val categoryId: String? = null
)

data class TimelineEvent(
    val date: String,
    val description: String,
    val user: String
)
