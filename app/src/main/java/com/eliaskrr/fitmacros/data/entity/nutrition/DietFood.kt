package com.eliaskrr.fitmacros.data.entity.nutrition

import androidx.room.Entity
import androidx.room.ForeignKey
import com.eliaskrr.fitmacros.data.entity.nutrition.type.QuantityUnit
import com.eliaskrr.fitmacros.data.entity.nutrition.type.MealType

@Entity(
    tableName = "tb_dieta_alimentos",
    primaryKeys = ["dietaId", "alimentoId", "mealType"],
    foreignKeys = [
        ForeignKey(entity = Diet::class, parentColumns = ["id"], childColumns = ["dietaId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Food::class, parentColumns = ["id"], childColumns = ["alimentoId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DietFood(
    val dietaId: Int,
    val alimentoId: Int,
    val mealType: MealType,
    val cantidad: Double,
    val unidad: QuantityUnit = QuantityUnit.GRAMS
)
