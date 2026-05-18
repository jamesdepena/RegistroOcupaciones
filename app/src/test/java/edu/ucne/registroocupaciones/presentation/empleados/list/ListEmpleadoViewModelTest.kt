package edu.ucne.registroocupaciones.presentation.empleados.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.usecase.DeleteEmpleadoUseCase
import edu.ucne.registroocupaciones.domain.empleados.usecase.ObserveEmpleadoUseCase
import edu.ucne.registroocupaciones.presentation.empleado.list.ListEmpleadoUiEvent
import edu.ucne.registroocupaciones.presentation.empleado.list.ListEmpleadoViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.time.LocalDate

@ExperimentalCoroutinesApi
class ListEmpleadoViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ListEmpleadoViewModel
    private lateinit var observeEmpleadoUseCase: ObserveEmpleadoUseCase
    private lateinit var deleteEmpleadoUseCase: DeleteEmpleadoUseCase

    @Before
    fun setup() {
        observeEmpleadoUseCase = mockk()
        deleteEmpleadoUseCase = mockk()

        every { observeEmpleadoUseCase() } returns flowOf(emptyList())
    }

    @Test
    fun `init o Load carga lista de empleados correctamente`() = runTest {
        // Given
        val empleadosPrueba = listOf(
            Empleado(1, LocalDate.now(), "Pedro Martinez",  "Masculino", 45000.0),
            Empleado(2, LocalDate.now(), "Maria Rodriguez", "Femenino", 55000.0)
        )
        every { observeEmpleadoUseCase() } returns flowOf(empleadosPrueba)

        // When
        viewModel = ListEmpleadoViewModel(observeEmpleadoUseCase, deleteEmpleadoUseCase)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(2, viewModel.state.value.empleados.size)
        assertEquals("Pedro Martinez", viewModel.state.value.empleados[0].nombres)
    }

    @Test
    fun `onEvent Delete elimina empleado con exito`() = runTest {
        // Given
        val empleadoId = 3
        coEvery { deleteEmpleadoUseCase(empleadoId) } returns Unit
        viewModel = ListEmpleadoViewModel(observeEmpleadoUseCase, deleteEmpleadoUseCase)

        // When
        viewModel.onEvent(ListEmpleadoUiEvent.Delete(empleadoId))
        advanceUntilIdle()

        // Then
        coVerify { deleteEmpleadoUseCase(empleadoId) }
        assertEquals("Estudiante eliminado", viewModel.state.value.message)
    }

    @Test
    fun `onEvent Delete maneja excepcion correctamente`() = runTest {
        // Given
        val empleadoId = 4
        coEvery { deleteEmpleadoUseCase(empleadoId) } throws Exception("Error de conexión")
        viewModel = ListEmpleadoViewModel(observeEmpleadoUseCase, deleteEmpleadoUseCase)

        // When
        viewModel.onEvent(ListEmpleadoUiEvent.Delete(empleadoId))
        advanceUntilIdle()

        // Then
        coVerify { deleteEmpleadoUseCase(empleadoId) }
        assertEquals("Error al eliminar: Error de conexión", viewModel.state.value.message)
    }

    @Test
    fun `onEvent CreateNew activa estado de navegacion`() = runTest {
        // Given
        viewModel = ListEmpleadoViewModel(observeEmpleadoUseCase, deleteEmpleadoUseCase)

        // When
        viewModel.onEvent(ListEmpleadoUiEvent.CreateNew)

        // Then
        assertTrue(viewModel.state.value.navigateToCreate)
    }

    @Test
    fun `onEvent Edit activa navegacion enviando el id idóneo`() = runTest {
        // Given
        val targetId = 12
        viewModel = ListEmpleadoViewModel(observeEmpleadoUseCase, deleteEmpleadoUseCase)

        // When
        viewModel.onEvent(ListEmpleadoUiEvent.Edit(targetId))

        // Then
        assertEquals(targetId, viewModel.state.value.navigateToEditId)
    }

    @Test
    fun `onNavigationHandled resetea correctamente las variables de navegacion y mensajes`() = runTest {
        // Given
        viewModel = ListEmpleadoViewModel(observeEmpleadoUseCase, deleteEmpleadoUseCase)
        viewModel.onEvent(ListEmpleadoUiEvent.CreateNew)
        viewModel.onEvent(ListEmpleadoUiEvent.ShowMessage("Mensaje temporal"))

        // When
        viewModel.onNavigationHandled()

        // Then
        assertFalse(viewModel.state.value.navigateToCreate)
        assertNull(viewModel.state.value.navigateToEditId)
        assertNull(viewModel.state.value.message)
    }
}

// Regla para el Dispatcher Main en tests
@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}