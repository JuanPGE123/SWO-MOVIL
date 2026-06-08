package com.example.swo.data.repository

import com.example.swo.data.incidents.local.IncidentDao
import com.example.swo.data.incidents.local.toDomain
import com.example.swo.data.incidents.local.toEntity
import com.example.swo.data.remote.IncidentService
import com.example.swo.domain.model.Incident
import com.example.swo.domain.repository.IncidentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncidentRepositoryImpl @Inject constructor(
    private val dao: IncidentDao,
    private val service: IncidentService
) : IncidentRepository {

    override fun getIncidents(): Flow<List<Incident>> {
        return dao.getAllIncidents().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getIncidentById(id: String): Flow<Incident?> {
        return dao.getIncidentById(id).map { it?.toDomain() }
    }

    override suspend fun resolveIncident(id: String, notes: String) {
        // Impact local DB first (Offline-First)
        val incident = dao.getIncidentByIdOnce(id)
        if (incident != null) {
            val updated = incident.copy(
                status = com.example.swo.domain.model.IncidentStatus.RESOLVED.name,
                solutionNotes = notes,
                isSynced = false
            )
            dao.insertIncident(updated)
        }
        
        // Attempt network sync
        try {
            val response = service.resolveIncident(id, mapOf("notes" to notes))
            if (response.isSuccessful) {
                dao.markAsSynced(id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Error logged, WorkManager will retry later
        }
    }

    override suspend fun updateIncidentPriority(id: String, priority: String) {
        // Similar logic for priority update
    }
}
