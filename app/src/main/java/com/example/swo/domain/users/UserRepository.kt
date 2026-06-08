package com.example.swo.domain.users

import com.example.swo.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers(): Flow<List<User>>
    suspend fun saveUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun deleteUser(userId: String)
    suspend fun toggleUserStatus(userId: String, isActive: Boolean)
    suspend fun getUserByCredentials(email: String, password: String): User?
}
