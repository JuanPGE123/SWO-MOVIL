package com.example.swo.data.remote

import com.example.swo.domain.model.Task
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TaskApi {
    @GET("tasks")
    suspend fun getTasks(): List<TaskDto>

    @POST("tasks")
    suspend fun createTask(@Body task: TaskDto): TaskDto

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String): Response<Unit>
}

data class TaskDto(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val createdAt: Long
) {
    fun toTask(): Task = Task(id, title, description, isCompleted, createdAt)
}

fun Task.toDto(): TaskDto = TaskDto(id, title, description, isCompleted, createdAt)
