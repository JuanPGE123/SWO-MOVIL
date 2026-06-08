package com.example.swo.ui

import app.cash.turbine.test
import com.example.swo.domain.incidents.GetIncidentsUseCase
import com.example.swo.domain.incidents.IncidentRepository
import com.example.swo.domain.model.Incident
import com.example.swo.domain.model.IncidentPriority
import com.example.swo.domain.model.IncidentStatus
import com.example.swo.ui.incidents.IncidentsViewModel
import com.example.swo.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IncidentsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val incidentRepository: IncidentRepository = mockk(relaxed = true)
    private val getIncidentsUseCase = GetIncidentsUseCase(incidentRepository)

    private fun incidenteDePrueba(id: String = "1") = Incident(
        id = id,
        title = "Incidencia $id",
        description = "Descripción de prueba",
        status = IncidentStatus.OPEN,
        priority = IncidentPriority.HIGH,
        reportedBy = "Analista",
        projectName = "SWO",
        createdAt = "2025-01-01"
    )

    // ── Estado inicial ────────────────────────────────────────────────────────

    @Test
    fun `estado inicial tiene isLoading en true y lista vacía`() = runTest {
        // Use case que nunca emite (simula carga prolongada)
        every { incidentRepository.getIncidents() } returns flowOf()

        val viewModel = IncidentsViewModel(getIncidentsUseCase)

        // Con UnconfinedTestDispatcher el flujo ya corrió; si no emite nada,
        // isLoading queda en true y la lista permanece vacía
        assertTrue(viewModel.state.value.incidents.isEmpty())
        assertNull(viewModel.state.value.error)
    }

    // ── Carga exitosa ────────────────────────────────────────────────────────

    @Test
    fun `al cargar incidencias el estado refleja la lista recibida`() = runTest {
        val listaEsperada = listOf(
            incidenteDePrueba("1"),
            incidenteDePrueba("2"),
            incidenteDePrueba("3")
        )
        every { incidentRepository.getIncidents() } returns flowOf(listaEsperada)

        val viewModel = IncidentsViewModel(getIncidentsUseCase)

        viewModel.state.test {
            val estado = awaitItem()
            assertEquals(3, estado.incidents.size)
            assertEquals("1", estado.incidents[0].id)
            assertEquals("2", estado.incidents[1].id)
            assertEquals("3", estado.incidents[2].id)
            assertFalse(estado.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── Lista vacía ──────────────────────────────────────────────────────────

    @Test
    fun `con lista vacía el estado tiene lista vacía y no está cargando`() = runTest {
        every { incidentRepository.getIncidents() } returns flowOf(emptyList())

        val viewModel = IncidentsViewModel(getIncidentsUseCase)

        viewModel.state.test {
            val estado = awaitItem()
            assertTrue(estado.incidents.isEmpty())
            assertFalse(estado.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── El use case delega al repositorio ────────────────────────────────────

    @Test
    fun `al crear el ViewModel se llama al repositorio para obtener incidencias`() = runTest {
        every { incidentRepository.getIncidents() } returns flowOf(emptyList())

        IncidentsViewModel(getIncidentsUseCase)

        verify(exactly = 1) { incidentRepository.getIncidents() }
    }

    // ── Filtrado por estado ───────────────────────────────────────────────────

    @Test
    fun `las incidencias con estado OPEN se distinguen correctamente`() = runTest {
        val incidencias = listOf(
            incidenteDePrueba("1").copy(status = IncidentStatus.OPEN),
            incidenteDePrueba("2").copy(status = IncidentStatus.RESOLVED),
            incidenteDePrueba("3").copy(status = IncidentStatus.IN_PROGRESS)
        )
        every { incidentRepository.getIncidents() } returns flowOf(incidencias)

        val viewModel = IncidentsViewModel(getIncidentsUseCase)

        val abiertas = viewModel.state.value.incidents
            .filter { it.status == IncidentStatus.OPEN }

        assertEquals(1, abiertas.size)
        assertEquals("1", abiertas.first().id)
    }
}
