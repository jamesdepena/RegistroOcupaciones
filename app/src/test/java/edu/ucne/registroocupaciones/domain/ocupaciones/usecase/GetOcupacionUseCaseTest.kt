package edu.ucne.registroocupaciones.domain.ocupaciones.usecase

import edu.ucne.registroocupaciones.domain.ocupaciones.model.Ocupacion
import edu.ucne.registroocupaciones.domain.ocupaciones.repository.OcupacionRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetOcupacionUseCaseTest {

    private lateinit var useCase: GetOcupacionUseCase
    private lateinit var repository: OcupacionRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetOcupacionUseCase(repository)
    }

    @Test
    fun `invoke con id valido retorna la ocupacion`() = runTest {
        // Given
        val idValido = 5
        val ocupacion = Ocupacion(idValido, "Contador", 45000.0)
        coEvery { repository.getOcupacion(idValido) } returns ocupacion

        // When
        val result = useCase(idValido)

        // Then
        assertNotNull(result)
        assertEquals("Contador", result?.descripcion)
        coVerify { repository.getOcupacion(idValido) }
    }

    @Test
    fun `invoke con id invalido arroja IllegalArgumentException`() = runTest {
        // When & Then
        try {
            useCase(-5)
            fail("Debería haber lanzado IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("El ID debe ser mayor que 0.", e.message)
        }
        coVerify(exactly = 0) { repository.getOcupacion(any()) }
    }
}