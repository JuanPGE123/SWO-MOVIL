package com.example.swo.data.incidents.repository

import com.example.swo.data.incidents.local.IncidentDao
import com.example.swo.data.incidents.local.toDomain
import com.example.swo.data.incidents.local.toEntity
import com.example.swo.data.incidents.remote.IncidentApi
import com.example.swo.domain.incidents.IncidentRepository
import com.example.swo.domain.model.Incident
import com.example.swo.domain.model.IncidentStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncidentRepositoryImpl @Inject constructor(
    private val incidentDao: IncidentDao,
    private val incidentApi: IncidentApi
) : IncidentRepository {

    override fun getIncidents(): Flow<List<Incident>> {
        return incidentDao.getAllIncidents().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refreshIncidents() {
        try {
            val remoteIncidents = incidentApi.getIncidents()
            incidentDao.insertIncidents(remoteIncidents.map { it.toEntity(isSynced = true) })
        } catch (e: Exception) {
            // Log error, but UI keeps showing local data
        }
    }

    override suspend fun addIncident(incident: Incident) {
        // Optimistic update: Save to local DB marked as unsynced
        incidentDao.insertIncident(incident.toEntity(isSynced = false))
        
        // Attempt to sync immediately
        try {
            val response = incidentApi.postIncident(incident)
            if (response.isSuccessful) {
                incidentDao.markAsSynced(incident.id)
            }
        } catch (e: Exception) {
            // Offline or server error: WorkManager will handle it later
        }
    }

    override suspend fun updateIncident(incident: Incident) {
        incidentDao.insertIncident(incident.toEntity(isSynced = false))
        try {
            // Assuming there's a putIncident or similar for updates
            val response = incidentApi.postIncident(incident) 
            if (response.isSuccessful) {
                incidentDao.markAsSynced(incident.id)
            }
        } catch (e: Exception) {
            // Offline handle
        }
    }

    override suspend fun syncPendingIncidents() {
        val pending = incidentDao.getUnsyncedIncidents()
        pending.forEach { entity ->
            try {
                val response = incidentApi.postIncident(entity.toDomain())
                if (response.isSuccessful) {
                    incidentDao.markAsSynced(entity.id)
                }
            } catch (e: Exception) {
                // Ignore and let next WorkManager run try again
            }
        }
    }

    override suspend fun resolveIncident(id: String, notes: String) {
        // Impact local DB first (Offline-First)
        val incident = incidentDao.getIncidentByIdOnce(id)
        if (incident != null) {
            val updated = incident.copy(
                status = IncidentStatus.RESOLVED.name,
                solutionNotes = notes,
                isSynced = false
            )
            incidentDao.insertIncident(updated)
        }
        
        // Attempt network sync
        try {
            val response = incidentApi.resolveIncident(id, mapOf("notes" to notes))
            if (response.isSuccessful) {
                incidentDao.markAsSynced(id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Error logged, WorkManager will retry later
        }
    }
}
