package com.example.swo.domain.auth

data class User(
    val id: String,
    val corporateId: String,
    val name: String,
    val role: UserRole
)

enum class UserRole {
    ADMIN,
    SUPPORT,
    CLIENT
}
