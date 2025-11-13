package com.eliaskrr.fitmacros.data.db

import androidx.room.TypeConverter
import com.eliaskrr.fitmacros.data.entity.nutrition.type.MealType
import com.eliaskrr.fitmacros.data.entity.nutrition.type.QuantityUnit

class Converter {
    @TypeConverter
    fun fromMealType(value: MealType): String = value.name

    @TypeConverter
    fun toMealType(value: String): MealType {
        val entries = MealType.values()
        entries.firstOrNull { it.name == value }?.let { return it }

        val ordinal = value.toIntOrNull()
        return ordinal?.let { entries.getOrNull(it) } ?: entries.first()
    }

    @TypeConverter
    fun fromQuantityUnit(value: QuantityUnit): String = value.name

    @TypeConverter
    fun toQuantityUnit(value: String): QuantityUnit {
        val entries = QuantityUnit.values()
        entries.firstOrNull { it.name == value }?.let { return it }

        val ordinal = value.toIntOrNull()
        return ordinal?.let { entries.getOrNull(it) } ?: entries.first()
    }
}
