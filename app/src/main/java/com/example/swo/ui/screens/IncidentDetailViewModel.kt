package com.example.swo.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swo.domain.incidents.IncidentRepository
import com.example.swo.domain.model.Incident
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncidentDetailViewModel @Inject constructor(
    private val repository: IncidentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val incidentId: String? = savedStateHandle["incidentId"]

    private val _incident = MutableStateFlow<Incident?>(null)
    val incident: StateFlow<Incident?> = _incident.asStateFlow()
    
    private val _isResolving = MutableStateFlow(false)
    val isResolving: StateFlow<Boolean> = _isResolving.asStateFlow()

    init {
        loadIncident()
    }

    private fun loadIncident() {
        if (incidentId == null) return
        
        viewModelScope.launch {
            repository.getIncidents().collect { incidents ->
                _incident.value = incidents.find { it.id == incidentId }
            }
        }
    }

    fun resolveIncident(notes: String, onSuccess: () -> Unit) {
        if (incidentId == null) return
        
        viewModelScope.launch {
            _isResolving.value = true
            try {
                repository.resolveIncident(incidentId, notes)
                onSuccess()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isResolving.value = false
            }
        }
    }
}
