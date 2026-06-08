package com.example.swo.ui

import com.example.swo.domain.model.User
import com.example.swo.domain.model.UserRole
import com.example.swo.domain.users.UserRepository
import com.example.swo.ui.screens.LoginViewModel
import com.example.swo.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository: UserRepository = mockk(relaxed = true)
    private lateinit var viewModel: LoginViewModel

    private val usuarioActivo = User(
        id = "1",
        name = "Ana García",
        email = "ana@sena.edu.co",
        password = "password123",
        role = UserRole.TECHNICIAN,
        isActive = true
    )

    private val usuarioInactivo = usuarioActivo.copy(isActive = false)

    @Before
    fun setUp() {
        // El repositorio devuelve un flow vacío para getUsers (llamado en otros contextos)
        coEvery { userRepository.getUsers() } returns flowOf(emptyList())
        viewModel = LoginViewModel(userRepository)
    }

    // ── Validación de campos vacíos ───────────────────────────────────────────

    @Test
    fun `login con correo vacío establece mensaje de error`() {
        viewModel.login("", "password123") {}

        assertEquals("Ingresa tu correo y contraseña", viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `login con contraseña vacía establece mensaje de error`() {
        viewModel.login("ana@sena.edu.co", "") {}

        assertEquals("Ingresa tu correo y contraseña", viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `login con campos vacíos no llama al repositorio`() = runTest {
        viewModel.login("", "") {}

        coVerify(exactly = 0) { userRepository.getUserByCredentials(any(), any()) }
    }

    // ── Credenciales inválidas ────────────────────────────────────────────────

    @Test
    fun `login con credenciales incorrectas establece error de credenciales`() = runTest {
        coEvery {
            userRepository.getUserByCredentials(any(), any())
        } returns null

        viewModel.login("noexiste@sena.edu.co", "wrongpass") {}

        assertEquals("Correo o contraseña incorrectos", viewModel.state.value.error)
        assertNull(viewModel.state.value.loggedUser)
        assertFalse(viewModel.state.value.isLoading)
    }

    // ── Usuario inactivo ─────────────────────────────────────────────────────

    @Test
    fun `login con usuario inactivo establece error de cuenta inactiva`() = runTest {
        coEvery {
            userRepository.getUserByCredentials(any(), any())
        } returns usuarioInactivo

        viewModel.login("ana@sena.edu.co", "password123") {}

        assertEquals(
            "Tu cuenta está inactiva. Contacta al administrador.",
            viewModel.state.value.error
        )
        assertNull(viewModel.state.value.loggedUser)
        assertFalse(viewModel.state.value.isLoading)
    }

    // ── Login exitoso ────────────────────────────────────────────────────────

    @Test
    fun `login exitoso establece el usuario en el estado`() = runTest {
        coEvery {
            userRepository.getUserByCredentials("ana@sena.edu.co", "password123")
        } returns usuarioActivo

        var usuarioRecibido: User? = null
        viewModel.login("ana@sena.edu.co", "password123") { usuarioRecibido = it }

        assertNotNull(usuarioRecibido)
        assertEquals("Ana García", usuarioRecibido!!.name)
        assertEquals(usuarioActivo, viewModel.state.value.loggedUser)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `login normaliza el correo a minúsculas antes de consultar`() = runTest {
        coEvery {
            userRepository.getUserByCredentials("ana@sena.edu.co", "password123")
        } returns usuarioActivo

        viewModel.login("  ANA@SENA.EDU.CO  ", "password123") {}

        coVerify(exactly = 1) {
            userRepository.getUserByCredentials("ana@sena.edu.co", "password123")
        }
    }

    // ── clearError ───────────────────────────────────────────────────────────

    @Test
    fun `clearError limpia el mensaje de error del estado`() {
        viewModel.login("", "") {} // provoca un error
        assertNotNull(viewModel.state.value.error)

        viewModel.clearError()

        assertNull(viewModel.state.value.error)
    }
}
