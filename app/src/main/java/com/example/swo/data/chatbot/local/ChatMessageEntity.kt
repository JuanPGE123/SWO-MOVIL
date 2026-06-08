package com.example.swo.data.chatbot.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.swo.model.ChatMessage

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val senderId: String,
    val text: String,
    val timestamp: Long,
    val isFromBot: Boolean
)

fun ChatMessageEntity.toDomain() = ChatMessage(id, senderId, text, timestamp, isFromBot)
fun ChatMessage.toEntity() = ChatMessageEntity(id, senderId, text, timestamp, isFromBot)
