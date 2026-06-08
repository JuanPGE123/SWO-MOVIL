package com.example.swo.domain.projects

import com.example.swo.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getProjects(): Flow<List<Project>>
    suspend fun saveProject(project: Project)
    suspend fun deleteProject(projectId: String)
}
