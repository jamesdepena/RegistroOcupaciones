package edu.ucne.registroocupaciones.presentation.ocupacion.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import edu.ucne.registroocupaciones.presentation.ocupacion.edit.EditOcupacionScreen
import edu.ucne.registroocupaciones.presentation.ocupacion.list.OcupacionListScreen

@Composable
fun OcupacionNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.OcupacionList
    ) {
        composable<Screen.OcupacionList> {
            OcupacionListScreen(
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
            )
        }
    }
}