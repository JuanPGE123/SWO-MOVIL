package com.example.swo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.swo.domain.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val createdAt: Long
) {
    fun toTask(): Task = Task(id, title, description, isCompleted, createdAt)
}

fun Task.toEntity(): TaskEntity = TaskEntity(id, title, description, isCompleted, createdAt)
