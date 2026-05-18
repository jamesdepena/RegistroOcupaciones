package edu.ucne.registroocupaciones.domain.empleados.usecase

import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import jakarta.inject.Inject
import java.time.LocalDate

class ValidateEmpleadoUseCase @Inject constructor(
    private val repository: EmpleadoRepository
) {
    data class ValidationResult(
        val isValid: Boolean,
        val fechaError: String? = null,
        val nombresError: String? = null,
        val sexoError: String? = null,
        val sueldoError: String? = null
    )

    suspend operator fun invoke(
        fechaIngreso: LocalDate?,
        nombres: String,
        sexo: String,
        sueldo: String,
        currentEmpleadoId: Int
    ): ValidationResult {

        val fechaError = when {
            fechaIngreso == null -> "Debe seleccionar una fecha."
            fechaIngreso.isAfter(LocalDate.now()) -> "La fecha no puede ser superior a la actual."
            else -> null
        }

        val nombresError = when {
            nombres.isBlank() -> "El nombre no puede estar vacío."
            !nombres.matches(Regex("^[\\p{L} ]+$")) -> "El nombre solo puede contener letras y espacios."
            else -> {
                val existingEmpleado = repository.getEmpleadoByNombre(nombres.trim())
                if (existingEmpleado != null && existingEmpleado.empleadoId != currentEmpleadoId) {
                    "Ya existe un empleado con este nombre."
                } else null
            }
        }

        val sexoError = when {
            sexo.isBlank() -> "Debe seleccioar el sexo."
            sexo.lowercase() != "masculino" && sexo.lowercase() != "femenino" -> "El sexo debe ser masculino o femenino."
            else -> null
        }

        val sueldoError = when {
            sueldo.isBlank() -> "El sueldo no puede estar vacío."
            sueldo.toDoubleOrNull() == null -> "El sueldo debe tener un formato correcto. Ej: 5850.5"
            (sueldo.toDoubleOrNull() ?: 0.0) <= 0.0 -> "El sueldo debe ser mayor que 0.0."
            else -> null
        }

        return ValidationResult(
            isValid = fechaError == null && nombresError == null && sexoError == null && sueldoError == null,
            fechaError = fechaError,
            nombresError = nombresError,
            sexoError = sexoError,
            sueldoError = sueldoError
        )
    }
}