package edu.ucne.registroocupaciones.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.registroocupaciones.data.local.entities.EmpleadoEntity
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import kotlinx.coroutines.flow.Flow

@Dao
interface EmpleadoDao {

    @Query("SELECT * FROM empleados ORDER BY empleadoId DESC")
    fun observeAll(): Flow<List<EmpleadoEntity>>

    @Query("SELECT * FROM empleados WHERE empleadoId = :id")
    suspend fun getById(id: Int): EmpleadoEntity?

    @Upsert
    suspend fun upsert(empleadoEntity: EmpleadoEntity)

    @Query("DELETE FROM empleados WHERE empleadoId = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM empleados WHERE LOWER(nombres) = LOWER(:nombre)")
    suspend fun getByNombre(nombre: String): EmpleadoEntity?
}