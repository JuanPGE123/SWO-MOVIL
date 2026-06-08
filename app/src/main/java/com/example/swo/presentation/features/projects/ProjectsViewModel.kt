package com.example.swo.presentation.features.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swo.core.base.UiState
import com.example.swo.domain.model.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<Project>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Project>>> = _uiState.asStateFlow()

    init {
        // Mock data for initial deploy
        _uiState.value = UiState.Success(listOf(
            Project("1", "Mantenimiento Servidores", "Infraestructura crítica", 5, listOf("Ing. Perez"), "Activo"),
            Project("2", "Desarrollo SWO Mobile", "Nueva App Corporativa", 2, listOf("Ing. Gomez"), "En Progreso")
        ))
    }
}
