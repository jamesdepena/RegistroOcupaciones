package edu.ucne.registroocupaciones.presentation.empleado.edit

import java.time.LocalDate

data class EditEmpleadoUiState(
    val empleadoId: Int = 0,
    val fechaIngreso: LocalDate? = null,
    val fechaIngresoTexto: String = "",
    val nombres: String = "",
    val sexo: String = "",
    val sueldo: String = "",
    val fechaIngresoError: String? = null,
    val nombresError: String? = null,
    val sexoError: String? = null,
    val sueldoError: String? = null,
    val isNew: Boolean = true,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val saved: Boolean = false,
    val deleted: Boolean = false
)