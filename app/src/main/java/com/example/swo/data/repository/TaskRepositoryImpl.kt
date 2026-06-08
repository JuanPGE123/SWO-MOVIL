package com.example.swo.data.repository

import com.example.swo.data.local.TaskDao
import com.example.swo.data.local.toEntity
import com.example.swo.data.remote.TaskApi
import com.example.swo.data.remote.toDto
import com.example.swo.domain.model.Task
import com.example.swo.domain.repository.TaskRepository
import com.example.swo.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import retrofit2.HttpException
import javax.inject.Inject
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl @Inject constructor(
    private val api: TaskApi,
    private val dao: TaskDao
) : TaskRepository {

    override fun getTasks(fetchFromRemote: Boolean): Flow<Resource<List<Task>>> = flow {
        emit(Resource.Loading())

        val localTasks = dao.getTasks()
        
        // Emitting local data first
        dao.getTasks().collect { entities ->
            val tasks = entities.map { it.toTask() }
            emit(Resource.Success(tasks))
            
            if (fetchFromRemote && tasks.isEmpty()) {
                try {
                    val remoteTasks = api.getTasks()
                    dao.clearTasks()
                    dao.insertTasks(remoteTasks.map { it.toTask().toEntity() })
                } catch(e: HttpException) {
                    emit(Resource.Error("An error occurred: ${e.localizedMessage}", tasks))
                } catch(e: IOException) {
                    emit(Resource.Error("Couldn't reach server. Check your internet connection.", tasks))
                }
            }
        }
    }

    override suspend fun saveTask(task: Task): Resource<Unit> {
        return try {
            // Optimistic update locally
            dao.insertTask(task.toEntity())
            // Sync to remote
            // api.createTask(task.toDto()) // Commented for offline sim
            Resource.Success(Unit)
        } catch(e: Exception) {
            Resource.Error("Could not save task")
        }
    }

    override suspend fun deleteTask(task: Task): Resource<Unit> {
        return try {
            dao.deleteTask(task.toEntity())
            // api.deleteTask(task.id) // Commented for offline sim
            Resource.Success(Unit)
        } catch(e: Exception) {
            Resource.Error("Could not delete task")
        }
    }
}
