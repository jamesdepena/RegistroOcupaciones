package edu.ucne.registroocupaciones.presentation.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import edu.ucne.registroocupaciones.presentation.empleado.edit.EditEmpleadoScreen
import edu.ucne.registroocupaciones.presentation.empleado.list.EmpleadoListScreen
import edu.ucne.registroocupaciones.presentation.ocupacion.edit.EditOcupacionScreen
import edu.ucne.registroocupaciones.presentation.ocupacion.list.OcupacionListScreen
import kotlinx.coroutines.launch

@Composable
fun OcupacionNavHost(
    navController: NavHostController = rememberNavController()
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    DrawerMenu(
        drawerState = drawerState,
        navHostController = navController
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.OcupacionList
        ) {
            composable<Screen.OcupacionList> {
                OcupacionListScreen(
                    onDrawer = { scope.launch { drawerState.open() } },
                    goToOcupacion = { id ->
                        navController.navigate(Screen.Ocupacion(id))
                    },
                    createOcupacion = {
                        navController.navigate(Screen.Ocupacion(0))
                    }

                )
            }

            composable<Screen.Ocupacion> {
                val args = it.toRoute<Screen.Ocupacion>()
                EditOcupacionScreen(
                    ocupacionId = args.ocupacionId,
                    onNavigateBack = { navController.navigateUp() },
                    onDrawer = { scope.launch { drawerState.open() } },
                )
            }

            composable<Screen.EmpleadoList> {
                EmpleadoListScreen(
                    onDrawer = { scope.launch { drawerState.open() } },
                    goToEmpleado = { id ->
                        navController.navigate(Screen.EmpleadoForm(id))
                    },
                    createEmpleado = {
                        navController.navigate(Screen.EmpleadoForm(0))
                    }
                )
            }

            composable<Screen.EmpleadoForm> {
                val args = it.toRoute<Screen.EmpleadoForm>()
                EditEmpleadoScreen(
                    empleadoId = args.empleadoId,
                    onNavigateBack = { navController.navigateUp() },
                    onDrawer = { scope.launch { drawerState.open() } },
                )
            }
        }
    }
}