package com.eliaskrr.fitmacros.data.entity.nutrition

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.eliaskrr.fitmacros.data.entity.nutrition.type.QuantityUnit

@Entity(tableName = "tb_food")
data class Food(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val precio: Double? = null,
    val marca: String? = null,
    val proteinas: Double,
    val carbos: Double,
    val grasas: Double,
    @ColumnInfo(name = "cantidad_base")
    val cantidadBase: Double = 100.0,
    @ColumnInfo(name = "unidad_base")
    val unidadBase: QuantityUnit = QuantityUnit.GRAMS,
    val calorias: Double,
    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "fecha_actualizacion")
    val fechaActualizacion: Long = System.currentTimeMillis(),
)
