package com.eliaskrr.fitmacros.data.model

import androidx.room.Embedded

data class DietaAlimentoWithAlimento(
    @Embedded val alimento: Alimento,
    val cantidad: Double,
    val unidad: QuantityUnit
)
