package com.example.swo.data.repository

import com.example.swo.domain.model.Incident
import com.example.swo.domain.model.IncidentPriority
import com.example.swo.domain.model.IncidentStatus
import com.example.swo.domain.repository.IncidentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockIncidentRepositoryImpl @Inject constructor() : IncidentRepository {
    override fun getIncidents(): Flow<List<Incident>> = flowOf(
        listOf(
            Incident(
                id = "1",
                title = "Falla de Red VPN",
                description = "No se puede conectar al servidor principal.",
                status = IncidentStatus.OPEN,
                priority = IncidentPriority.HIGH,
                reportedBy = "Juan Perez",
                projectName = "Banca Móvil 2.0",
                createdAt = "10:30 AM"
            )
        )
    )

    override fun getIncidentById(id: String): Flow<Incident?> = flowOf(null)
    override suspend fun resolveIncident(id: String, notes: String) {}
    override suspend fun updateIncidentPriority(id: String, priority: String) {}
}
