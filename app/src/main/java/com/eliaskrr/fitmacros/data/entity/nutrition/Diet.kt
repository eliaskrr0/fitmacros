package com.eliaskrr.fitmacros.data.entity.nutrition

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_diet")
data class Diet(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String
)
