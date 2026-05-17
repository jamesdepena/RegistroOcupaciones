package edu.ucne.registroocupaciones.domain.empleados.repository

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import kotlinx.coroutines.flow.Flow

interface EmpleadoRepository {
    fun observeEmpleados(): Flow<List<Empleado>>
    suspend fun getEmpleado(id: Int): Empleado?
    suspend fun upsert(empleado: Empleado)
    suspend fun delete(id: Int)
    suspend fun getEmpleadoByNombre(nombre: String): Empleado?
}