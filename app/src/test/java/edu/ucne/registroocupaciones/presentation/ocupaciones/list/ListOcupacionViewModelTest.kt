package edu.ucne.registroocupaciones.presentation.ocupaciones.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import edu.ucne.registroocupaciones.domain.ocupaciones.model.Ocupacion
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.DeleteOcupacionUseCase
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.ObserveOcupacionUseCase
import edu.ucne.registroocupaciones.presentation.empleados.list.MainDispatcherRule
import edu.ucne.registroocupaciones.presentation.ocupacion.list.ListOcupacionUiEvent
import edu.ucne.registroocupaciones.presentation.ocupacion.list.ListOcupacionViewModel
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ListOcupacionViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ListOcupacionViewModel
    private lateinit var observeOcupacionUseCase: ObserveOcupacionUseCase
    private lateinit var deleteOcupacionUseCase: DeleteOcupacionUseCase

    @Before
    fun setup() {
        observeOcupacionUseCase = mockk()
        deleteOcupacionUseCase = mockk()
        every { observeOcupacionUseCase() } returns flowOf(emptyList())
    }

    @Test
    fun `init o Load carga la lista de ocupaciones exitosamente`() = runTest {
        // Given
        val listaPrueba = listOf(
            Ocupacion(1, "Ingeniero de Software", 85000.0),
            Ocupacion(2, "Diseñador UX", 65000.0)
        )
        every { observeOcupacionUseCase() } returns flowOf(listaPrueba)

        // When
        viewModel = ListOcupacionViewModel(observeOcupacionUseCase, deleteOcupacionUseCase)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(2, viewModel.state.value.ocupaciones.size)
        assertEquals("Ingeniero de Software", viewModel.state.value.ocupaciones[0].descripcion)
    }

    @Test
    fun `onEvent Delete elimina ocupacion de forma exitosa`() = runTest {
        // Given
        val idEliminar = 15
        coEvery { deleteOcupacionUseCase(idEliminar) } just Runs
        viewModel = ListOcupacionViewModel(observeOcupacionUseCase, deleteOcupacionUseCase)

        // When
        viewModel.onEvent(ListOcupacionUiEvent.Delete(idEliminar))
        advanceUntilIdle()

        // Then
        coVerify { deleteOcupacionUseCase(idEliminar) }
        assertEquals("Estudiante eliminado", viewModel.state.value.message)
    }

    @Test
    fun `onEvent Delete captura errores correctamente`() = runTest {
        // Given
        val idEliminar = 20
        coEvery { deleteOcupacionUseCase(idEliminar) } throws Exception("Error de servidor")
        viewModel = ListOcupacionViewModel(observeOcupacionUseCase, deleteOcupacionUseCase)

        // When
        viewModel.onEvent(ListOcupacionUiEvent.Delete(idEliminar))
        advanceUntilIdle()

        // Then
        coVerify { deleteOcupacionUseCase(idEliminar) }
        assertEquals("Error al eliminar: Error de servidor", viewModel.state.value.message)
    }

    @Test
    fun `onEvent CreateNew altera bandera de navegacion correctamente`() = runTest {
        // Given
        viewModel = ListOcupacionViewModel(observeOcupacionUseCase, deleteOcupacionUseCase)

        // When
        viewModel.onEvent(ListOcupacionUiEvent.CreateNew)

        // Then
        assertTrue(viewModel.state.value.navigateToCreate)
    }

    @Test
    fun `onEvent Edit establece el id de destino esperado`() = runTest {
        // Given
        viewModel = ListOcupacionViewModel(observeOcupacionUseCase, deleteOcupacionUseCase)

        // When
        viewModel.onEvent(ListOcupacionUiEvent.Edit(7))

        // Then
        assertEquals(7, viewModel.state.value.navigateToEditId)
    }

    @Test
    fun `onNavigationHandled limpia todas las variables de navegacion y estados temporales`() = runTest {
        // Given
        viewModel = ListOcupacionViewModel(observeOcupacionUseCase, deleteOcupacionUseCase)
        viewModel.onEvent(ListOcupacionUiEvent.CreateNew)
        viewModel.onEvent(ListOcupacionUiEvent.ShowMessage("Alerta"))

        // When
        viewModel.onNavigationHandled()

        // Then
        assertFalse(viewModel.state.value.navigateToCreate)
        assertNull(viewModel.state.value.navigateToEditId)
        assertNull(viewModel.state.value.message)
    }
}