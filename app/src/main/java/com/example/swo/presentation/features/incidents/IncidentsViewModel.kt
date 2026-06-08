package com.example.swo.presentation.features.incidents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swo.core.base.UiState
import com.example.swo.domain.model.Incident
import com.example.swo.domain.model.IncidentStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IncidentMetrics(
    val total: Int = 0,
    val openPercentage: Int = 0,
    val resolvedPercentage: Int = 0
)

@HiltViewModel
class IncidentsViewModel @Inject constructor(
    private val repository: com.example.swo.domain.incidents.IncidentRepository
) : ViewModel() {

    private val _allIncidents = MutableStateFlow<List<Incident>>(emptyList())
    
    val selectedStatus = MutableStateFlow<IncidentStatus?>(null)
    val selectedAssignee = MutableStateFlow<String?>(null)

    val metrics: StateFlow<IncidentMetrics> = _allIncidents.map { incidents ->
        if (incidents.isEmpty()) return@map IncidentMetrics()
        val total = incidents.size
        val openCount = incidents.count { it.status == IncidentStatus.OPEN || it.status == IncidentStatus.IN_PROGRESS }
        val resolvedCount = incidents.count { it.status == IncidentStatus.RESOLVED }
        
        IncidentMetrics(
            total = total,
            openPercentage = (openCount * 100) / total,
            resolvedPercentage = (resolvedCount * 100) / total
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, IncidentMetrics())

    val uiState: StateFlow<UiState<List<Incident>>> = combine(
        _allIncidents, selectedStatus, selectedAssignee
    ) { incidents, status, assignee ->
        var filteredList = incidents
        if (status != null) {
            filteredList = filteredList.filter { it.status == status }
        }
        if (assignee != null) {
            filteredList = filteredList.filter { it.assignedTo == assignee }
        }
        
        if (filteredList.isEmpty()) UiState.Empty else UiState.Success(filteredList)
    }.catch { e ->
        emit(UiState.Error(e.message ?: "Unknown Error"))
    }.stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading)

    init {
        loadIncidents()
    }

    private fun loadIncidents() {
        viewModelScope.launch {
            repository.getIncidents()
                .catch { e -> _allIncidents.value = emptyList() } // In a real app, emit error to a separate flow
                .collect { list ->
                    _allIncidents.value = list
                }
        }
    }
    
    fun setStatusFilter(status: IncidentStatus?) {
        selectedStatus.value = status
    }
    
    fun setAssigneeFilter(assignee: String?) {
        selectedAssignee.value = assignee
    }
}
