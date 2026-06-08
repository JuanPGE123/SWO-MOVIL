package com.example.swo.data.notificaciones.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificacionDao {

    @Query("SELECT * FROM notificaciones WHERE idUsuario = :idUsuario ORDER BY fecha DESC")
    fun getNotificacionesByUsuario(idUsuario: String): Flow<List<NotificacionEntity>>

    @Query("SELECT * FROM notificaciones WHERE idUsuario = :idUsuario AND leida = 0 ORDER BY fecha DESC")
    fun getNotificacionesNoLeidas(idUsuario: String): Flow<List<NotificacionEntity>>

    @Query("SELECT COUNT(*) FROM notificaciones WHERE idUsuario = :idUsuario AND leida = 0")
    fun getConteoNoLeidas(idUsuario: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotificacion(notificacion: NotificacionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotificaciones(notificaciones: List<NotificacionEntity>)

    @Query("UPDATE notificaciones SET leida = 1 WHERE idNotificacion = :idNotificacion")
    suspend fun marcarLeida(idNotificacion: String)

    @Query("UPDATE notificaciones SET leida = 1 WHERE idUsuario = :idUsuario")
    suspend fun marcarTodasLeidas(idUsuario: String)

    @Delete
    suspend fun deleteNotificacion(notificacion: NotificacionEntity)

    @Query("DELETE FROM notificaciones WHERE idUsuario = :idUsuario AND leida = 1")
    suspend fun eliminarLeidas(idUsuario: String)
}
