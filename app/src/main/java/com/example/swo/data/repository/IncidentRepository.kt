package com.example.swo.data.repository

import com.example.swo.model.Incident

/**
 * Repositorio para gestionar los datos de los incidentes.
 *
 * En una aplicación real, este repositorio se comunicaría con una base de datos local
 * (como Room) o una API remota. Para este proyecto, simulamos los datos en memoria.
 */
class IncidentRepository {

    // Lista simulada de incidentes para fines de demostración.
    private val incidents = listOf(
        Incident(
            id = "INC-001",
            title = "Fallo en el servidor de producción",
            description = "El servidor principal ha dejado de responder. Se requiere reinicio inmediato. Múltiples servicios afectados.",
            status = "Abierto",
            priority = "Alta",
            date = "2024-05-20"
        ),
        Incident(
            id = "INC-002",
            title = "Error de acceso a la base de datos",
            description = "Algunos usuarios reportan que no pueden acceder a sus perfiles. Parece un problema de conexión con la BD.",
            status = "En Progreso",
            priority = "Media",
            date = "2024-05-19"
        ),
        Incident(
            id = "INC-003",
            title = "La aplicación móvil se cierra inesperadamente",
            description = "La última actualización de la app (v1.2.1) está causando cierres forzosos en dispositivos Android 13.",
            status = "Abierto",
            priority = "Alta",
            date = "2024-05-21"
        ),
        Incident(
            id = "INC-004",
            title = "Problema de visualización en el portal web",
            description = "El CSS del portal de clientes no se carga correctamente en navegadores Safari, afectando la experiencia de usuario.",
            status = "Cerrado",
            priority = "Baja",
            date = "2024-05-15"
        )
    )

    /**
     * Devuelve la lista completa de incidentes.
     */
    fun getIncidents(): List<Incident> {
        return incidents
    }

    /**
     * Busca y devuelve un incidente por su ID.
     * Devuelve null si no se encuentra el incidente.
     */
    fun getIncidentById(id: String): Incident? {
        return incidents.find { it.id == id }
    }
}
