package com.example.swo.domain.chatbot

import com.example.swo.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatHistory(): Flow<List<ChatMessage>>
    suspend fun sendMessage(text: String)
    suspend fun clearChat()
}
