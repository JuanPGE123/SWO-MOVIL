package com.example.swo.domain.incidents

import com.example.swo.domain.auth.UserRole

data class IncidentDetail(
    val id: String,
    val title: String,
    val description: String,
    val status: IncidentStatus,
    val priority: IncidentPriority,
    val reporterName: String,
    val assignedTo: String?,
    val projectName: String,
    val createdAt: String,
    val solutionNotes: String? = null,
    val history: List<IncidentHistory> = emptyList()
)

data class IncidentHistory(
    val date: String,
    val action: String,
    val userName: String
)

data class UpdateIncidentRequest(
    val priority: IncidentPriority? = null,
    val description: String? = null,
    val assignedUserId: String? = null
)

data class ResolveIncidentRequest(
    val solutionNotes: String,
    val resolvedAt: String = System.currentTimeMillis().toString()
)
