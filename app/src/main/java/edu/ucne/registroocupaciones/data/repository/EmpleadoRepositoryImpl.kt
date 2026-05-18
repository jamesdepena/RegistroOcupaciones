package edu.ucne.registroocupaciones.data.repository

import edu.ucne.registroocupaciones.data.local.dao.EmpleadoDao
import edu.ucne.registroocupaciones.data.mapper.toDomain
import edu.ucne.registroocupaciones.data.mapper.toEntity
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EmpleadoRepositoryImpl @Inject constructor(
    private val empleadoDao: EmpleadoDao
) : EmpleadoRepository {

    override fun observeEmpleados(): Flow<List<Empleado>> {
        return empleadoDao.observeAll().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getEmpleado(id: Int): Empleado? {
        return empleadoDao.getById(id)?.toDomain()
    }

    override suspend fun upsert(empleado: Empleado) {
        val entity = empleado.toEntity()
        empleadoDao.upsert(entity)
    }

    override suspend fun delete(id: Int) {
        empleadoDao.deleteById(id)
    }

    override suspend fun getEmpleadoByNombre(nombre: String): Empleado? {
        return empleadoDao.getByNombre(nombre.trim())?.toDomain()
    }
}