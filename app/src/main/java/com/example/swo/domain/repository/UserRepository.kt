package com.example.swo.domain.repository

import com.example.swo.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers(): Flow<List<User>>
    suspend fun toggleUserStatus(userId: String, isActive: Boolean)
    suspend fun saveUser(user: User)
}
