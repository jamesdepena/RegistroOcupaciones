package edu.ucne.registroocupaciones.presentation.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun DrawerMenu(
    drawerState: DrawerState,
    navHostController: NavHostController,
    content: @Composable () -> Unit
) {
    val selectedItem = remember { mutableStateOf("Ocupaciones") }
    val scope = rememberCoroutineScope()

    fun handleItemClick(destination: Screen, item: String) {
        navHostController.navigate(destination) {
            launchSingleTop = true
        }
        selectedItem.value = item
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Registro",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding((16.dp))
                )

                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    item {
                        DrawerItem(
                            title = "Ocupaciones",
                            icon = Icons.Filled.People,
                            isSelected = selectedItem.value == "Ocupaciones"
                        ) {
                            handleItemClick(Screen.OcupacionList, "Ocupaciones")
                        }

                        DrawerItem(
                            title = "Empleados",
                            icon = Icons.Filled.People,
                            isSelected = selectedItem.value == "Empleados"
                        ) {
                            handleItemClick(Screen.EmpleadoList, "Empleados")
                        }
                    }
                }
            }
        }
    ) {
        content()
    }
}