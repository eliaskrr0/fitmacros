package com.eliaskrr.fitmacros.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_alimentos")
data class Alimento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val precio: Double? = null,
    val marca: String? = null,
    val proteinas: Double,
    val carbos: Double,
    val grasas: Double,
    val calorias: Double,
    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "fecha_actualizacion")
    val fechaActualizacion: Long = System.currentTimeMillis(),
    val detalles: String? = null
)
