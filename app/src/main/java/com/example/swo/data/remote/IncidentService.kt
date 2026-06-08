package com.example.swo.data.remote

import com.example.swo.domain.model.Incident
import retrofit2.Response
import retrofit2.http.*

interface IncidentService {
    @GET("incidents")
    suspend fun getIncidents(): Response<List<Incident>>

    @POST("incidents")
    suspend fun createIncident(@Body incident: Incident): Response<Incident>

    @PATCH("incidents/{id}/resolve")
    suspend fun resolveIncident(
        @Path("id") id: String,
        @Body solution: Map<String, String>
    ): Response<Unit>
}
