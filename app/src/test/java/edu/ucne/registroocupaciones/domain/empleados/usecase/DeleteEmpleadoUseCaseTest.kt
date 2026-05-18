package edu.ucne.registroocupaciones.domain.empleados.usecase

import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DeleteEmpleadoUseCaseTest {
    private lateinit var useCase: DeleteEmpleadoUseCase
    private lateinit var repository: EmpleadoRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = DeleteEmpleadoUseCase(repository)
    }

    @Test
    fun `invoke con id valido elimina correctamente`() = runTest {
        // Given
        val idValido = 5
        coEvery { repository.delete(idValido) } just Runs

        // When
        useCase(idValido)

        // Then
        coVerify { repository.delete(idValido) }
    }

    @Test
    fun `invoke con id invalido lanza excepcion`() = runTest {
        // When & Then
        try {
            useCase(-1)
            fail("Debería haber lanzado IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("El ID debe ser mayor que 0.", e.message)
        }

        coVerify(exactly = 0) { repository.delete(any()) }
    }
}