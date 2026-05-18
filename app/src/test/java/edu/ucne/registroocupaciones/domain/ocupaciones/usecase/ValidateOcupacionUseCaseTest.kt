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
class ValidateOcupacionUseCaseTest {

    private lateinit var useCase: ValidateOcupacionUseCase
    private lateinit var repository: OcupacionRepository

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        useCase = ValidateOcupacionUseCase(repository)
    }

    @Test
    fun `retorna valido si la descripcion y sueldo cumplen los requisitos`() = runTest {
        // Given
        coEvery { repository.getOcupacionByDescripcion("Ingeniero") } returns emptyList()

        // When
        val result = useCase(descripcion = "Ingeniero", sueldo = 50000.0)

        // Then
        assertTrue(result.isValid)
        assertNull(result.descripcionError)
        assertNull(result.sueldoError)
    }

    @Test
    fun `detecta error si la descripcion esta vacia`() = runTest {
        val result = useCase(descripcion = "    ", sueldo = 25000.0)
        assertEquals("La descripción no puede estar vacía", result.descripcionError)
        assertFalse(result.isValid)
    }

    @Test
    fun `detecta error si la descripcion tiene menos de tres caracteres`() = runTest {
        val result = useCase(descripcion = "IT", sueldo = 25000.0)
        assertEquals("La descripción debe tener al menos tres caracteres.", result.descripcionError)
        assertFalse(result.isValid)
    }

    @Test
    fun `detecta error si la descripcion ya existe en otra ocupacion`() = runTest {
        // Given
        val descripcionRepetida = "Chef"
        val otraOcupacion = listOf(Ocupacion(ocupacionId = 8, descripcion = descripcionRepetida, sueldo = 40000.0))
        coEvery { repository.getOcupacionByDescripcion(descripcionRepetida) } returns otraOcupacion

        // When
        val result = useCase(descripcion = descripcionRepetida, sueldo = 40000.0, currentOcupacionId = 1)

        // Then
        assertEquals("Ya hay una ocupación con esta misma descripción.", result.descripcionError)
        assertFalse(result.isValid)
    }

    @Test
    fun `permite la misma descripcion si pertenece al id de la ocupacion actual en edicion`() = runTest {
        // Given
        val descripcionPropia = "Chef"
        val ocupacionActual = listOf(Ocupacion(ocupacionId = 5, descripcion = descripcionPropia, sueldo = 40000.0))
        coEvery { repository.getOcupacionByDescripcion(descripcionPropia) } returns ocupacionActual

        // When
        val result = useCase(descripcion = descripcionPropia, sueldo = 40000.0, currentOcupacionId = 5)

        // Then
        assertNull(result.descripcionError)
        assertTrue(result.isValid)
    }

    @Test
    fun `detecta error si el sueldo es nulo`() = runTest {
        val result = useCase(descripcion = "Maestro", sueldo = null)
        assertEquals("El sueldo no puede estar vacío.", result.sueldoError)
        assertFalse(result.isValid)
    }

    @Test
    fun `detecta error si el sueldo es menor o igual a cero`() = runTest {
        val result = useCase(descripcion = "Maestro", sueldo = 0.0)
        assertEquals("El sueldo debe ser mayor que 0.0", result.sueldoError)
        assertFalse(result.isValid)
    }
}