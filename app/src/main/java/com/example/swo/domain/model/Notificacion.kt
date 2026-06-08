package com.example.swo.domain.model

enum class TipoNotificacion(val label: String) {
    INCIDENCIA_ASIGNADA("Incidencia asignada"),
    ESTADO_ACTUALIZADO("Estado actualizado"),
    COMENTARIO_NUEVO("Nuevo comentario"),
    GENERAL("General")
}

data class Notificacion(
    val idNotificacion: String,
    val mensaje: String,
    val fecha: Long,
    val leida: Boolean = false,
    val idUsuario: String,
    val idIncidencia: String? = null,
    val tipo: TipoNotificacion = TipoNotificacion.GENERAL
)
