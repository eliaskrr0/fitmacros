package com.eliaskrr.fitmacros.data.model

import androidx.room.Embedded

data class RegisterDietFood(
	@Embedded val food: Food,
	val cantidad: Double,
	val unidad: QuantityUnit
)
