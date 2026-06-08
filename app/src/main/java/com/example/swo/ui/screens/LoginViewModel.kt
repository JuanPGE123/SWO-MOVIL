package com.example.swo.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swo.domain.model.User
import com.example.swo.domain.users.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedUser: User? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun login(email: String, password: String, onSuccess: (User) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(error = "Ingresa tu correo y contraseña")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val user = repository.getUserByCredentials(email.trim().lowercase(), password)
            when {
                user == null -> _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Correo o contraseña incorrectos"
                )
                !user.isActive -> _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Tu cuenta está inactiva. Contacta al administrador."
                )
                else -> {
                    _state.value = _state.value.copy(isLoading = false, loggedUser = user)
                    onSuccess(user)
                }
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
