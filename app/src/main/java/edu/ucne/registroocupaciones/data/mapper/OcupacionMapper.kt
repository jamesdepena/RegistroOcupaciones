package edu.ucne.registroocupaciones.data.mapper

import edu.ucne.registroocupaciones.data.local.entities.OcupacionEntity
import edu.ucne.registroocupaciones.domain.model.Ocupacion

fun Ocupacion.toEntity(): OcupacionEntity =
    OcupacionEntity(
        ocupacionId = ocupacionId,
        descripcion = descripcion,
        sueldo = sueldo
    )

fun OcupacionEntity.toDomain(): Ocupacion =
    Ocupacion(
        ocupacionId = ocupacionId,
        descripcion = descripcion,
        sueldo = sueldo
    )