package edu.ucne.registroocupaciones.domain.empleados.usecase

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@ExperimentalCoroutinesApi
class GetEmpleadoUseCaseTest {

    private lateinit var useCase: GetEmpleadoUseCase
    private lateinit var repository: EmpleadoRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetEmpleadoUseCase(repository)
    }

    @Test
    fun `invoke con id valido retorna el empleado`() = runTest {
        // Given
        val idValido = 1
        val empleado = Empleado(idValido, LocalDate.now(), "Ana", "Femenino", 35000.0)
        coEvery { repository.getEmpleado(idValido) } returns empleado

        // When
        val result = useCase(idValido)

        // Then
        assertNotNull(result)
        assertEquals("Ana", result?.nombres)
        coVerify { repository.getEmpleado(idValido) }
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

        coVerify(exactly = 0) { repository.getEmpleado(any()) }
    }
}