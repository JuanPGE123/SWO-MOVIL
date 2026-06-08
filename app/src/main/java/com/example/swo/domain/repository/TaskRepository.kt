package com.example.swo.domain.repository

import com.example.swo.domain.model.Task
import com.example.swo.utils.Resource
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(fetchFromRemote: Boolean): Flow<Resource<List<Task>>>
    suspend fun saveTask(task: Task): Resource<Unit>
    suspend fun deleteTask(task: Task): Resource<Unit>
}
