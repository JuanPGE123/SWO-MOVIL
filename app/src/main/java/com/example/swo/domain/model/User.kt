package com.example.swo.domain.model

enum class UserRole(val label: String) {
    ADMIN("Administrador"),
    TECHNICIAN("Técnico"),
    CLIENT("Cliente")
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String = "",
    val role: UserRole,
    val isActive: Boolean,
    val avatarUrl: String? = null
)
