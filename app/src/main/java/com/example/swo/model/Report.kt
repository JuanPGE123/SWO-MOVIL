package com.example.swo.model

data class Report(
    val id: String,
    val title: String,
    val type: String,
    val data: String, // Representación JSON o CSV simplificada
    val createdAt: String
)
