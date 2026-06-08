package com.example.swo.ui

import com.example.swo.data.categories.local.CategoryDao
import com.example.swo.data.categories.local.CategoryEntity
import com.example.swo.data.incidents.local.IncidentDao
import com.example.swo.data.incidents.local.ProjectIncidentCount
import com.example.swo.ui.reports.ReportsViewModel
import com.example.swo.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val incidentDao: IncidentDao = mockk(relaxed = true)
    private val categoryDao: CategoryDao = mockk(relaxed = true)

    private fun configurarDaosMock(
        total: Int = 0,
        open: Int = 0,
        inProgress: Int = 0,
        resolved: Int = 0,
        cancelled: Int = 0,
        critical: Int = 0,
        high: Int = 0,
        medium: Int = 0,
        low: Int = 0,
        byProject: List<ProjectIncidentCount> = emptyList(),
        categorias: List<CategoryEntity> = emptyList()
    ) {
        every { incidentDao.getTotalCount() }          returns MutableStateFlow(total)
        every { incidentDao.getOpenCount() }           returns MutableStateFlow(open)
        every { incidentDao.getInProgressCount() }     returns MutableStateFlow(inProgress)
        every { incidentDao.getResolvedCount() }       returns MutableStateFlow(resolved)
        every { incidentDao.getCancelledCount() }      returns MutableStateFlow(cancelled)
        every { incidentDao.getCriticalCount() }       returns MutableStateFlow(critical)
        every { incidentDao.getHighCount() }           returns MutableStateFlow(high)
        every { incidentDao.getMediumCount() }         returns MutableStateFlow(medium)
        every { incidentDao.getLowCount() }            returns MutableStateFlow(low)
        every { incidentDao.getIncidentsByProject() }  returns MutableStateFlow(byProject)
        every { categoryDao.getAllCategories() }        returns MutableStateFlow(categorias)
    }

    // ── Estado inicial ────────────────────────────────────────────────────────

    @Test
    fun `estado inicial tiene isLoading en true`() {
        configurarDaosMock()
        val viewModel = ReportsViewModel(incidentDao, categoryDao)

        assertTrue(viewModel.stats.value.isLoading)
    }

    // ── Estadísticas por estado ───────────────────────────────────────────────

    @Test
    fun `stats combina correctamente los conteos por estado`() = runTest {
        configurarDaosMock(
            total = 50,
            open = 20,
            inProgress = 10,
            resolved = 15,
            cancelled = 5
        )

        val viewModel = ReportsViewModel(incidentDao, categoryDao)
        val job = launch { viewModel.stats.collect { } }
        advanceUntilIdle()

        val stats = viewModel.stats.value
        assertEquals(50, stats.total)
        assertEquals(20, stats.open)
        assertEquals(10, stats.inProgress)
        assertEquals(15, stats.resolved)
        assertEquals(5,  stats.cancelled)
        assertFalse(stats.isLoading)

        job.cancel()
    }

    // ── Tasa de resolución ────────────────────────────────────────────────────

    @Test
    fun `tasa de resolución se calcula correctamente`() = runTest {
        // 10 resueltas de 40 totales = 25%
        configurarDaosMock(total = 40, resolved = 10)

        val viewModel = ReportsViewModel(incidentDao, categoryDao)
        val job = launch { viewModel.stats.collect { } }
        advanceUntilIdle()

        assertEquals(25f, viewModel.stats.value.resolutionRate, 0.01f)

        job.cancel()
    }

    @Test
    fun `tasa de resolución es 0 cuando no hay incidencias`() = runTest {
        configurarDaosMock(total = 0, resolved = 0)

        val viewModel = ReportsViewModel(incidentDao, categoryDao)
        val job = launch { viewModel.stats.collect { } }
        advanceUntilIdle()

        assertEquals(0f, viewModel.stats.value.resolutionRate, 0.001f)

        job.cancel()
    }

    // ── Estadísticas por prioridad ────────────────────────────────────────────

    @Test
    fun `stats incluye conteos por prioridad`() = runTest {
        configurarDaosMock(
            total = 30,
            critical = 3,
            high = 10,
            medium = 12,
            low = 5
        )

        val viewModel = ReportsViewModel(incidentDao, categoryDao)
        val job = launch { viewModel.stats.collect { } }
        advanceUntilIdle()

        val stats = viewModel.stats.value
        assertEquals(3,  stats.critical)
        assertEquals(10, stats.high)
        assertEquals(12, stats.medium)
        assertEquals(5,  stats.low)

        job.cancel()
    }

    // ── Incidencias por proyecto ──────────────────────────────────────────────

    @Test
    fun `stats incluye agrupación por proyecto`() = runTest {
        val porProyecto = listOf(
            ProjectIncidentCount("SWO Backend", 15),
            ProjectIncidentCount("SWO Mobile", 8)
        )
        configurarDaosMock(total = 23, byProject = porProyecto)

        val viewModel = ReportsViewModel(incidentDao, categoryDao)
        val job = launch { viewModel.stats.collect { } }
        advanceUntilIdle()

        val stats = viewModel.stats.value
        assertEquals(2, stats.byProject.size)
        assertEquals("SWO Backend", stats.byProject[0].projectName)
        assertEquals(15, stats.byProject[0].count)

        job.cancel()
    }

    // ── Estadísticas de categorías ────────────────────────────────────────────

    @Test
    fun `categoryStats se mapea desde las entidades de categoría`() = runTest {
        val categorias = listOf(
            CategoryEntity(id = "1", name = "Hardware",  description = "Fallas hardware",  color = "#FF5733"),
            CategoryEntity(id = "2", name = "Software",  description = "Fallas software",  color = "#3498DB"),
            CategoryEntity(id = "3", name = "Red",       description = "Problemas de red", color = "#2ECC71")
        )
        configurarDaosMock(categorias = categorias)

        val viewModel = ReportsViewModel(incidentDao, categoryDao)
        val job = launch { viewModel.categoryStats.collect { } }
        advanceUntilIdle()

        val catStats = viewModel.categoryStats.value
        assertEquals(3, catStats.size)
        assertEquals("Hardware", catStats[0].name)
        assertEquals("#FF5733",  catStats[0].color)
        assertEquals("Software", catStats[1].name)

        job.cancel()
    }

    @Test
    fun `categoryStats es lista vacía cuando no hay categorías`() = runTest {
        configurarDaosMock(categorias = emptyList())

        val viewModel = ReportsViewModel(incidentDao, categoryDao)
        val job = launch { viewModel.categoryStats.collect { } }
        advanceUntilIdle()

        assertTrue(viewModel.categoryStats.value.isEmpty())

        job.cancel()
    }
}
