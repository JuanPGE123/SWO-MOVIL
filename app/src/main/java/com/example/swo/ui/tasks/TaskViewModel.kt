package com.example.swo.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swo.domain.model.Task
import com.example.swo.domain.repository.TaskRepository
import com.example.swo.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class TaskListState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val error: String = ""
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TaskListState())
    val state: StateFlow<TaskListState> = _state.asStateFlow()

    init {
        getTasks(fetchFromRemote = true)
    }

    private fun getTasks(fetchFromRemote: Boolean = false) {
        repository.getTasks(fetchFromRemote).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = TaskListState(tasks = result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _state.value = TaskListState(
                        error = result.message ?: "An unexpected error occurred",
                        tasks = result.data ?: emptyList()
                    )
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun addTask(title: String, description: String) {
        viewModelScope.launch {
            val newTask = Task(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                isCompleted = false,
                createdAt = System.currentTimeMillis()
            )
            repository.saveTask(newTask)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.saveTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
}
