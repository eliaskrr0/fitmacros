package com.eliaskrr.fitmacros.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_dietas")
data class Dieta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String
)
