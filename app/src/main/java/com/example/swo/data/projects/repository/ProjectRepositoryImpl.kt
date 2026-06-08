package com.example.swo.data.projects.repository

import com.example.swo.data.projects.local.ProjectDao
import com.example.swo.data.projects.local.toDomain
import com.example.swo.data.projects.local.toEntity
import com.example.swo.data.projects.remote.ProjectApi
import com.example.swo.domain.projects.ProjectRepository
import com.example.swo.domain.model.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepositoryImpl @Inject constructor(
    private val projectDao: ProjectDao,
    private val projectApi: ProjectApi
) : ProjectRepository {

    override fun getProjects(): Flow<List<Project>> {
        return projectDao.getAllProjects().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveProject(project: Project) {
        projectDao.insertProject(project.toEntity(isSynced = false))
        try {
            val response = projectApi.createProject(project)
            if (response.isSuccessful) projectDao.markAsSynced(project.id)
        } catch (e: Exception) { }
    }

    override suspend fun deleteProject(projectId: String) {
        projectDao.deleteProjectById(projectId)
        try { projectApi.deleteProject(projectId) } catch (e: Exception) { }
    }
}
