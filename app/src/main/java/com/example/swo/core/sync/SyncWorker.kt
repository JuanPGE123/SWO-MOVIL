package com.example.swo.core.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.swo.data.incidents.local.IncidentDao
import com.example.swo.data.remote.IncidentService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val dao: IncidentDao,
    private val service: IncidentService
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): androidx.work.ListenableWorker.Result {
        val unsynced = dao.getUnsyncedIncidents()
        
        unsynced.forEach { incident ->
            try {
                if (incident.status == "RESOLVED") {
                    val response = service.resolveIncident(incident.id, mapOf("notes" to (incident.solutionNotes ?: "")))
                    if (response.isSuccessful) {
                        dao.markAsSynced(incident.id)
                    }
                }
            } catch (e: Exception) {
                return androidx.work.ListenableWorker.Result.retry()
            }
        }
        
        return androidx.work.ListenableWorker.Result.success()
    }
}
