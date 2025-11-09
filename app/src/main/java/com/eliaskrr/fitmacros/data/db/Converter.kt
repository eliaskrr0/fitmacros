package com.eliaskrr.fitmacros.data.db

import androidx.room.TypeConverter
import com.eliaskrr.fitmacros.data.entity.nutrition.type.MealType
import com.eliaskrr.fitmacros.data.entity.nutrition.type.QuantityUnit

class Converter {
    @TypeConverter
    fun fromMealType(value: MealType): Int = value.ordinal

    @TypeConverter
    fun toMealType(value: Int): MealType = MealType.values()[value]

    @TypeConverter
    fun fromQuantityUnit(value: QuantityUnit): Int = value.ordinal

    @TypeConverter
    fun toQuantityUnit(value: Int): QuantityUnit = QuantityUnit.values()[value]
}
