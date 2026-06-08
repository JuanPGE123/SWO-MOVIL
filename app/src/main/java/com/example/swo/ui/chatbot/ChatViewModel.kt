package com.example.swo.ui.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swo.domain.chatbot.ChatRepository
import com.example.swo.model.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

val QUICK_SUGGESTIONS = listOf(
    "¿Cómo reportar una incidencia?",
    "Ver estado de mis tickets",
    "¿Qué es SWO?",
    "Gestión de proyectos",
    "Administración de usuarios",
    "Ver reportes y estadísticas"
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getChatHistory().collect { history ->
                _state.value = _state.value.copy(messages = history)
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                repository.sendMessage(text)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "No se pudo enviar el mensaje")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    fun dismissError() {
        _state.value = _state.value.copy(error = null)
    }
}
