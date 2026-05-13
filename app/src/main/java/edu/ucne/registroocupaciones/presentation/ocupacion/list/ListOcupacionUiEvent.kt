package edu.ucne.registroocupaciones.presentation.ocupacion.list

sealed interface ListOcupacionUiEvent {
    data object Load : ListOcupacionUiEvent
    data class Delete(val id: Int) : ListOcupacionUiEvent
    data object CreateNew : ListOcupacionUiEvent
    data class Edit(val id: Int) : ListOcupacionUiEvent
    data class ShowMessage(val message: String) : ListOcupacionUiEvent
}