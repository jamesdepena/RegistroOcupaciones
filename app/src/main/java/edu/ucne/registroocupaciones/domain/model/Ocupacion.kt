package edu.ucne.registroocupaciones.domain.model

data class Ocupacion(
    val ocupacionId: Int = 0,
    val descripcion: String,
    val sueldo: Double
)
