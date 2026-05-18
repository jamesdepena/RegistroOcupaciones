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
class ValidateEmpleadoUseCaseTest {

    private lateinit var useCase: ValidateEmpleadoUseCase
    private lateinit var repository: EmpleadoRepository

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        useCase = ValidateEmpleadoUseCase(repository)
    }

    @Test
    fun `retorna valido si todos los campos estan correctos y no existe duplicado`() = runTest {
        // Given
        coEvery { repository.getEmpleadoByNombre("Luis Perez") } returns null

        // When
        val result = useCase(
            fechaIngreso = LocalDate.now(),
            nombres = "Luis Perez",
            sexo = "Masculino",
            sueldo = "45000.0",
            currentEmpleadoId = 0
        )

        // Then
        assertTrue(result.isValid)
        assertNull(result.fechaError)
        assertNull(result.nombresError)
        assertNull(result.sexoError)
        assertNull(result.sueldoError)
    }

    @Test
    fun `detecta error si la fecha es nula o del futuro`() = runTest {
        val resultNull = useCase(null, "Luis", "Masculino", "100", 0)
        assertEquals("Debe seleccionar una fecha.", resultNull.fechaError)
        assertFalse(resultNull.isValid)

        val resultFuturo = useCase(LocalDate.now().plusDays(1), "Luis", "Masculino", "100", 0)
        assertEquals("La fecha no puede ser superior a la actual.", resultFuturo.fechaError)
    }

    @Test
    fun `detecta error si el nombre esta vacio o posee caracteres invalidos`() = runTest {
        val resultVacio = useCase(LocalDate.now(), "   ", "Masculino", "100", 0)
        assertEquals("El nombre no puede estar vacío.", resultVacio.nombresError)

        val resultInvalido = useCase(LocalDate.now(), "Juan123!", "Masculino", "100", 0)
        assertEquals("El nombre solo puede contener letras y espacios.", resultInvalido.nombresError)
    }

    @Test
    fun `detecta error si el nombre ya existe en otro empleado diferente`() = runTest {
        // Given
        val nombreRepetido = "Maria Lopez"
        val empleadoExistente = Empleado(
            empleadoId = 99,
            nombres = nombreRepetido,
            fechaIngreso = LocalDate.now(),
            sexo = "Femenino",
            sueldo = 50000.0
        )
        coEvery { repository.getEmpleadoByNombre(nombreRepetido) } returns empleadoExistente

        // When
        val result = useCase(LocalDate.now(), nombreRepetido, "Femenino", "50000.0", currentEmpleadoId = 1)

        // Then
        assertEquals("Ya existe un empleado con este nombre.", result.nombresError)
        assertFalse(result.isValid)
    }

    @Test
    fun `permite el mismo nombre si pertenece al mismo empleado que se esta editando`() = runTest {
        // Given
        val nombreRepetido = "Maria Lopez"
        val empleadoExistente = Empleado(
            empleadoId = 5,
            nombres = nombreRepetido,
            fechaIngreso = LocalDate.now(),
            sexo = "Femenino",
            sueldo = 50000.0
        )
        coEvery { repository.getEmpleadoByNombre(nombreRepetido) } returns empleadoExistente

        // When
        val result = useCase(LocalDate.now(), nombreRepetido, "Femenino", "50000.0", currentEmpleadoId = 5)

        // Then
        assertNull(result.nombresError)
    }

    @Test
    fun `detecta error en sexo invalido`() = runTest {
        val resultError = useCase(LocalDate.now(), "Luis", "Desconocido", "100", 0)
        assertEquals("El sexo debe ser masculino o femenino.", resultError.sexoError)
    }

    @Test
    fun `detecta errores en formatos de sueldo`() = runTest {
        val resultVacio = useCase(LocalDate.now(), "Luis", "Masculino", "", 0)
        assertEquals("El sueldo no puede estar vacío.", resultVacio.sueldoError)

        val resultFormato = useCase(LocalDate.now(), "Luis", "Masculino", "abc", 0)
        assertEquals("El sueldo debe tener un formato correcto. Ej: 5850.5", resultFormato.sueldoError)

        val resultCero = useCase(LocalDate.now(), "Luis", "Masculino", "0.0", 0)
        assertEquals("El sueldo debe ser mayor que 0.0.", resultCero.sueldoError)
    }
}