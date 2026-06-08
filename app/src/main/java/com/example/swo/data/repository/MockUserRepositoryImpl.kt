package com.example.swo.data.repository

import com.example.swo.domain.model.User
import com.example.swo.domain.model.UserRole
import com.example.swo.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockUserRepositoryImpl @Inject constructor() : UserRepository {
    override fun getUsers(): Flow<List<User>> = flowOf(
        listOf(
            User(
                id = "1",
                name = "Admin",
                email = "admin@example.com",
                role = UserRole.ADMIN,
                isActive = true
            )
        )
    )

    override suspend fun toggleUserStatus(userId: String, isActive: Boolean) {}
    override suspend fun saveUser(user: User) {}
}
