package edu.ucne.registroocupaciones.presentation.ocupaciones.edit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import edu.ucne.registroocupaciones.domain.ocupaciones.model.Ocupacion
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.DeleteOcupacionUseCase
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.GetOcupacionUseCase
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.UpsertOcupacionUseCase
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.ValidateOcupacionUseCase
import edu.ucne.registroocupaciones.presentation.empleados.list.MainDispatcherRule
import edu.ucne.registroocupaciones.presentation.ocupacion.edit.EditOcupacionUiEvent
import edu.ucne.registroocupaciones.presentation.ocupacion.edit.EditOcupacionViewModel
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class EditOcupacionViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: EditOcupacionViewModel
    private lateinit var getOcupacionUseCase: GetOcupacionUseCase
    private lateinit var deleteOcupacionUseCase: DeleteOcupacionUseCase
    private lateinit var upsertOcupacionUseCase: UpsertOcupacionUseCase
    private lateinit var validateOcupacionUseCase: ValidateOcupacionUseCase

    @Before
    fun setup() {
        getOcupacionUseCase = mockk()
        deleteOcupacionUseCase = mockk(relaxed = true)
        upsertOcupacionUseCase = mockk(relaxed = true)
        validateOcupacionUseCase = mockk(relaxed = true)

        coEvery {
            validateOcupacionUseCase(any(), any(), any())
        } returns ValidateOcupacionUseCase.ValidationResult(isValid = true)

        viewModel = EditOcupacionViewModel(
            getOcupacionUseCase,
            deleteOcupacionUseCase,
            upsertOcupacionUseCase,
            validateOcupacionUseCase
        )
    }

    @Test
    fun `onEvent Load con id invalido o nulo marca el estado como nuevo`() = runTest {
        // When
        viewModel.onEvent(EditOcupacionUiEvent.Load(0))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.isNew)
        assertNull(viewModel.state.value.ocupacionId)
    }

    @Test
    fun `onEvent Load con id valido actualiza el estado con los datos cargados`() = runTest {
        // Given
        val targetId = 3
        val ocupacionPrueba = Ocupacion(targetId, "Contador Público", 50000.0)
        coEvery { getOcupacionUseCase(targetId) } returns ocupacionPrueba

        // When
        viewModel.onEvent(EditOcupacionUiEvent.Load(targetId))
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isNew)
        assertEquals(targetId, viewModel.state.value.ocupacionId)
        assertEquals("Contador Público", viewModel.state.value.descripcion)
        assertEquals(50000.0, viewModel.state.value.sueldo)
    }

    @Test
    fun `cambios en los textos modifican el estado y disparan validaciones asincronas`() = runTest {
        // When
        viewModel.onEvent(EditOcupacionUiEvent.DescripcionChanged("Arquitecto"))
        advanceUntilIdle()

        // Then
        assertEquals("Arquitecto", viewModel.state.value.descripcion)
        coVerify { validateOcupacionUseCase("Arquitecto", any(), any()) }
    }

    @Test
    fun `onEvent Save con datos validos ejecuta upsert y actualiza estado`() = runTest {
        // Given
        viewModel.onEvent(EditOcupacionUiEvent.DescripcionChanged("Chef"))
        viewModel.onEvent(EditOcupacionUiEvent.SueldoChanged("40000.0"))
        advanceUntilIdle()

        // When
        viewModel.onEvent(EditOcupacionUiEvent.Save)
        advanceUntilIdle()

        // Then
        coVerify { upsertOcupacionUseCase(any()) }
        assertTrue(viewModel.state.value.saved)
        assertFalse(viewModel.state.value.isSaving)
    }

    @Test
    fun `onEvent Save con datos invalidos detiene el proceso y muestra los errores`() = runTest {
        // Given
        coEvery {
            validateOcupacionUseCase(any(), any(), any())
        } returns ValidateOcupacionUseCase.ValidationResult(
            isValid = false,
            descripcionError = "La descripción debe tener al menos tres caracteres."
        )

        // When
        viewModel.onEvent(EditOcupacionUiEvent.Save)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { upsertOcupacionUseCase(any()) }
        assertFalse(viewModel.state.value.saved)
        assertEquals("La descripción debe tener al menos tres caracteres.", viewModel.state.value.descripcionError)
    }

    @Test
    fun `onEvent Delete sobre ocupacion existente ejecuta la eliminacion con exito`() = runTest {
        // Given
        val targetId = 5
        val ocupacionPrueba = Ocupacion(targetId, "Mecánico", 35000.0)
        coEvery { getOcupacionUseCase(targetId) } returns ocupacionPrueba

        viewModel.onEvent(EditOcupacionUiEvent.Load(targetId))
        advanceUntilIdle()

        // When
        viewModel.onEvent(EditOcupacionUiEvent.Delete)
        advanceUntilIdle()

        // Then
        coVerify { deleteOcupacionUseCase(targetId) }
        assertTrue(viewModel.state.value.deleted)
        assertFalse(viewModel.state.value.isDeleting)
    }

    @Test
    fun `onEvent Delete no hace nada si ocupacionId es nulo`() = runTest {
        // Given
        viewModel.onEvent(EditOcupacionUiEvent.Load(null))
        advanceUntilIdle()

        // When
        viewModel.onEvent(EditOcupacionUiEvent.Delete)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { deleteOcupacionUseCase(any()) }
        assertFalse(viewModel.state.value.deleted)
    }
}