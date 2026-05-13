package edu.ucne.registroocupaciones.presentation.ocupacion.edit

sealed interface EditOcupacionUiEvent {
    data class Load(val id: Int?) : EditOcupacionUiEvent
    data class DescripcionChanged(val value: String) : EditOcupacionUiEvent
    data class SueldoChanged(val value: String) : EditOcupacionUiEvent
    data object Save : EditOcupacionUiEvent
    data object Delete : EditOcupacionUiEvent
}