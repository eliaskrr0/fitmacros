package com.eliaskrr.fitmacros.data.entity.nutrition

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.eliaskrr.fitmacros.data.entity.nutrition.type.QuantityUnit

@Entity(tableName = "tb_food")
data class Food(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double? = null,
    val brand: String? = null,
    val proteins: Double,
    val carbs: Double,
    val fats: Double,
    @ColumnInfo(name = "amount_base")
    val amountBase: Double = 100.0,
    @ColumnInfo(name = "unit_base")
    val unitBase: QuantityUnit = QuantityUnit.GRAMS,
    val calories: Double,
    @ColumnInfo(name = "creation_date")
    val creationDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "update_date")
    val updateDate: Long = System.currentTimeMillis(),
)
