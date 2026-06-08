package com.example.swo.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swo.data.incidents.local.IncidentDao
import com.example.swo.data.incidents.local.IncidentEntity
import com.example.swo.data.projects.local.ProjectDao
import com.example.swo.data.users.local.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DashboardStats(
    val totalIncidents: Int = 0,
    val openIncidents: Int = 0,
    val inProgressIncidents: Int = 0,
    val resolvedIncidents: Int = 0,
    val totalProjects: Int = 0,
    val totalUsers: Int = 0,
    val recentIncidents: List<IncidentEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val incidentDao: IncidentDao,
    private val projectDao: ProjectDao,
    private val userDao: UserDao
) : ViewModel() {

    // Users count as a Flow
    private val usersCountFlow: Flow<Int> = userDao.getAllUsers()
        .map { it.size }

    val stats: StateFlow<DashboardStats> = combine(
        incidentDao.getTotalCount(),
        incidentDao.getOpenCount(),
        incidentDao.getInProgressCount(),
        incidentDao.getResolvedCount(),
        incidentDao.getRecentActiveIncidents()
    ) { total, open, inProgress, resolved, recent ->
        DashboardStats(
            totalIncidents = total,
            openIncidents = open,
            inProgressIncidents = inProgress,
            resolvedIncidents = resolved,
            recentIncidents = recent,
            isLoading = false
        )
    }.combine(projectDao.getCount()) { base, projectCount ->
        base.copy(totalProjects = projectCount)
    }.combine(usersCountFlow) { base, userCount ->
        base.copy(totalUsers = userCount)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())
}
