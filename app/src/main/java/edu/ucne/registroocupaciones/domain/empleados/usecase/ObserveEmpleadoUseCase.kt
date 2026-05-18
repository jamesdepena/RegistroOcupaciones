package edu.ucne.registroocupaciones.domain.empleados.usecase

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveEmpleadoUseCase @Inject constructor(
    private val repository: EmpleadoRepository
) {
    operator fun invoke(): Flow<List<Empleado>> {
        return repository.observeEmpleados()
    }
}