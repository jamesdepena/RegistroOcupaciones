package edu.ucne.registroocupaciones.domain.ocupaciones.usecase

import edu.ucne.registroocupaciones.domain.ocupaciones.model.Ocupacion
import edu.ucne.registroocupaciones.domain.ocupaciones.repository.OcupacionRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ObserveOcupacionUseCaseTest {

    private lateinit var useCase: ObserveOcupacionUseCase
    private lateinit var repository: OcupacionRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = ObserveOcupacionUseCase(repository)
    }

    @Test
    fun `invoke retorna la lista del repositorio como flujo`() = runTest {
        // Given
        val ocupaciones = listOf(
            Ocupacion(1, "Plomero", 30000.0),
            Ocupacion(2, "Electricista", 35000.0)
        )
        every { repository.observeOcupaciones() } returns flowOf(ocupaciones)

        // When
        val result = useCase().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Plomero", result[0].descripcion)
        verify { repository.observeOcupaciones() }
    }
}