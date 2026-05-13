package edu.ucne.registroocupaciones.presentation.ocupacion.edit

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOcupacionScreen(
    ocupacionId: Int?,
    viewModel: EditOcupacionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,

    ) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(ocupacionId) {
        viewModel.onEvent(EditOcupacionUiEvent.Load(ocupacionId))
    }

    if (state.saved || state.deleted) {
        SideEffect {
            onNavigateBack()
        }
    }

    EditOcupacionBody(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditOcupacionBody(
    state: EditOcupacionUiState,
    onEvent: (EditOcupacionUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (state.isNew) "Nueva Ocupación" else "Editar Ocupación") },
                actions = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp)
        ) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    OutlinedTextField(
                        value = state.descripcion,
                        onValueChange = { onEvent(EditOcupacionUiEvent.DescripcionChanged(it)) },
                        label = { Text("Descripción") },
                        isError = state.descripcionError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.descripcionError?.let { Text(text = it, color = Color.Red) }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.sueldo?.toString() ?: "",
                        onValueChange = { onEvent(EditOcupacionUiEvent.SueldoChanged(it)) },
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
                            onClick = { onEvent(EditOcupacionUiEvent.Save) },
                            enabled = !state.isSaving
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Guardar")
                            Text("Guardar")
                        }

                        if (!state.isNew) {
                            OutlinedButton(
                                onClick = { onEvent(EditOcupacionUiEvent.Delete) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditOcupacionPreview() {
    MaterialTheme {
        EditOcupacionBody(
            state = EditOcupacionUiState(descripcion = "Ingeniero en Sístemas", sueldo = 642500.50),
            onEvent = {},
            onNavigateBack = {},
        )
    }
}