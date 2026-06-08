package com.example.swo.data.comentarios.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ComentarioDao {

    @Query("SELECT * FROM comentarios WHERE idIncidencia = :idIncidencia ORDER BY fecha ASC")
    fun getComentariosByIncidencia(idIncidencia: String): Flow<List<ComentarioEntity>>

    @Query("SELECT * FROM comentarios WHERE idIncidencia = :idIncidencia AND esPublico = 1 ORDER BY fecha ASC")
    fun getComentariosPublicosByIncidencia(idIncidencia: String): Flow<List<ComentarioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComentario(comentario: ComentarioEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComentarios(comentarios: List<ComentarioEntity>)

    @Delete
    suspend fun deleteComentario(comentario: ComentarioEntity)

    @Query("DELETE FROM comentarios WHERE idIncidencia = :idIncidencia")
    suspend fun deleteComentariosByIncidencia(idIncidencia: String)

    @Query("SELECT COUNT(*) FROM comentarios WHERE idIncidencia = :idIncidencia")
    fun getCountByIncidencia(idIncidencia: String): Flow<Int>
}
