package com.example.swo.data.chatbot.remote

import com.example.swo.model.ChatMessage
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatbotApi {
    @POST("chatbot/query")
    suspend fun sendQuery(@Body query: ChatRequest): Response<ChatResponse>
}

data class ChatRequest(val message: String)
data class ChatResponse(val reply: String)
