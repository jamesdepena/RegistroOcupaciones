package edu.ucne.registroocupaciones.presentation.ocupacion.list

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.registroocupaciones.domain.model.Ocupacion

@Composable
fun OcupacionListScreen(
    goToOcupacion: (Int) -> Unit,
    createOcupacion: () -> Unit,
    viewModel: ListOcupacionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    OcupacionListBody(
        state = state,
        onEvent = { event ->
            when (event) {
                is ListOcupacionUiEvent.Edit -> goToOcupacion(event.id)
                is ListOcupacionUiEvent.CreateNew -> createOcupacion()
                else -> viewModel.onEvent(event)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OcupacionListBody(
    state: ListOcupacionUiState,
    onEvent: (ListOcupacionUiEvent) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Listado de Ocupaciones") },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onEvent(ListOcupacionUiEvent.CreateNew) }) {
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
                items(state.ocupaciones) { ocupacion ->
                    OcupacionCard(
                        ocupacion = ocupacion,
                        onClick = { onEvent(ListOcupacionUiEvent.Edit(ocupacion.ocupacionId)) },
                        onDelete = { onEvent(ListOcupacionUiEvent.Delete(ocupacion.ocupacionId)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OcupacionCard(
    ocupacion: Ocupacion,
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
            Column(modifier = Modifier.weight(1f)) {
                Text(ocupacion.descripcion, style = MaterialTheme.typography.titleMedium)
                Text("Sueldo: ${ocupacion.sueldo}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(
                onClick = onClick

            ) { Icon(Icons.Default.Edit, contentDescription = "Editar") }
            IconButton(onClick = { onDelete(ocupacion.ocupacionId) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OcupacionListPreview() {
    MaterialTheme {
        OcupacionListBody(
            state = ListOcupacionUiState(
                ocupaciones = listOf(
                    Ocupacion(1, "Ingeniero en Sístemas", 642500.50),
                    Ocupacion(2, "Ingeniero Civil", 33000.22),
                )
            ),
            onEvent = {}
        )
    }
}