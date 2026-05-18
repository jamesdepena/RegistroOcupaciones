package edu.ucne.registroocupaciones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import edu.ucne.registroocupaciones.presentation.navigation.OcupacionNavHost
import edu.ucne.registroocupaciones.ui.theme.RegistroOcupacionesTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegistroOcupacionesTheme {
                val navHostController = rememberNavController()
                OcupacionNavHost(navController = navHostController)
            }
        }
    }
}