package com.example.swo.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swo.data.categories.local.CategoryDao
import com.example.swo.data.incidents.local.IncidentDao
import com.example.swo.data.incidents.local.ProjectIncidentCount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class ReportStats(
    val total: Int = 0,
    val open: Int = 0,
    val inProgress: Int = 0,
    val resolved: Int = 0,
    val cancelled: Int = 0,
    val critical: Int = 0,
    val high: Int = 0,
    val medium: Int = 0,
    val low: Int = 0,
    val resolutionRate: Float = 0f,
    val byProject: List<ProjectIncidentCount> = emptyList(),
    val isLoading: Boolean = true
)

data class CategoryStat(val name: String, val count: Int, val color: String)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val incidentDao: IncidentDao,
    private val categoryDao: CategoryDao
) : ViewModel() {

    private val statusStats: Flow<ReportStats> = combine(
        incidentDao.getTotalCount(),
        incidentDao.getOpenCount(),
        incidentDao.getInProgressCount(),
        incidentDao.getResolvedCount(),
        incidentDao.getCancelledCount()
    ) { total, open, inProgress, resolved, cancelled ->
        val rate = if (total > 0) (resolved.toFloat() / total.toFloat()) * 100f else 0f
        ReportStats(
            total = total,
            open = open,
            inProgress = inProgress,
            resolved = resolved,
            cancelled = cancelled,
            resolutionRate = rate,
            isLoading = false
        )
    }

    private val priorityStats: Flow<List<Int>> = combine(
        incidentDao.getCriticalCount(),
        incidentDao.getHighCount(),
        incidentDao.getMediumCount(),
        incidentDao.getLowCount()
    ) { critical, high, medium, low ->
        listOf(critical, high, medium, low)
    }

    val stats: StateFlow<ReportStats> = combine(
        statusStats,
        priorityStats,
        incidentDao.getIncidentsByProject()
    ) { base, priorities, byProject ->
        base.copy(
            critical = priorities[0],
            high = priorities[1],
            medium = priorities[2],
            low = priorities[3],
            byProject = byProject
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ReportStats()
    )

    val categoryStats: StateFlow<List<CategoryStat>> =
        categoryDao.getAllCategories()
            .map { cats ->
                cats.map { cat ->
                    CategoryStat(name = cat.name, count = 0, color = cat.color)
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
