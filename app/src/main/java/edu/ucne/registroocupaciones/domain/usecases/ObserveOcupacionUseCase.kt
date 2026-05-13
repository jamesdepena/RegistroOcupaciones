package edu.ucne.registroocupaciones.domain.usecases

import edu.ucne.registroocupaciones.domain.model.Ocupacion
import edu.ucne.registroocupaciones.domain.repository.OcupacionRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveOcupacionUseCase @Inject constructor(
    private val repository: OcupacionRepository
) {
    operator fun invoke(): Flow<List<Ocupacion>> {
        return repository.observeOcupaciones()
    }
}