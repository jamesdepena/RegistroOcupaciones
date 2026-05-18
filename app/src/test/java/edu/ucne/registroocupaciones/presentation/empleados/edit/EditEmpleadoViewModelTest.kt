package edu.ucne.registroocupaciones.presentation.empleados.edit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.usecase.DeleteEmpleadoUseCase
import edu.ucne.registroocupaciones.domain.empleados.usecase.GetEmpleadoUseCase
import edu.ucne.registroocupaciones.domain.empleados.usecase.UpsertEmpleadoUseCase
import edu.ucne.registroocupaciones.domain.empleados.usecase.ValidateEmpleadoUseCase
import edu.ucne.registroocupaciones.presentation.empleado.edit.EditEmpleadoUiEvent
import edu.ucne.registroocupaciones.presentation.empleado.edit.EditEmpleadoViewModel
import edu.ucne.registroocupaciones.presentation.empleados.list.MainDispatcherRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@ExperimentalCoroutinesApi
class EditEmpleadoViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: EditEmpleadoViewModel
    private lateinit var getEmpleadoUseCase: GetEmpleadoUseCase
    private lateinit var upsertEmpleadoUseCase: UpsertEmpleadoUseCase
    private lateinit var deleteEmpleadoUseCase: DeleteEmpleadoUseCase
    private lateinit var validateEmpleadoUseCase: ValidateEmpleadoUseCase

    @Before
    fun setup() {
        getEmpleadoUseCase = mockk()
        upsertEmpleadoUseCase = mockk(relaxed = true)
        deleteEmpleadoUseCase = mockk(relaxed = true)
        validateEmpleadoUseCase = mockk(relaxed = true)

        coEvery {
            validateEmpleadoUseCase.invoke(any(), any(), any(), any(), any())
        } returns ValidateEmpleadoUseCase.ValidationResult(isValid = true)

        viewModel = EditEmpleadoViewModel(
            getEmpleadoUseCase,
            upsertEmpleadoUseCase,
            deleteEmpleadoUseCase,
            validateEmpleadoUseCase
        )
    }

    @Test
    fun `onEvent Load con id nulo o cero define el estado como nuevo`() = runTest {
        // When
        viewModel.onEvent(EditEmpleadoUiEvent.Load(0))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.isNew)
        assertEquals(0, viewModel.state.value.empleadoId)
    }

    @Test
    fun `onEvent Load con id valido carga los datos del empleado en el estado`() = runTest {
        // Given
        val targetId = 1
        val fecha = LocalDate.of(2026, 5, 1)
        val empleadoPrueba = Empleado(targetId, fecha, "Juan Perez", "Masculino", 45000.0)
        coEvery { getEmpleadoUseCase(targetId) } returns empleadoPrueba

        // When
        viewModel.onEvent(EditEmpleadoUiEvent.Load(targetId))
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isNew)
        assertEquals(targetId, viewModel.state.value.empleadoId)
        assertEquals("Juan Perez", viewModel.state.value.nombres)
        assertEquals("45000.0", viewModel.state.value.sueldo)
        assertEquals("01/05/2026", viewModel.state.value.fechaIngresoTexto)
    }

    @Test
    fun `cambios en los inputs actualizan el estado y disparan la validacion individual`() = runTest {
        // When
        viewModel.onEvent(EditEmpleadoUiEvent.nombresChanged("Carlos Gomez"))
        advanceUntilIdle()

        // Then
        assertEquals("Carlos Gomez", viewModel.state.value.nombres)
        coVerify { validateEmpleadoUseCase(any(), "Carlos Gomez", any(), any(), any()) }
    }

    @Test
    fun `onEvent Save con datos validos ejecuta el upsert correctamente`() = runTest {
        // Given
        val fecha = LocalDate.of(2026, 5, 15)
        viewModel.onEvent(EditEmpleadoUiEvent.fechaIngresoChanged(fecha))
        viewModel.onEvent(EditEmpleadoUiEvent.nombresChanged("Maria Lopez"))
        viewModel.onEvent(EditEmpleadoUiEvent.sexoChanged("Femenino"))
        viewModel.onEvent(EditEmpleadoUiEvent.sueldoChanged("60000.0"))
        advanceUntilIdle()

        // When
        viewModel.onEvent(EditEmpleadoUiEvent.Save)
        advanceUntilIdle()

        // Then
        coVerify { upsertEmpleadoUseCase(any()) }
        assertTrue(viewModel.state.value.saved)
        assertFalse(viewModel.state.value.isSaving)
    }

    @Test
    fun `onEvent Save con datos invalidos no ejecuta upsert y carga errores en el estado`() = runTest {
        // Given
        coEvery {
            validateEmpleadoUseCase(any(), any(), any(), any(), any())
        } returns ValidateEmpleadoUseCase.ValidationResult(
            isValid = false,
            nombresError = "El nombre no puede estar vacío.",
            sueldoError = "El sueldo debe ser mayor que 0.0."
        )

        // When
        viewModel.onEvent(EditEmpleadoUiEvent.Save)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { upsertEmpleadoUseCase(any()) }
        assertFalse(viewModel.state.value.saved)
        assertEquals("El nombre no puede estar vacío.", viewModel.state.value.nombresError)
        assertEquals("El sueldo debe ser mayor que 0.0.", viewModel.state.value.sueldoError)
    }

    @Test
    fun `onEvent Delete ejecuta la eliminacion de un empleado existente con exito`() = runTest {
        // Given
        val targetId = 4
        val empleadoPrueba = Empleado(targetId, LocalDate.now(), "Pedro", "Masculino", 30000.0)
        coEvery { getEmpleadoUseCase(targetId) } returns empleadoPrueba

        viewModel.onEvent(EditEmpleadoUiEvent.Load(targetId))
        advanceUntilIdle()

        // When
        viewModel.onEvent(EditEmpleadoUiEvent.Delete)
        advanceUntilIdle()

        // Then
        coVerify { deleteEmpleadoUseCase(targetId) }
        assertTrue(viewModel.state.value.deleted)
        assertFalse(viewModel.state.value.isDeleting)
    }

    @Test
    fun `onEvent Delete no hace nada si el empleadoId es cero`() = runTest {
        // Given
        viewModel.onEvent(EditEmpleadoUiEvent.Load(0))
        advanceUntilIdle()

        // When
        viewModel.onEvent(EditEmpleadoUiEvent.Delete)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { deleteEmpleadoUseCase(any()) }
        assertFalse(viewModel.state.value.deleted)
    }
}