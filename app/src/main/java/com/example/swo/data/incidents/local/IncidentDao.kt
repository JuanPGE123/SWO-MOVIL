package com.example.swo.data.incidents.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {
    @Query("SELECT * FROM incidents ORDER BY lastModified DESC")
    fun getAllIncidents(): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM incidents WHERE id = :id")
    fun getIncidentById(id: String): Flow<IncidentEntity?>

    @Query("SELECT * FROM incidents WHERE id = :id")
    suspend fun getIncidentByIdOnce(id: String): IncidentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: IncidentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncidents(incidents: List<IncidentEntity>)

    @Update
    suspend fun updateIncident(incident: IncidentEntity)

    @Delete
    suspend fun deleteIncident(incident: IncidentEntity)

    @Query("UPDATE incidents SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("SELECT * FROM incidents WHERE isSynced = 0")
    suspend fun getUnsyncedIncidents(): List<IncidentEntity>

    // --- Queries para Reportes ---

    @Query("SELECT COUNT(*) FROM incidents")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM incidents WHERE status = 'OPEN'")
    fun getOpenCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM incidents WHERE status = 'IN_PROGRESS'")
    fun getInProgressCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM incidents WHERE status = 'RESOLVED'")
    fun getResolvedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM incidents WHERE status = 'CANCELLED'")
    fun getCancelledCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM incidents WHERE priority = 'CRITICAL'")
    fun getCriticalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM incidents WHERE priority = 'HIGH'")
    fun getHighCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM incidents WHERE priority = 'MEDIUM'")
    fun getMediumCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM incidents WHERE priority = 'LOW'")
    fun getLowCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM incidents WHERE categoryId = :categoryId")
    fun getCountByCategory(categoryId: String): Flow<Int>

    @Query("SELECT projectName, COUNT(*) as count FROM incidents GROUP BY projectName ORDER BY count DESC")
    fun getIncidentsByProject(): Flow<List<ProjectIncidentCount>>

    @Query("SELECT * FROM incidents WHERE status = 'OPEN' OR status = 'IN_PROGRESS' ORDER BY lastModified DESC LIMIT 5")
    fun getRecentActiveIncidents(): Flow<List<IncidentEntity>>
}

data class ProjectIncidentCount(
    val projectName: String,
    val count: Int
)
