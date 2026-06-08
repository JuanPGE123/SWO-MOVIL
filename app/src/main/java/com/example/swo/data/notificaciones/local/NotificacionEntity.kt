package com.example.swo.data.notificaciones.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.swo.domain.model.Notificacion
import com.example.swo.domain.model.TipoNotificacion

@Entity(tableName = "notificaciones")
data class NotificacionEntity(
    @PrimaryKey val idNotificacion: String,
    val mensaje: String,
    val fecha: Long,
    val leida: Boolean = false,
    val idUsuario: String,
    val idIncidencia: String? = null,
    val tipo: String = TipoNotificacion.GENERAL.name
)

fun NotificacionEntity.toDomain() = Notificacion(
    idNotificacion = idNotificacion,
    mensaje = mensaje,
    fecha = fecha,
    leida = leida,
    idUsuario = idUsuario,
    idIncidencia = idIncidencia,
    tipo = try { TipoNotificacion.valueOf(tipo) } catch (e: Exception) { TipoNotificacion.GENERAL }
)

fun Notificacion.toEntity() = NotificacionEntity(
    idNotificacion = idNotificacion,
    mensaje = mensaje,
    fecha = fecha,
    leida = leida,
    idUsuario = idUsuario,
    idIncidencia = idIncidencia,
    tipo = tipo.name
)
