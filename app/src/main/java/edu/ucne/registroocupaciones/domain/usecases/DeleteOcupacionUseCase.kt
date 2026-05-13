package edu.ucne.registroocupaciones.domain.usecases

import edu.ucne.registroocupaciones.domain.repository.OcupacionRepository
import jakarta.inject.Inject

class DeleteOcupacionUseCase @Inject constructor(
    private val repository: OcupacionRepository
) {
    suspend operator fun invoke(id: Int) {
        if (id <= 0) throw IllegalArgumentException("El ID debe ser mayor que 0.")
        repository.delete(id)
    }
}