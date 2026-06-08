package com.example.swo.ui.incidents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swo.domain.incidents.GetIncidentsUseCase
import com.example.swo.domain.model.Incident
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IncidentsState(
    val incidents: List<Incident> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class IncidentsViewModel @Inject constructor(
    private val getIncidentsUseCase: GetIncidentsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(IncidentsState())
    val state: StateFlow<IncidentsState> = _state.asStateFlow()

    init {
        loadIncidents()
    }

    private fun loadIncidents() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            getIncidentsUseCase().collect { result ->
                _state.value = _state.value.copy(
                    incidents = result,
                    isLoading = false
                )
            }
        }
    }
}
