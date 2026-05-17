package edu.ucne.registroocupaciones.presentation.empleado.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.usecase.DeleteEmpleadoUseCase
import edu.ucne.registroocupaciones.domain.empleados.usecase.GetEmpleadoUseCase
import edu.ucne.registroocupaciones.domain.empleados.usecase.UpsertEmpleadoUseCase
import edu.ucne.registroocupaciones.domain.empleados.usecase.ValidateEmpleadoUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltViewModel
class EditEmpleadoViewModel @Inject constructor(
    private val getEmpleadoUseCase: GetEmpleadoUseCase,
    private val upsertEmpleadoUseCase: UpsertEmpleadoUseCase,
    private val deleteEmpleadoUseCase: DeleteEmpleadoUseCase,
    private val validateEmpleadoUseCase: ValidateEmpleadoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditEmpleadoUiState())
    val state: StateFlow<EditEmpleadoUiState> = _state.asStateFlow()

    private fun validateFecha(fechaIngreso: LocalDate?) {
        viewModelScope.launch {
            val result = validateEmpleadoUseCase(
                fechaIngreso = fechaIngreso,
                nombres = _state.value.nombres,
                sexo = _state.value.sexo,
                sueldo = _state.value.sueldo,
                currentEmpleadoId = _state.value.empleadoId
            )

            _state.update { it.copy(fechaIngresoError = result.fechaError) }
        }
    }

    private fun validateNombres(nombres: String) {
        viewModelScope.launch {
            val result = validateEmpleadoUseCase(
                fechaIngreso = _state.value.fechaIngreso,
                nombres = nombres,
                sexo = _state.value.sexo,
                sueldo = _state.value.sueldo,
                currentEmpleadoId = _state.value.empleadoId
            )

            _state.update { it.copy(nombresError = result.nombresError) }
        }
    }

    private fun validateSexo(sexo: String) {
        viewModelScope.launch {
            val result = validateEmpleadoUseCase(
                fechaIngreso = _state.value.fechaIngreso,
                nombres = _state.value.nombres,
                sexo = sexo,
                sueldo = _state.value.sueldo,
                currentEmpleadoId = _state.value.empleadoId
            )

            _state.update { it.copy(sexoError = result.sexoError) }
        }
    }

    private fun validateSueldo(sueldo: String) {
        viewModelScope.launch {
            val result = validateEmpleadoUseCase(
                fechaIngreso = _state.value.fechaIngreso,
                nombres = _state.value.nombres,
                sexo = _state.value.sexo,
                sueldo = sueldo,
                currentEmpleadoId = _state.value.empleadoId
            )

            _state.update { it.copy(sueldoError = result.sueldoError) }
        }
    }

    fun onEvent(event: EditEmpleadoUiEvent) {
        when (event) {
            is EditEmpleadoUiEvent.Load -> onLoad(event.id)
            is EditEmpleadoUiEvent.fechaIngresoChanged -> {
                val formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val fechaTexto = event.value?.format(formateador) ?: ""

                _state.update { it.copy(
                    fechaIngreso = event.value,
                    fechaIngresoTexto = fechaTexto,
                    fechaIngresoError = null)
                }
                validateFecha(event.value)
            }
            is EditEmpleadoUiEvent.nombresChanged -> {
                _state.update { it.copy(nombres = event.value, nombresError = null) }
                validateNombres(event.value)
            }
            is EditEmpleadoUiEvent.sexoChanged -> {
                _state.update { it.copy(sexo = event.value, sexoError = null) }
                validateSexo(event.value)
            }
            is EditEmpleadoUiEvent.sueldoChanged -> {
                _state.update { it.copy(sueldo = event.value) }
                validateSueldo(event.value)
            }
            is EditEmpleadoUiEvent.Save -> onSave()
            is EditEmpleadoUiEvent.Delete -> onDelete()
        }
    }

    private fun onLoad(id: Int?) {
        if (id == null || id == 0) {
            _state.update { it.copy(isNew = true, empleadoId = 0) }
            return
        }

        viewModelScope.launch {
            val empleado = getEmpleadoUseCase(id)
            if (empleado != null) {
                _state.update { it.copy(
                    empleadoId = empleado.empleadoId,
                    fechaIngreso = empleado.fechaIngreso,
                    fechaIngresoTexto = empleado.fechaIngreso.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    nombres = empleado.nombres,
                    sexo = empleado.sexo,
                    sueldo = empleado.sueldo.toString(),
                    isNew = false
                ) }
            } else {
                _state.update { it.copy(empleadoId = 0, isNew = true) }
            }
        }
    }

    private fun onSave() {
        viewModelScope.launch {
            val validation = validateEmpleadoUseCase(
                _state.value.fechaIngreso,
                _state.value.nombres,
                _state.value.sexo,
                _state.value.sueldo,
                _state.value.empleadoId
            )

            if (!validation.isValid) {
                _state.update {
                    it.copy(
                        fechaIngresoError = validation.fechaError,
                        nombresError = validation.nombresError,
                        sexoError = validation.sexoError,
                        sueldoError = validation.sueldoError
                    )
                }
                return@launch
            }

            _state.update { it.copy(isSaving = true) }

            val fechaGarantizada = _state.value.fechaIngreso ?: return@launch

            try {
                val empleado = Empleado(
                    empleadoId = _state.value.empleadoId,
                    fechaIngreso = fechaGarantizada,
                    nombres = _state.value.nombres,
                    sexo = _state.value.sexo,
                    sueldo = _state.value.sueldo.toDoubleOrNull() ?: 0.0
                )
                upsertEmpleadoUseCase(empleado)
                _state.update { it.copy(saved = true, isSaving = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, fechaIngresoError = e.message) }
            }
        }
    }

    private fun onDelete() {
        val id = _state.value.empleadoId

        if (id == 0) return

        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            try {
                deleteEmpleadoUseCase(id)
                _state.update { it.copy(isDeleting = false, deleted = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isDeleting = false, fechaIngresoError = e.message) }
            }
        }
    }
}