package edu.ucne.registroocupaciones.presentation.empleado.list

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado

data class ListEmpleadoUiState(
    val isLoading: Boolean = false,
    val empleados: List<Empleado> = emptyList(),
    val message: String? =  null,
    val navigateToCreate: Boolean = false,
    val navigateToEditId: Int? = null,
)