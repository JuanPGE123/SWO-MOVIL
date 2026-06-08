package com.example.swo.data.users.repository

import com.example.swo.data.users.local.UserDao
import com.example.swo.data.users.local.toDomain
import com.example.swo.data.users.local.toEntity
import com.example.swo.data.users.remote.UserApi
import com.example.swo.domain.users.UserRepository
import com.example.swo.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val userApi: UserApi
) : UserRepository {

    override fun getUsers(): Flow<List<User>> = userDao.getAllUsers().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun saveUser(user: User) {
        userDao.insertUser(user.toEntity())
        try { userApi.createUser(user) } catch (e: Exception) { }
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user.toEntity())
        try { userApi.createUser(user) } catch (e: Exception) { }
    }

    override suspend fun deleteUser(userId: String) {
        userDao.deleteUserById(userId)
        try { userApi.deleteUser(userId) } catch (e: Exception) { }
    }

    override suspend fun toggleUserStatus(userId: String, isActive: Boolean) {
        userDao.setUserActive(userId, isActive)
    }

    override suspend fun getUserByCredentials(email: String, password: String): User? {
        return userDao.getUserByCredentials(email.trim().lowercase(), password)?.toDomain()
    }
}
