package edu.ucne.registroocupaciones.domain.empleados.usecase

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import jakarta.inject.Inject

class UpsertEmpleadoUseCase @Inject constructor(
    private val repository: EmpleadoRepository,
    private val validateEmpleadoUseCase: ValidateEmpleadoUseCase
) {
    suspend operator fun invoke(empleado: Empleado): Result<Unit> {
        return try {

            val validation = validateEmpleadoUseCase(
                fechaIngreso = empleado.fechaIngreso,
                nombres = empleado.nombres,
                sexo = empleado.sexo,
                sueldo = empleado.sueldo.toString(),
                currentEmpleadoId = empleado.empleadoId
            )

            if (!validation.isValid) {
                val errorMsg = validation.fechaError ?: validation.nombresError ?: validation.sueldoError ?: "Error de validación"
                Result.failure(IllegalArgumentException(errorMsg))
            } else {
                repository.upsert(empleado)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}