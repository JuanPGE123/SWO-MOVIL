package com.example.swo.data.incidents.remote

import com.example.swo.domain.model.Incident
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface IncidentApi {
    @GET("incidents")
    suspend fun getIncidents(): List<Incident>

    @POST("incidents")
    suspend fun postIncident(@Body incident: Incident): Response<Unit>

    @PATCH("incidents/{id}/resolve")
    suspend fun resolveIncident(
        @Path("id") id: String,
        @Body solution: Map<String, String>
    ): Response<Unit>
}
