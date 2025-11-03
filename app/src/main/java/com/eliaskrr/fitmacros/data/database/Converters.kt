package com.eliaskrr.fitmacros.data.database

import androidx.room.TypeConverter
import com.eliaskrr.fitmacros.data.model.MealType
import com.eliaskrr.fitmacros.data.model.QuantityUnit

class Converters {
    @TypeConverter
    fun fromMealType(value: MealType): String = value.name

    @TypeConverter
    fun toMealType(value: String): MealType = MealType.valueOf(value)

    @TypeConverter
    fun fromQuantityUnit(value: QuantityUnit): String = value.name

    @TypeConverter
    fun toQuantityUnit(value: String): QuantityUnit = QuantityUnit.valueOf(value)
}
