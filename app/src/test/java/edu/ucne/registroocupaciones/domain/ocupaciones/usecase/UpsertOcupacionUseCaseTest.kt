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
class UpsertOcupacionUseCaseTest {

    private lateinit var useCase: UpsertOcupacionUseCase
    private lateinit var repository: OcupacionRepository
    private lateinit var validateOcupacionUseCase: ValidateOcupacionUseCase

    @Before
    fun setup() {
        repository = mockk()
        validateOcupacionUseCase = mockk()
        useCase = UpsertOcupacionUseCase(repository, validateOcupacionUseCase)
    }

    @Test
    fun `invoke guarda ocupacion con datos validos y retorna id`() = runTest {
        // Given
        val ocupacion = Ocupacion(ocupacionId = 0, descripcion = "Doctor", sueldo = 90000.0)

        coEvery {
            validateOcupacionUseCase(any(), any(), any())
        } returns ValidateOcupacionUseCase.ValidationResult(isValid = true)

        coEvery { repository.upsert(ocupacion) } returns 50

        // When
        val result = useCase(ocupacion)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(50, result.getOrNull())
        coVerify { repository.upsert(ocupacion) }
    }

    @Test
    fun `invoke retorna failure si falla la validacion`() = runTest {
        // Given
        val ocupacionInvalida = Ocupacion(0, "", 100.0)

        coEvery {
            validateOcupacionUseCase(any(), any(), any())
        } returns ValidateOcupacionUseCase.ValidationResult(
            isValid = false,
            descripcionError = "La descripción no puede estar vacía"
        )

        // When
        val result = useCase(ocupacionInvalida)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertEquals("La descripción no puede estar vacía", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { repository.upsert(any()) }
    }

    @Test
    fun `invoke captura cualquier excepcion del repositorio en un Result failure`() = runTest {
        // Given
        val ocupacion = Ocupacion(1, "Abogado", 60000.0)

        coEvery {
            validateOcupacionUseCase(any(), any(), any())
        } returns ValidateOcupacionUseCase.ValidationResult(isValid = true)

        coEvery { repository.upsert(ocupacion) } throws RuntimeException("Error en Base de datos")

        // When
        val result = useCase(ocupacion)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Error en Base de datos", result.exceptionOrNull()?.message)
    }
}