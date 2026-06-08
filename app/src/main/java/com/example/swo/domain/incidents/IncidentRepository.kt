package com.example.swo.domain.incidents

import com.example.swo.domain.model.Incident
import kotlinx.coroutines.flow.Flow

interface IncidentRepository {
    fun getIncidents(): Flow<List<Incident>>
    suspend fun refreshIncidents()
    suspend fun addIncident(incident: Incident)
    suspend fun updateIncident(incident: Incident)
    suspend fun syncPendingIncidents()
    suspend fun resolveIncident(id: String, notes: String)
}
