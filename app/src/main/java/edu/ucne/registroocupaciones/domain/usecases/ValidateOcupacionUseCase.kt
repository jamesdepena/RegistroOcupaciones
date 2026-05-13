package edu.ucne.registroocupaciones.domain.usecases

import edu.ucne.registroocupaciones.domain.repository.OcupacionRepository
import jakarta.inject.Inject

class ValidateOcupacionUseCase @Inject constructor(
    private val repository: OcupacionRepository
) {
    data class ValidationResult(
        val isValid: Boolean = false,
        val descripcionError: String? = null,
        val sueldoError: String? = null
    )

    suspend operator fun invoke(
        descripcion: String,
        sueldo: Double?,
        currentOcupacionId: Int? = null
    ): ValidationResult {

        val descripcionError = when {
            descripcion.isBlank() -> "La descripción no puede estar vacía"
            descripcion.length < 3 -> "La descripción debe tener al menos tres caracteres."
            else -> {
                val existingOcupacion = repository.getOcupacionByDescripcion(descripcion)
                val isDuplicate = existingOcupacion.any() { it.ocupacionId != currentOcupacionId }
                if (isDuplicate) "Ya hay una ocupación con esta misma descripción." else null
            }
        }

        val sueldoError = when {
            sueldo == null -> "El sueldo no puede estar vacío."
            sueldo <= 0.0 -> "El sueldo debe ser mayor que 0.0"
            else -> null
        }

        return ValidationResult(
            isValid = descripcionError == null && sueldoError == null,
            descripcionError = descripcionError,
            sueldoError = sueldoError
        )
    }
}