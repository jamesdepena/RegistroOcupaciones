package edu.ucne.registroocupaciones.presentation.ocupacion.list

import edu.ucne.registroocupaciones.domain.model.Ocupacion

data class ListOcupacionUiState(
    val isLoading: Boolean = false,
    val ocupaciones: List<Ocupacion> = emptyList(),
    val message: String? = null,
    val navigateToCreate: Boolean = false,
    val navigateToEditId: Int? = null
)