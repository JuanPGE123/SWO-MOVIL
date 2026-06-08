package com.example.swo.ui

import com.example.swo.data.incidents.local.IncidentDao
import com.example.swo.data.incidents.local.IncidentEntity
import com.example.swo.data.projects.local.ProjectDao
import com.example.swo.data.users.local.UserDao
import com.example.swo.data.users.local.UserEntity
import com.example.swo.ui.dashboard.DashboardViewModel
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
class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val incidentDao: IncidentDao = mockk(relaxed = true)
    private val projectDao: ProjectDao   = mockk(relaxed = true)
    private val userDao: UserDao         = mockk(relaxed = true)

    private fun entidadDeIncidencia(id: String, status: String = "OPEN") = IncidentEntity(
        id = id,
        title = "Incidencia $id",
        description = "Descripción",
        status = status,
        priority = "HIGH",
        reportedBy = "Analista",
        projectName = "SWO",
        createdAt = "2025-01-01"
    )

    private fun entidadDeUsuario(id: String) = UserEntity(
        id = id,
        name = "Usuario $id",
        email = "u$id@sena.edu.co",
        role = "TECHNICIAN",
        isActive = true,
        avatarUrl = null
    )

    private fun configurarDaosMock(
        total: Int = 0,
        open: Int = 0,
        inProgress: Int = 0,
        resolved: Int = 0,
        recientes: List<IncidentEntity> = emptyList(),
        proyectos: Int = 0,
        usuarios: List<UserEntity> = emptyList()
    ) {
        every { incidentDao.getTotalCount() }           returns MutableStateFlow(total)
        every { incidentDao.getOpenCount() }            returns MutableStateFlow(open)
        every { incidentDao.getInProgressCount() }      returns MutableStateFlow(inProgress)
        every { incidentDao.getResolvedCount() }        returns MutableStateFlow(resolved)
        every { incidentDao.getRecentActiveIncidents() } returns MutableStateFlow(recientes)
        every { projectDao.getCount() }                 returns MutableStateFlow(proyectos)
        every { userDao.getAllUsers() }                  returns MutableStateFlow(usuarios)
    }

    // ── Estado inicial ────────────────────────────────────────────────────────

    @Test
    fun `estado inicial tiene isLoading en true`() {
        configurarDaosMock()
        val viewModel = DashboardViewModel(incidentDao, projectDao, userDao)

        // Antes de suscribirse, el StateFlow retorna el valor inicial
        assertTrue(viewModel.stats.value.isLoading)
    }

    // ── Combinación de conteos ────────────────────────────────────────────────

    @Test
    fun `stats combina correctamente los conteos de incidencias`() = runTest {
        configurarDaosMock(
            total = 20,
            open = 8,
            inProgress = 5,
            resolved = 7,
            proyectos = 3,
            usuarios = listOf(entidadDeUsuario("1"), entidadDeUsuario("2"))
        )

        val viewModel = DashboardViewModel(incidentDao, projectDao, userDao)
        val job = launch { viewModel.stats.collect { } }
        advanceUntilIdle()

        val stats = viewModel.stats.value
        assertEquals(20, stats.totalIncidents)
        assertEquals(8,  stats.openIncidents)
        assertEquals(5,  stats.inProgressIncidents)
        assertEquals(7,  stats.resolvedIncidents)
        assertEquals(3,  stats.totalProjects)
        assertEquals(2,  stats.totalUsers)
        assertFalse(stats.isLoading)

        job.cancel()
    }

    @Test
    fun `stats con todos los valores en cero es consistente`() = runTest {
        configurarDaosMock()

        val viewModel = DashboardViewModel(incidentDao, projectDao, userDao)
        val job = launch { viewModel.stats.collect { } }
        advanceUntilIdle()

        val stats = viewModel.stats.value
        assertEquals(0, stats.totalIncidents)
        assertEquals(0, stats.openIncidents)
        assertEquals(0, stats.inProgressIncidents)
        assertEquals(0, stats.resolvedIncidents)
        assertEquals(0, stats.totalProjects)
        assertEquals(0, stats.totalUsers)

        job.cancel()
    }

    // ── Incidencias recientes ─────────────────────────────────────────────────

    @Test
    fun `stats incluye las incidencias recientes activas`() = runTest {
        val recientes = listOf(
            entidadDeIncidencia("A", "OPEN"),
            entidadDeIncidencia("B", "IN_PROGRESS")
        )
        configurarDaosMock(recientes = recientes)

        val viewModel = DashboardViewModel(incidentDao, projectDao, userDao)
        val job = launch { viewModel.stats.collect { } }
        advanceUntilIdle()

        val stats = viewModel.stats.value
        assertEquals(2, stats.recentIncidents.size)
        assertEquals("A", stats.recentIncidents[0].id)
        assertEquals("B", stats.recentIncidents[1].id)

        job.cancel()
    }

    // ── Actualización reactiva ────────────────────────────────────────────────

    @Test
    fun `stats se actualiza cuando el flow de incidencias emite un nuevo valor`() = runTest {
        val totalFlow = MutableStateFlow(5)
        every { incidentDao.getTotalCount() }           returns totalFlow
        every { incidentDao.getOpenCount() }            returns MutableStateFlow(5)
        every { incidentDao.getInProgressCount() }      returns MutableStateFlow(0)
        every { incidentDao.getResolvedCount() }        returns MutableStateFlow(0)
        every { incidentDao.getRecentActiveIncidents() } returns MutableStateFlow(emptyList())
        every { projectDao.getCount() }                 returns MutableStateFlow(0)
        every { userDao.getAllUsers() }                  returns MutableStateFlow(emptyList())

        val viewModel = DashboardViewModel(incidentDao, projectDao, userDao)
        val job = launch { viewModel.stats.collect { } }
        advanceUntilIdle()

        assertEquals(5, viewModel.stats.value.totalIncidents)

        // Simulamos que llegan más incidencias
        totalFlow.value = 12
        advanceUntilIdle()

        assertEquals(12, viewModel.stats.value.totalIncidents)

        job.cancel()
    }
}
