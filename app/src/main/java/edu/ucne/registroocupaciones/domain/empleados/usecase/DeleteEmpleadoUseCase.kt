package edu.ucne.registroocupaciones.domain.empleados.usecase

import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import jakarta.inject.Inject

class DeleteEmpleadoUseCase @Inject constructor(
    private val repository: EmpleadoRepository
) {
    suspend operator fun invoke(id: Int) {
        if (id <= 0) throw IllegalArgumentException("El ID debe ser mayor que 0.")
        repository.delete(id)
    }
}