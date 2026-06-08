package com.example.swo.model

/**
 * Data class que representa un incidente.
 *
 * @property id El identificador único del incidente.
 * @property title El título o resumen del incidente.
 * @property description La descripción detallada del incidente.
 * @property status El estado actual del incidente (ej. Abierto, En Progreso, Cerrado).
 * @property priority La prioridad del incidente (ej. Alta, Media, Baja).
 * @property date La fecha en que se reportó el incidente.
 */
data class Incident(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val priority: String,
    val date: String
)
