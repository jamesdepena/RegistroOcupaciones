package edu.ucne.registroocupaciones.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object OcupacionList : Screen()

    @Serializable
    data class Ocupacion(val ocupacionId: Int) : Screen()
}