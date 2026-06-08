package com.example.swo.domain.model

data class Comentario(
    val idComentario: String,
    val texto: String,
    val fecha: Long,
    val idIncidencia: String,
    val idUsuario: String,
    val nombreUsuario: String,
    val esPublico: Boolean = true
)
