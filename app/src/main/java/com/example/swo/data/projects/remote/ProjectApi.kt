package com.example.swo.data.projects.remote

import com.example.swo.domain.model.Project
import retrofit2.Response
import retrofit2.http.*

interface ProjectApi {
    @GET("projects")
    suspend fun getProjects(): List<Project>

    @POST("projects")
    suspend fun createProject(@Body project: Project): Response<Unit>

    @DELETE("projects/{id}")
    suspend fun deleteProject(@Path("id") id: String): Response<Unit>
}
