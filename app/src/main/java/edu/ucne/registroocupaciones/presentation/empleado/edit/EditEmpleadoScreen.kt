package edu.ucne.registroocupaciones.presentation.empleado.edit

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEmpleadoScreen(
    empleadoId: Int?,
    viewModel: EditEmpleadoViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onDrawer: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(empleadoId) {
        viewModel.onEvent(EditEmpleadoUiEvent.Load(empleadoId))
    }


    if (state.saved || state.deleted) {
        SideEffect {
            onNavigateBack()
        }
    }

    EditEmpleadoBody(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        onDrawer = onDrawer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEmpleadoBody(
    state: EditEmpleadoUiState,
    onEvent: (EditEmpleadoUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onDrawer: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (state.isNew) "Crear Empleado" else "Editar Empleado") },
                navigationIcon = {
                    IconButton(onClick = onDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    )
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp)
        ) {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()

                    LaunchedEffect(isPressed) {
                        if (isPressed) {
                            showDatePicker = true
                        }
                    }

                    OutlinedTextField(
                        value = state.fechaIngresoTexto,
                        onValueChange = { },
                        label = { Text("Fecha de ingreso") },
                        readOnly = true,
                        interactionSource = interactionSource,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Seleccionar fecha"
                            )
                        },
                        isError = state.fechaIngresoError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.fechaIngresoError?.let { Text(text = it, color = Color.Red) }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.nombres,
                        onValueChange = { onEvent(EditEmpleadoUiEvent.nombresChanged(it)) },
                        label = { Text("Nombres") },
                        isError = state.nombresError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.nombresError?.let { Text(text = it, color = Color.Red) }

                    Spacer(Modifier.height(8.dp))

                    var expandedSexo by remember { mutableStateOf(false) }
                    val opcionesSexo = listOf("Masculino", "Femenino")

                    ExposedDropdownMenuBox(
                        expanded = expandedSexo,
                        onExpandedChange = { expandedSexo = !expandedSexo }
                    ) {
                        OutlinedTextField(
                            value = state.sexo,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Sexo") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSexo)
                            },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            isError = state.sexoError != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        )

                        ExposedDropdownMenu(
                            expanded = expandedSexo,
                            onDismissRequest = { expandedSexo = false }
                        ) {
                            opcionesSexo.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(text = opcion) },
                                    onClick = {
                                        onEvent(EditEmpleadoUiEvent.sexoChanged(opcion))
                                        expandedSexo = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                    state.sexoError?.let { Text(text = it, color = Color.Red) }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.sueldo,
                        onValueChange = { onEvent(EditEmpleadoUiEvent.sueldoChanged(it)) },
                        label = { Text("Sueldo") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = state.sueldoError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.sueldoError?.let { Text(text = it, color = Color.Red) }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = { onEvent(EditEmpleadoUiEvent.Save) },
                            enabled = !state.isSaving
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Guardar")
                            Text("Guardar")
                        }

                        if (!state.isNew) {
                            OutlinedButton(
                                onClick = { onEvent(EditEmpleadoUiEvent.Delete) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis
                    if (selectedDateMillis != null) {
                        val localDate = Instant.ofEpochMilli(selectedDateMillis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()

                        onEvent(EditEmpleadoUiEvent.fechaIngresoChanged(localDate))
                    }
                    showDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun EditEmpleadoPreview() {
    MaterialTheme {
        EditEmpleadoBody(
            state = EditEmpleadoUiState(nombres = "Juan"),
            onEvent = {},
            onNavigateBack = {},
            onDrawer = {}
        )
    }
}
