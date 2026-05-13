package edu.ucne.registroocupaciones.domain.usecases

import edu.ucne.registroocupaciones.domain.model.Ocupacion
import edu.ucne.registroocupaciones.domain.repository.OcupacionRepository
import jakarta.inject.Inject

class GetOcupacionUseCase @Inject constructor(
    private val repository: OcupacionRepository
) {
    suspend operator fun invoke(id: Int): Ocupacion? {
        if (id <= 0) throw IllegalArgumentException("El ID debe ser mayor que 0.")
        return repository.getOcupacion(id)
    }
}