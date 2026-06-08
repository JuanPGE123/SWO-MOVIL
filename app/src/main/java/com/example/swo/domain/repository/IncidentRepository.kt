package com.example.swo.domain.repository

import com.example.swo.domain.model.Incident
import kotlinx.coroutines.flow.Flow

interface IncidentRepository {
    fun getIncidents(): Flow<List<Incident>>
    fun getIncidentById(id: String): Flow<Incident?>
    suspend fun resolveIncident(id: String, notes: String)
    suspend fun updateIncidentPriority(id: String, priority: String)
}
