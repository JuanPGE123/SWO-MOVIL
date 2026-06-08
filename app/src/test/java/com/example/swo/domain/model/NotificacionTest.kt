package com.example.swo.domain.model

import com.example.swo.data.notificaciones.local.NotificacionEntity
import com.example.swo.data.notificaciones.local.toDomain
import com.example.swo.data.notificaciones.local.toEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificacionTest {

    private val notificacionDomain = Notificacion(
        idNotificacion = "n-001",
        mensaje = "La incidencia INC-42 te fue asignada.",
        fecha = 1_700_000_000L,
        leida = false,
        idUsuario = "usr-7",
        idIncidencia = "inc-42",
        tipo = TipoNotificacion.INCIDENCIA_ASIGNADA
    )

    private val notificacionEntity = NotificacionEntity(
        idNotificacion = "n-001",
        mensaje = "La incidencia INC-42 te fue asignada.",
        fecha = 1_700_000_000L,
        leida = false,
        idUsuario = "usr-7",
        idIncidencia = "inc-42",
        tipo = TipoNotificacion.INCIDENCIA_ASIGNADA.name
    )

    // ── Mapeo domain → entity ─────────────────────────────────────────────────

    @Test
    fun `toEntity mapea todos los campos correctamente`() {
        val entity = notificacionDomain.toEntity()

        assertEquals("n-001",  entity.idNotificacion)
        assertEquals("La incidencia INC-42 te fue asignada.", entity.mensaje)
        assertEquals(1_700_000_000L, entity.fecha)
        assertFalse(entity.leida)
        assertEquals("usr-7",  entity.idUsuario)
        assertEquals("inc-42", entity.idIncidencia)
        assertEquals("INCIDENCIA_ASIGNADA", entity.tipo)
    }

    // ── Mapeo entity → domain ─────────────────────────────────────────────────

    @Test
    fun `toDomain mapea todos los campos correctamente`() {
        val domain = notificacionEntity.toDomain()

        assertEquals("n-001",  domain.idNotificacion)
        assertEquals("La incidencia INC-42 te fue asignada.", domain.mensaje)
        assertEquals(1_700_000_000L, domain.fecha)
        assertFalse(domain.leida)
        assertEquals("usr-7",  domain.idUsuario)
        assertEquals("inc-42", domain.idIncidencia)
        assertEquals(TipoNotificacion.INCIDENCIA_ASIGNADA, domain.tipo)
    }

    // ── Tipo inválido en entity cae en GENERAL ────────────────────────────────

    @Test
    fun `toDomain con tipo inválido en entity usa GENERAL como fallback`() {
        val entityConTipoRoto = notificacionEntity.copy(tipo = "TIPO_INEXISTENTE")
        val domain = entityConTipoRoto.toDomain()

        assertEquals(TipoNotificacion.GENERAL, domain.tipo)
    }

    // ── Notificación sin incidencia asociada ──────────────────────────────────

    @Test
    fun `notificación general sin idIncidencia se mapea correctamente`() {
        val general = notificacionDomain.copy(
            idIncidencia = null,
            tipo = TipoNotificacion.GENERAL
        )
        val entity = general.toEntity()
        val vuelta = entity.toDomain()

        assertNull(entity.idIncidencia)
        assertNull(vuelta.idIncidencia)
        assertEquals(TipoNotificacion.GENERAL, vuelta.tipo)
    }

    // ── Estado leída ──────────────────────────────────────────────────────────

    @Test
    fun `notificación leída se mapea correctamente`() {
        val leida = notificacionDomain.copy(leida = true)
        val entity = leida.toEntity()
        val vuelta = entity.toDomain()

        assertTrue(entity.leida)
        assertTrue(vuelta.leida)
    }

    // ── Valor por defecto de leida ────────────────────────────────────────────

    @Test
    fun `leida es false por defecto`() {
        val notif = Notificacion(
            idNotificacion = "n-002",
            mensaje = "Mensaje de prueba",
            fecha = 0L,
            idUsuario = "usr-1"
        )
        assertFalse(notif.leida)
    }

    // ── Todos los tipos de notificación tienen label ──────────────────────────

    @Test
    fun `todos los TipoNotificacion tienen label no vacío`() {
        TipoNotificacion.values().forEach { tipo ->
            assertTrue(
                "TipoNotificacion.${tipo.name} debe tener label no vacío",
                tipo.label.isNotBlank()
            )
        }
    }
}
