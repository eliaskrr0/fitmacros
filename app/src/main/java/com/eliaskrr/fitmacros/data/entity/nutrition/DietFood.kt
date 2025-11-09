package com.eliaskrr.fitmacros.data.entity.nutrition

import androidx.room.Entity
import androidx.room.ForeignKey
import com.eliaskrr.fitmacros.data.entity.nutrition.type.QuantityUnit
import com.eliaskrr.fitmacros.data.entity.nutrition.type.MealType

@Entity(
    tableName = "tb_diet_food",
    primaryKeys = ["dietId", "foodId", "mealType"],
    foreignKeys = [
        ForeignKey(entity = Diet::class, parentColumns = ["id"], childColumns = ["dietId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Food::class, parentColumns = ["id"], childColumns = ["foodId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DietFood(
    val dietId: Int,
    val foodId: Int,
    val mealType: MealType,
    val amount: Double,
    val unit: QuantityUnit = QuantityUnit.GRAMS
)
