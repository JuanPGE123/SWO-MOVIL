package com.example.swo.data.comentarios.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.swo.domain.model.Comentario

@Entity(tableName = "comentarios")
data class ComentarioEntity(
    @PrimaryKey val idComentario: String,
    val texto: String,
    val fecha: Long,
    val idIncidencia: String,
    val idUsuario: String,
    val nombreUsuario: String,
    val esPublico: Boolean = true
)

fun ComentarioEntity.toDomain() = Comentario(
    idComentario = idComentario,
    texto = texto,
    fecha = fecha,
    idIncidencia = idIncidencia,
    idUsuario = idUsuario,
    nombreUsuario = nombreUsuario,
    esPublico = esPublico
)

fun Comentario.toEntity() = ComentarioEntity(
    idComentario = idComentario,
    texto = texto,
    fecha = fecha,
    idIncidencia = idIncidencia,
    idUsuario = idUsuario,
    nombreUsuario = nombreUsuario,
    esPublico = esPublico
)
