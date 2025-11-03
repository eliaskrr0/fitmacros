package com.eliaskrr.fitmacros.data.database

import androidx.room.TypeConverter
import com.eliaskrr.fitmacros.data.model.MealType

class Converters {
    @TypeConverter
    fun fromMealType(value: MealType): String = value.name

    @TypeConverter
    fun toMealType(value: String): MealType = MealType.valueOf(value)
}
