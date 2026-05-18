package edu.ucne.registroocupaciones.data.empleados.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import edu.ucne.registroocupaciones.data.local.dao.EmpleadoDao
import edu.ucne.registroocupaciones.data.local.entities.EmpleadoEntity
import edu.ucne.registroocupaciones.data.repository.EmpleadoRepositoryImpl
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@ExperimentalCoroutinesApi
class EmpleadoRepositoryImplTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: EmpleadoRepositoryImpl
    private lateinit var dao: EmpleadoDao

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = EmpleadoRepositoryImpl(dao)
    }

    @Test
    fun `save guarda empleado nuevo correctamente`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            nombres = "Juan Perez",
            fechaIngreso = LocalDate.of(2026, 5, 1),
            sexo = "Masculino",
            sueldo = 45000.0
        )
        val empleadoSlot = slot<EmpleadoEntity>()
        coEvery { dao.upsert(capture(empleadoSlot)) } just Runs

        // When
        repository.upsert(empleado)

        // Then
        coVerify { dao.upsert(any()) }
        assertEquals(empleado.nombres, empleadoSlot.captured.nombres)
        assertEquals(empleado.fechaIngreso, empleadoSlot.captured.fechaIngreso)
        assertEquals(empleado.sexo, empleadoSlot.captured.sexo)
        assertEquals(empleado.sueldo, empleadoSlot.captured.sueldo, 0.0)
    }

    @Test
    fun `save actualiza empleado existente correctamente`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 5,
            nombres = "Maria Lopez",
            fechaIngreso = LocalDate.now(),
            sexo = "Femenino",
            sueldo = 60000.0
        )
        coEvery { dao.upsert(any()) } just Runs

        // When
        repository.upsert(empleado)

        // Then
        coVerify { dao.upsert(any()) }
    }

    @Test
    fun `delete elimina empleado correctamente`() = runTest {
        // Given
        val empleadoId = 10
        coEvery { dao.deleteById(empleadoId) } just Runs

        // When
        repository.delete(empleadoId)

        // Then
        coVerify { dao.deleteById(empleadoId) }
    }

    @Test
    fun `observeEmpleados retorna flow de lista de empleados`() = runTest {
        // Given
        val entities = listOf(
            EmpleadoEntity(1, LocalDate.of(2025, 1, 1), "Pedro", "Masculino", 30000.0),
            EmpleadoEntity(2, LocalDate.of(2026, 2, 1), "Ana", "Femenino", 40000.0)
        )
        every { dao.observeAll() } returns flowOf(entities)

        // When
        val result = repository.observeEmpleados().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Pedro", result[0].nombres)
        assertEquals("Ana", result[1].nombres)
        assertEquals(30000.0, result[0].sueldo, 0.0)
    }

    @Test
    fun `getEmpleado retorna empleado por id`() = runTest {
        // Given
        val fecha = LocalDate.of(2026, 3, 15)
        val entity = EmpleadoEntity(12, fecha, "Carlos Gomez", "Masculino", 55000.0)
        coEvery { dao.getById(12) } returns entity

        // When
        val result = repository.getEmpleado(12)

        // Then
        assertNotNull(result)
        assertEquals("Carlos Gomez", result?.nombres)
        assertEquals(fecha, result?.fechaIngreso)
        assertEquals("Masculino", result?.sexo)
        assertEquals(55000.0, result?.sueldo ?: 0.0, 0.0)
    }
}