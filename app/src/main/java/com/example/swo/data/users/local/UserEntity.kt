package com.example.swo.data.users.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.swo.domain.model.User
import com.example.swo.domain.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val password: String = "",
    val role: String,
    val isActive: Boolean,
    val avatarUrl: String?
)

fun UserEntity.toDomain() = User(
    id = id,
    name = name,
    email = email,
    password = password,
    role = try { UserRole.valueOf(role) } catch (e: Exception) { UserRole.CLIENT },
    isActive = isActive,
    avatarUrl = avatarUrl
)

fun User.toEntity() = UserEntity(
    id = id,
    name = name,
    email = email,
    password = password,
    role = role.name,
    isActive = isActive,
    avatarUrl = avatarUrl
)
