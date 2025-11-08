package com.eliaskrr.fitmacros.data.entity.nutrition

import androidx.room.Embedded
import com.eliaskrr.fitmacros.data.entity.nutrition.type.QuantityUnit

data class Meal(
	@Embedded val food: Food,
	val cantidad: Double,
	val unidad: QuantityUnit
)