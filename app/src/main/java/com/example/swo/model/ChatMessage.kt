package com.example.swo.model

data class ChatMessage(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: Long,
    val isFromBot: Boolean
)
