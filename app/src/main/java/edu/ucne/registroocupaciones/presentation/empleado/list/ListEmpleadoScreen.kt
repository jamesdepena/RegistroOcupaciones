package edu.ucne.registroocupaciones.presentation.empleado.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import java.time.format.DateTimeFormatter

@Composable
fun EmpleadoListScreen(
    goToEmpleado: (Int) -> Unit,
    createEmpleado: () -> Unit,
    viewModel: ListEmpleadoViewModel = hiltViewModel(),
    onDrawer: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    EmpleadoListBody(
        state = state,
        onDrawer = onDrawer,
        onEvent = { event ->
            when (event) {
                is ListEmpleadoUiEvent.Edit -> goToEmpleado(event.id)
                is ListEmpleadoUiEvent.CreateNew -> createEmpleado()
                else -> viewModel.onEvent(event)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoListBody(
    state: ListEmpleadoUiState,
    onEvent: (ListEmpleadoUiEvent) -> Unit,
    onDrawer: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Listado de Empleados") },
                navigationIcon = {
                    IconButton(onClick = onDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        }, floatingActionButton = {
            FloatingActionButton(onClick = { onEvent(ListEmpleadoUiEvent.CreateNew) }) {
                Text("+")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()

        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
               items(state.empleados) { empleado ->
                   EmpleadoCard(
                       empleado = empleado,
                       onClick = { onEvent(ListEmpleadoUiEvent.Edit(empleado.empleadoId)) },
                       onDelete = { onEvent(ListEmpleadoUiEvent.Delete(empleado.empleadoId)) }
                   )
               }
            }
        }
    }
}

@Composable
private fun EmpleadoCard(
    empleado: Empleado,
    onClick: () -> Unit,
    onDelete: (Int) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                val fechaFormato = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val fechaFormateada = empleado.fechaIngreso.format(fechaFormato)

                Text(empleado.nombres, style = MaterialTheme.typography.titleMedium)
                Text("Ingresó el: ${fechaFormateada}", style = MaterialTheme.typography.bodyMedium)
                Text("Sexo: ${empleado.sexo}", style = MaterialTheme.typography.bodyMedium)
                Text("Sueldo: ${empleado.sueldo}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onClick) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = { onDelete(empleado.empleadoId) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}