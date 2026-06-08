package com.example.swo.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.swo.data.repository.IncidentRepository
import com.example.swo.model.Incident

/**
 * ViewModel para la pantalla de Gestión de Incidentes.
 *
 * Se encarga de obtener los incidentes del repositorio y mantener el estado
 * de la interfaz de usuario.
 */
class IncidentsViewModel : ViewModel() {

    private val repository = IncidentRepository()

    // Expone la lista de incidentes para que la UI la observe.
    val incidents: List<Incident> = repository.getIncidents()

    /**
     * Busca y devuelve un incidente por su ID.
     */
    fun getIncidentById(id: String): Incident? {
        return repository.getIncidentById(id)
    }
}
