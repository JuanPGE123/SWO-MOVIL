package com.example.swo.domain.model

import com.example.swo.data.comentarios.local.ComentarioEntity
import com.example.swo.data.comentarios.local.toDomain
import com.example.swo.data.comentarios.local.toEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class ComentarioTest {

    private val comentarioDomain = Comentario(
        idComentario = "c-001",
        texto = "El error ocurre al iniciar sesión desde móvil.",
        fecha = 1_700_000_000L,
        idIncidencia = "inc-42",
        idUsuario = "usr-7",
        nombreUsuario = "Ana García",
        esPublico = true
    )

    private val comentarioEntity = ComentarioEntity(
        idComentario = "c-001",
        texto = "El error ocurre al iniciar sesión desde móvil.",
        fecha = 1_700_000_000L,
        idIncidencia = "inc-42",
        idUsuario = "usr-7",
        nombreUsuario = "Ana García",
        esPublico = true
    )

    // ── Mapeo domain → entity ─────────────────────────────────────────────────

    @Test
    fun `toEntity mapea todos los campos correctamente`() {
        val entity = comentarioDomain.toEntity()

        assertEquals("c-001",   entity.idComentario)
        assertEquals("El error ocurre al iniciar sesión desde móvil.", entity.texto)
        assertEquals(1_700_000_000L, entity.fecha)
        assertEquals("inc-42", entity.idIncidencia)
        assertEquals("usr-7",  entity.idUsuario)
        assertEquals("Ana García", entity.nombreUsuario)
        assertTrue(entity.esPublico)
    }

    // ── Mapeo entity → domain ─────────────────────────────────────────────────

    @Test
    fun `toDomain mapea todos los campos correctamente`() {
        val domain = comentarioEntity.toDomain()

        assertEquals("c-001",   domain.idComentario)
        assertEquals("El error ocurre al iniciar sesión desde móvil.", domain.texto)
        assertEquals(1_700_000_000L, domain.fecha)
        assertEquals("inc-42", domain.idIncidencia)
        assertEquals("usr-7",  domain.idUsuario)
        assertEquals("Ana García", domain.nombreUsuario)
        assertTrue(domain.esPublico)
    }

    // ── Comentario privado ────────────────────────────────────────────────────

    @Test
    fun `comentario privado se mapea correctamente`() {
        val privado = comentarioDomain.copy(esPublico = false)
        val entity  = privado.toEntity()
        val vuelta  = entity.toDomain()

        assertFalse(entity.esPublico)
        assertFalse(vuelta.esPublico)
    }

    // ── Valor por defecto de esPublico ────────────────────────────────────────

    @Test
    fun `esPublico es true por defecto`() {
        val comentario = Comentario(
            idComentario = "c-002",
            texto = "Comentario sin especificar visibilidad",
            fecha = 0L,
            idIncidencia = "inc-1",
            idUsuario = "usr-1",
            nombreUsuario = "Test"
        )
        assertTrue(comentario.esPublico)
    }
}
