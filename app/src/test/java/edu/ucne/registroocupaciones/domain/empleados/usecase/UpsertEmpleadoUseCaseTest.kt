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
class UpsertEmpleadoUseCaseTest {
    private lateinit var useCase: UpsertEmpleadoUseCase
    private lateinit var repository: EmpleadoRepository
    private lateinit var validateEmpleadoUseCase: ValidateEmpleadoUseCase

    @Before
    fun setup() {
        repository = mockk()
        validateEmpleadoUseCase = mockk()
        useCase = UpsertEmpleadoUseCase(repository, validateEmpleadoUseCase)
    }

    @Test
    fun `invoke guarda empleado con datos validos`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 1,
            nombres = "Juan Perez",
            fechaIngreso = LocalDate.now(),
            sexo = "Masculino",
            sueldo = 25000.0
        )

        coEvery {
            validateEmpleadoUseCase.invoke(any(), any(), any(), any(), any())
        } returns ValidateEmpleadoUseCase.ValidationResult(isValid = true)

        coEvery { repository.upsert(empleado) } returns Unit

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isSuccess)
        coVerify { repository.upsert(empleado) }
    }

    @Test
    fun `invoke falla con nombres vacios`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 1,
            nombres = "   ",
            fechaIngreso = LocalDate.now(),
            sexo = "Masculino",
            sueldo = 25000.0
        )

        coEvery {
            validateEmpleadoUseCase.invoke(any(), any(), any(), any(), any())
        } returns ValidateEmpleadoUseCase.ValidationResult(
            isValid = false,
            nombresError = "El nombre no puede estar vacío."
        )

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("El nombre no puede estar vacío.", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla con sueldo negativo o cero`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 1,
            nombres = "Carlos Gomez",
            fechaIngreso = LocalDate.now(),
            sexo = "Masculino",
            sueldo = -100.0
        )

        coEvery {
            validateEmpleadoUseCase.invoke(any(), any(), any(), any(), any())
        } returns ValidateEmpleadoUseCase.ValidationResult(
            isValid = false,
            sueldoError = "El sueldo debe ser mayor que 0.0."
        )

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("El sueldo debe ser mayor que 0.0.", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke falla con fecha invalida futura`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 1,
            nombres = "Ana Lopez",
            fechaIngreso = LocalDate.now().plusDays(5),
            sexo = "Femenino",
            sueldo = 30000.0
        )

        coEvery {
            validateEmpleadoUseCase.invoke(any(), any(), any(), any(), any())
        } returns ValidateEmpleadoUseCase.ValidationResult(
            isValid = false,
            fechaError = "La fecha no puede ser superior a la actual."
        )

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("La fecha no puede ser superior a la actual.", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }
}