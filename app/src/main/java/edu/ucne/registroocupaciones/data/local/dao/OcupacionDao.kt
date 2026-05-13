package edu.ucne.registroocupaciones.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import edu.ucne.registroocupaciones.data.local.entities.OcupacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OcupacionDao {
    @Query("SELECT * FROM ocupaciones ORDER BY ocupacionId DESC")
    fun observeAll(): Flow<List<OcupacionEntity>>

    @Query("SELECT * FROM ocupaciones where ocupacionId = :id")
    suspend fun getById(id: Int): OcupacionEntity?

    @Upsert
    suspend fun upsert(entity: OcupacionEntity): Long

    @Query("DELETE FROM ocupaciones WHERE ocupacionId = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM ocupaciones WHERE descripcion = :descripcion")
    suspend fun getByDescripcion(descripcion: String): List<OcupacionEntity>
}