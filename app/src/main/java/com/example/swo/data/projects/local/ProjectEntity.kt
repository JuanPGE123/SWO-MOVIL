package com.example.swo.data.projects.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.swo.domain.model.Project
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val status: String,
    val activeIncidentsCount: Int,
    val assignedEngineers: String, // Stored as JSON string
    val isSynced: Boolean = true
)

fun ProjectEntity.toDomain(): Project {
    val listType = object : TypeToken<List<String>>() {}.type
    val engineers: List<String> = Gson().fromJson(assignedEngineers, listType)
    return Project(
        id = id,
        name = name,
        description = description,
        activeIncidentsCount = activeIncidentsCount,
        assignedEngineers = engineers,
        status = status
    )
}

fun Project.toEntity(isSynced: Boolean = true) = ProjectEntity(
    id = id,
    name = name,
    description = description,
    status = status,
    activeIncidentsCount = activeIncidentsCount,
    assignedEngineers = Gson().toJson(assignedEngineers),
    isSynced = isSynced
)
