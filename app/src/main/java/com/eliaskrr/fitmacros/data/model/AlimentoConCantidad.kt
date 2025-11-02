package com.eliaskrr.fitmacros.data.model

import androidx.room.Embedded

data class AlimentoConCantidad(
    @Embedded val alimento: Alimento,
    val cantidad: Double
)
