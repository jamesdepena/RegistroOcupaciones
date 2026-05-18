package edu.ucne.registroocupaciones.data.ocupaciones.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import edu.ucne.registroocupaciones.data.local.dao.OcupacionDao
import edu.ucne.registroocupaciones.data.local.entities.OcupacionEntity
import edu.ucne.registroocupaciones.data.repository.OcupacionRepositoryImpl
import edu.ucne.registroocupaciones.domain.ocupaciones.model.Ocupacion
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class OcupacionRepositoryImplTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: OcupacionRepositoryImpl
    private lateinit var dao: OcupacionDao

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = OcupacionRepositoryImpl(dao)
    }

    @Test
    fun `upsert guarda ocupacion nueva y retorna el id generado`() = runTest {
        // Given
        val ocupacionNueva = Ocupacion(
            ocupacionId = 0,
            descripcion = "Ingeniero de Software",
            sueldo = 85000.0
        )
        val entitySlot = slot<OcupacionEntity>()
        coEvery { dao.upsert(capture(entitySlot)) } returns 105L

        // When
        val resultId = repository.upsert(ocupacionNueva)

        // Then
        coVerify { dao.upsert(any()) }
        assertEquals(105, resultId)
        assertEquals("Ingeniero de Software", entitySlot.captured.descripcion)
        assertEquals(85000.0, entitySlot.captured.sueldo, 0.0)
    }

    @Test
    fun `upsert actualiza ocupacion existente y retorna el mismo id`() = runTest {
        // Given
        val ocupacionExistente = Ocupacion(
            ocupacionId = 3,
            descripcion = "Contador",
            sueldo = 50000.0
        )
        coEvery { dao.upsert(any()) } returns 3L

        // When
        val resultId = repository.upsert(ocupacionExistente)

        // Then
        coVerify { dao.upsert(any()) }
        assertEquals(3, resultId)
    }

    @Test
    fun `delete elimina ocupacion correctamente`() = runTest {
        // Given
        val ocupacionId = 15
        coEvery { dao.deleteById(ocupacionId) } just Runs

        // When
        repository.delete(ocupacionId)

        // Then
        coVerify { dao.deleteById(ocupacionId) }
    }

    @Test
    fun `observeOcupaciones retorna flow de lista de ocupaciones transformadas`() = runTest {
        // Given
        val entities = listOf(
            OcupacionEntity(ocupacionId = 1, descripcion = "Diseñador UX", sueldo = 60000.0),
            OcupacionEntity(ocupacionId = 2, descripcion = "Administrador", sueldo = 45000.0)
        )
        every { dao.observeAll() } returns flowOf(entities)

        // When
        val result = repository.observeOcupaciones().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Diseñador UX", result[0].descripcion)
        assertEquals(60000.0, result[0].sueldo, 0.0)
        assertEquals("Administrador", result[1].descripcion)
        assertEquals(45000.0, result[1].sueldo, 0.0)
    }

    @Test
    fun `getOcupacion retorna la ocupacion por id mapeada a dominio`() = runTest {
        // Given
        val idBusqueda = 42
        val entity = OcupacionEntity(ocupacionId = idBusqueda, descripcion = "Analista de Datos", sueldo = 70000.0)
        coEvery { dao.getById(idBusqueda) } returns entity

        // When
        val result = repository.getOcupacion(idBusqueda)

        // Then
        assertNotNull(result)
        assertEquals(idBusqueda, result?.ocupacionId)
        assertEquals("Analista de Datos", result?.descripcion)
        assertEquals(70000.0, result?.sueldo ?: 0.0, 0.0)
    }

    @Test
    fun `getOcupacionByDescripcion retorna lista de ocupaciones que coinciden`() = runTest {
        // Given
        val busqueda = "Médico"
        val entities = listOf(
            OcupacionEntity(ocupacionId = 7, descripcion = "Médico General", sueldo = 90000.0),
            OcupacionEntity(ocupacionId = 8, descripcion = "Médico Especialista", sueldo = 120000.0)
        )
        coEvery { dao.getByDescripcion(busqueda) } returns entities

        // When
        val result = repository.getOcupacionByDescripcion(busqueda)

        // Then
        assertEquals(2, result.size)
        assertEquals("Médico General", result[0].descripcion)
        assertEquals(90000.0, result[0].sueldo, 0.0)
        assertEquals("Médico Especialista", result[1].descripcion)
        assertEquals(120000.0, result[1].sueldo, 0.0)
    }
}