package com.example.swo.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swo.domain.users.UserRepository
import com.example.swo.domain.model.User
import com.example.swo.domain.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class SnackbarEvent(val message: String, val isError: Boolean)

data class UserUiState(
    val isLoading: Boolean = false,
    val snackbarEvent: SnackbarEvent? = null
)

@OptIn(FlowPreview::class)
@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    // ── Search & filter state ─────────────────────────────────────────────────
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _roleFilter = MutableStateFlow<UserRole?>(null)
    val roleFilter: StateFlow<UserRole?> = _roleFilter.asStateFlow()

    // ── Shared Room source (ONE query, TWO subscribers) ───────────────────────
    private val usersSource: Flow<List<User>> = repository.getUsers()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), replay = 1)

    // All users (for counts in filter chips)
    val allUsers: StateFlow<List<User>> = usersSource
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered + debounced search  (null = still loading first emission)
    val displayedUsers: StateFlow<List<User>?> = combine(
        usersSource,
        _searchQuery.debounce(300),
        _roleFilter
    ) { users, query, role ->
        users
            .filter { role == null || it.role == role }
            .filter {
                query.isBlank() ||
                    it.name.contains(query, ignoreCase = true) ||
                    it.email.contains(query, ignoreCase = true)
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // ── UI feedback ───────────────────────────────────────────────────────────
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    // ── Actions ───────────────────────────────────────────────────────────────
    fun updateSearch(query: String) { _searchQuery.value = query }
    fun updateRoleFilter(role: UserRole?) { _roleFilter.value = if (_roleFilter.value == role) null else role }

    fun saveUser(name: String, email: String, password: String, role: String, userId: String? = null) {
        viewModelScope.launch {
            try {
                val mappedRole = when (role) {
                    "Administrador" -> UserRole.ADMIN
                    "Técnico"       -> UserRole.TECHNICIAN
                    else            -> UserRole.CLIENT
                }
                val existing = allUsers.value.find { it.id == userId }
                val user = User(
                    id       = userId ?: UUID.randomUUID().toString(),
                    name     = name.trim(),
                    email    = email.trim().lowercase(),
                    password = password,
                    role     = mappedRole,
                    isActive = existing?.isActive ?: true,
                    avatarUrl = existing?.avatarUrl
                )
                if (userId == null) {
                    repository.saveUser(user)
                    emitSuccess("Usuario '${user.name}' creado exitosamente")
                } else {
                    repository.updateUser(user)
                    emitSuccess("Usuario actualizado exitosamente")
                }
            } catch (e: Exception) {
                emitError("Error al guardar el usuario: ${e.localizedMessage}")
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                repository.deleteUser(userId)
                emitSuccess("Usuario eliminado")
            } catch (e: Exception) {
                emitError("No se pudo eliminar el usuario")
            }
        }
    }

    fun toggleUserStatus(userId: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                repository.toggleUserStatus(userId, isActive)
                val label = if (isActive) "activado" else "desactivado"
                emitSuccess("Usuario $label")
            } catch (e: Exception) {
                emitError("No se pudo cambiar el estado")
            }
        }
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarEvent = null)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private fun emitSuccess(msg: String) {
        _uiState.value = _uiState.value.copy(snackbarEvent = SnackbarEvent(msg, false))
    }

    private fun emitError(msg: String) {
        _uiState.value = _uiState.value.copy(snackbarEvent = SnackbarEvent(msg, true))
    }
}
