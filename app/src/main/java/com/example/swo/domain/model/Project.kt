package com.example.swo.domain.model

data class Project(
    val id: String,
    val name: String,
    val description: String,
    val activeIncidentsCount: Int,
    val assignedEngineers: List<String>,
    val status: String
)
