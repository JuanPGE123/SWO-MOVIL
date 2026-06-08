package com.example.swo.data.chatbot.repository

import com.example.swo.data.chatbot.local.ChatDao
import com.example.swo.data.chatbot.local.ChatMessageEntity
import com.example.swo.data.chatbot.local.toDomain
import com.example.swo.data.chatbot.local.toEntity
import com.example.swo.data.chatbot.remote.ChatRequest
import com.example.swo.data.chatbot.remote.ChatbotApi
import com.example.swo.domain.chatbot.ChatRepository
import com.example.swo.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val chatbotApi: ChatbotApi
) : ChatRepository {

    override fun getChatHistory(): Flow<List<ChatMessage>> =
        chatDao.getAllMessages().map { it.map { e -> e.toDomain() } }

    override suspend fun sendMessage(text: String) {
        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = "user_1",
            text = text,
            timestamp = System.currentTimeMillis(),
            isFromBot = false
        )
        chatDao.insertMessage(userMessage.toEntity())

        val reply = try {
            val response = chatbotApi.sendQuery(ChatRequest(text))
            if (response.isSuccessful) {
                response.body()?.reply?.takeIf { it.isNotBlank() }
            } else null
        } catch (e: Exception) {
            null
        } ?: generateLocalReply(text)

        val botMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = "bot",
            text = reply,
            timestamp = System.currentTimeMillis(),
            isFromBot = true
        )
        chatDao.insertMessage(botMessage.toEntity())
    }

    override suspend fun clearChat() {
        chatDao.clearAllMessages()
        // Reinsertar mensaje de bienvenida
        chatDao.insertMessage(
            ChatMessageEntity(
                id = "BOT_WELCOME_${System.currentTimeMillis()}",
                senderId = "bot",
                text = "¡Hola! Soy el Asistente Virtual de SWO 👋 ¿En qué puedo ayudarte hoy?",
                timestamp = System.currentTimeMillis(),
                isFromBot = true
            )
        )
    }

    private fun generateLocalReply(input: String): String {
        val q = input.lowercase().trim()
        return when {
            q.contains("incidencia") || q.contains("ticket") || q.contains("reporte") ->
                "Para reportar una incidencia, ve al módulo de Incidentes y toca el botón '+'. Completa el título, descripción, prioridad y proyecto, y guarda el formulario."
            q.contains("estado") || q.contains("seguimiento") || q.contains("mis ticket") ->
                "Puedes ver el estado de tus tickets en la pantalla de Incidentes. Usa los filtros de estado (Abierto, En Progreso, Resuelto) para encontrar los que necesitas."
            q.contains("priorit") || q.contains("crítico") || q.contains("urgente") ->
                "Las prioridades disponibles son: Baja, Media, Alta y Crítica. Los incidentes Críticos se deben resolver en menos de 4 horas. Alta prioridad en menos de 8 horas."
            q.contains("proyecto") ->
                "En el módulo de Proyectos puedes ver todos los proyectos activos, sus ingenieros asignados y la cantidad de incidentes abiertos por proyecto."
            q.contains("usuario") || q.contains("perfil") || q.contains("cuenta") ->
                "En el módulo de Usuarios puedes administrar las cuentas del equipo. Los roles disponibles son: Administrador, Técnico y Cliente."
            q.contains("reporte") || q.contains("estadístic") || q.contains("analític") ->
                "El módulo de Reportes muestra estadísticas en tiempo real: tasa de resolución, distribución por estado, prioridad y proyecto. Accede desde el Dashboard."
            q.contains("password") || q.contains("contraseña") || q.contains("login") ->
                "Si tienes problemas para iniciar sesión, contacta al administrador del sistema para restablecer tu contraseña o revisar tus permisos de acceso."
            q.contains("hola") || q.contains("buenos") || q.contains("saludos") ->
                "¡Hola! Estoy aquí para ayudarte. Puedo orientarte sobre incidencias, proyectos, usuarios y reportes. ¿Qué necesitas?"
            q.contains("gracias") || q.contains("listo") || q.contains("ok") ->
                "¡Con gusto! Si necesitas más ayuda, estoy aquí. 😊"
            q.contains("asignar") || q.contains("técnico") ->
                "Para asignar un técnico a una incidencia, abre el detalle del ticket y selecciona el usuario en el campo 'Asignado a'. Solo los administradores pueden reasignar tickets."
            q.contains("swo") || q.contains("sistema") || q.contains("app") ->
                "SWO es el Sistema de Gestión de Incidencias del SENA. Permite registrar, hacer seguimiento y resolver tickets de soporte técnico de forma eficiente."
            q.contains("chatbot") || q.contains("asistente") || q.contains("bot") ->
                "Soy el asistente virtual de SWO. Puedo ayudarte con dudas sobre el sistema, guiarte en procesos y orientarte sobre cómo usar los diferentes módulos."
            q.contains("ayuda") || q.contains("help") || q.contains("qué puedes") ->
                "Puedo ayudarte con:\n• Cómo reportar una incidencia\n• Estado de tus tickets\n• Gestión de proyectos\n• Administración de usuarios\n• Interpretación de reportes\n¿Sobre qué quieres saber más?"
            else ->
                "Entiendo tu consulta. Para asistencia específica, puedes revisar la documentación del sistema o contactar al equipo de soporte en soporte@swo.com. ¿Hay algo más en lo que pueda orientarte?"
        }
    }
}
