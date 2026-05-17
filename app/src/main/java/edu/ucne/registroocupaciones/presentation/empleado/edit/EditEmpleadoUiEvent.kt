package edu.ucne.registroocupaciones.presentation.empleado.edit

import java.time.LocalDate

sealed interface EditEmpleadoUiEvent {
    data class Load(val id: Int?) : EditEmpleadoUiEvent
    data class fechaIngresoChanged(val value: LocalDate?): EditEmpleadoUiEvent
    data class nombresChanged(val value: String): EditEmpleadoUiEvent
    data class sexoChanged(val value: String): EditEmpleadoUiEvent
    data class sueldoChanged(val value: String): EditEmpleadoUiEvent
    data object Save : EditEmpleadoUiEvent
    data object Delete : EditEmpleadoUiEvent
}