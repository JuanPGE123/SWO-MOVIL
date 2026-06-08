package com.example.swo.data.incidents.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.swo.domain.model.Incident
import com.example.swo.domain.model.IncidentPriority
import com.example.swo.domain.model.IncidentStatus
import com.example.swo.domain.model.TimelineEvent

@Entity(tableName = "incidents")
data class IncidentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val status: String,
    val priority: String,
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
    val isSynced: Boolean = true,
    val lastModified: Long = System.currentTimeMillis(),
    val categoryId: String? = null
)

fun IncidentEntity.toDomain() = Incident(
    id = id,
    title = title,
    description = description,
    status = IncidentStatus.valueOf(status),
    priority = IncidentPriority.valueOf(priority),
    reportedBy = reportedBy,
    projectName = projectName,
    createdAt = createdAt,
    assignedTo = assignedTo,
    solutionNotes = solutionNotes,
    ciudad = ciudad,
    momentoError = momentoError,
    entorno = entorno,
    requiereVisitaPresencial = requiereVisitaPresencial,
    direccion = direccion,
    timeline = timeline,
    categoryId = categoryId
)

fun Incident.toEntity(isSynced: Boolean = true) = IncidentEntity(
    id = id,
    title = title,
    description = description,
    status = status.name,
    priority = priority.name,
    reportedBy = reportedBy,
    projectName = projectName,
    createdAt = createdAt,
    assignedTo = assignedTo,
    solutionNotes = solutionNotes,
    ciudad = ciudad,
    momentoError = momentoError,
    entorno = entorno,
    requiereVisitaPresencial = requiereVisitaPresencial,
    direccion = direccion,
    timeline = timeline,
    isSynced = isSynced,
    categoryId = categoryId
)
