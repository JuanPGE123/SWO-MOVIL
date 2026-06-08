package com.example.swo.domain.incidents

import com.example.swo.domain.model.Incident
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetIncidentsUseCase @Inject constructor(
    private val repository: IncidentRepository
) {
    operator fun invoke(): Flow<List<Incident>> {
        return repository.getIncidents()
    }
}
