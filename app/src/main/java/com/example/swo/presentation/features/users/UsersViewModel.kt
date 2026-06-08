package com.example.swo.presentation.features.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swo.core.base.UiState
import com.example.swo.domain.model.User
import com.example.swo.domain.users.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<User>>> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            repository.getUsers()
                .catch { e -> _uiState.value = UiState.Error(e.message ?: "Error al cargar usuarios") }
                .collect { list ->
                    _uiState.value = if (list.isEmpty()) UiState.Empty else UiState.Success(list)
                }
        }
    }

    fun toggleUserStatus(user: User) {
        viewModelScope.launch {
            try {
                repository.toggleUserStatus(user.id, !user.isActive)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
