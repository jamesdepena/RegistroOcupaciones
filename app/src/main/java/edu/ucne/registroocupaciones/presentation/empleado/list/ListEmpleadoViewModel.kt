package edu.ucne.registroocupaciones.presentation.empleado.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.registroocupaciones.domain.empleados.usecase.DeleteEmpleadoUseCase
import edu.ucne.registroocupaciones.domain.empleados.usecase.ObserveEmpleadoUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ListEmpleadoViewModel @Inject constructor(
    private val observeEmpleadoUseCase: ObserveEmpleadoUseCase,
    private val deleteEmpleadoUseCase: DeleteEmpleadoUseCase
): ViewModel() {

    private val _state = MutableStateFlow(ListEmpleadoUiState(isLoading = true))
    val state: StateFlow<ListEmpleadoUiState> = _state.asStateFlow()

    init {
        onEvent(ListEmpleadoUiEvent.Load)
    }

    fun onEvent(event: ListEmpleadoUiEvent) {
        when (event) {
            ListEmpleadoUiEvent.Load -> observeEmpleados()
            is ListEmpleadoUiEvent.Delete -> onDelete(event.id)
            is ListEmpleadoUiEvent.CreateNew -> _state.update { it.copy(navigateToCreate = true) }
            is ListEmpleadoUiEvent.Edit -> _state.update { it.copy(navigateToEditId = event.id) }
            is ListEmpleadoUiEvent.ShowMessage -> _state.update { it.copy(message = event.message) }
        }
    }

    private fun observeEmpleados() {
        viewModelScope.launch {
            observeEmpleadoUseCase().collectLatest { empleadosList ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        empleados = empleadosList,
                        message = null
                    )
                }
            }
        }
    }

    private fun onDelete(id: Int) {
        viewModelScope.launch {
            try {
                deleteEmpleadoUseCase(id)
                onEvent(ListEmpleadoUiEvent.ShowMessage("Estudiante eliminado"))
            } catch (e: Exception) {
                onEvent(ListEmpleadoUiEvent.ShowMessage("Error al eliminar: ${e.message}"))
            }
        }
    }

    fun onNavigationHandled() {
        _state.update {
            it.copy(
                navigateToCreate = false,
                navigateToEditId = null,
                message = null
            )
        }
    }
}