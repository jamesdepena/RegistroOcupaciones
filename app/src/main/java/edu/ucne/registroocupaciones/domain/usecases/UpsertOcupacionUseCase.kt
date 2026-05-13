package edu.ucne.registroocupaciones.domain.usecases

import edu.ucne.registroocupaciones.domain.model.Ocupacion
import edu.ucne.registroocupaciones.domain.repository.OcupacionRepository
import jakarta.inject.Inject

class UpsertOcupacionUseCase @Inject constructor(
    private val repository: OcupacionRepository,
    private val validateOcupacionUseCase: ValidateOcupacionUseCase
) {

    suspend operator fun invoke(ocupacion: Ocupacion): Result<Int> {
        return try {
            val validation = validateOcupacionUseCase(
                descripcion = ocupacion.descripcion,
                sueldo = ocupacion.sueldo,
                currentOcupacionId = if (ocupacion.ocupacionId != 0) ocupacion.ocupacionId else null
            )

            if (!validation.isValid) {
                val errorMsg = validation.descripcionError ?: validation.sueldoError ?: "Error de validación"
                Result.failure(IllegalArgumentException(errorMsg))
            } else {
                val id = repository.upsert(ocupacion)
                Result.success(id)
            }

        } catch (e: Exception) {
            Result.failure(e)
        }

    }

}