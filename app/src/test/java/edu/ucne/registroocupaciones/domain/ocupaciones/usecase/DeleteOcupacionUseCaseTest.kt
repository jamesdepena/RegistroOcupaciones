package edu.ucne.registroocupaciones.domain.ocupaciones.usecase

import edu.ucne.registroocupaciones.domain.ocupaciones.repository.OcupacionRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DeleteOcupacionUseCaseTest {

    private lateinit var useCase: DeleteOcupacionUseCase
    private lateinit var repository: OcupacionRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = DeleteOcupacionUseCase(repository)
    }

    @Test
    fun `invoke con id valido elimina correctamente`() = runTest {
        // Given
        val idValido = 10
        coEvery { repository.delete(idValido) } just Runs

        // When
        useCase(idValido)

        // Then
        coVerify { repository.delete(idValido) }
    }

    @Test
    fun `invoke con id invalido arroja IllegalArgumentException`() = runTest {
        // When & Then
        try {
            useCase(0)
            fail("Debería haber lanzado IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("El ID debe ser mayor que 0.", e.message)
        }
        coVerify(exactly = 0) { repository.delete(any()) }
    }
}