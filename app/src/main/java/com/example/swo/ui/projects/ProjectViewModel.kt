package com.example.swo.ui.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swo.domain.projects.ProjectRepository
import com.example.swo.domain.users.UserRepository
import com.example.swo.domain.model.Project
import com.example.swo.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ProjectUiState(
    val projects: List<Project> = emptyList(),
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val repository: ProjectRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProjectUiState())
    val state: StateFlow<ProjectUiState> = _state.asStateFlow()

    init {
        loadProjects()
        loadUsers()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.getProjects().collect { result ->
                _state.value = _state.value.copy(projects = result, isLoading = false)
            }
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            userRepository.getUsers().collect { users ->
                _state.value = _state.value.copy(users = users)
            }
        }
    }

    fun saveProject(
        id: String?,
        name: String,
        description: String,
        status: String = "Activo",
        assignedEngineers: List<String> = emptyList()
    ) {
        if (name.isBlank()) {
            _state.value = _state.value.copy(error = "El nombre es obligatorio")
            return
        }
        val projectId = id ?: UUID.randomUUID().toString()
        val existingProject = _state.value.projects.find { it.id == projectId }

        viewModelScope.launch {
            val project = Project(
                id = projectId,
                name = name,
                description = description,
                status = status,
                activeIncidentsCount = existingProject?.activeIncidentsCount ?: 0,
                assignedEngineers = assignedEngineers.ifEmpty {
                    existingProject?.assignedEngineers ?: emptyList()
                }
            )
            repository.saveProject(project)
            _state.value = _state.value.copy(
                successMessage = if (id == null) "Proyecto creado con éxito" else "Proyecto actualizado con éxito"
            )
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(error = null, successMessage = null)
    }
}
