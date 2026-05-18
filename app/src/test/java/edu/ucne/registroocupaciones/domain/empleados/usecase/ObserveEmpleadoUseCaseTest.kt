package edu.ucne.registroocupaciones.domain.empleados.usecase

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@ExperimentalCoroutinesApi
class ObserveEmpleadoUseCaseTest {

    private lateinit var useCase: ObserveEmpleadoUseCase
    private lateinit var repository: EmpleadoRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = ObserveEmpleadoUseCase(repository)
    }

    @Test
    fun `invoke retorna el flujo de datos del repositorio`() = runTest {
        // Given
        val empleados = listOf(
            Empleado(1,LocalDate.now(), "Pedro", "Masculino", 20000.0)
        )
        every { repository.observeEmpleados() } returns flowOf(empleados)

        // When
        val result = useCase().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Pedro", result[0].nombres)
        verify { repository.observeEmpleados() }
    }
}