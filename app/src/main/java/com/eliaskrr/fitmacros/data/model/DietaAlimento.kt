package com.eliaskrr.fitmacros.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "tb_dieta_alimentos",
    foreignKeys = [
        ForeignKey(
            entity = Dieta::class,
            parentColumns = ["id"],
            childColumns = ["dieta_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Alimento::class,
            parentColumns = ["id"],
            childColumns = ["alimento_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("dieta_id"), Index("alimento_id")]
)
data class DietaAlimento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "dieta_id")
    val dietaId: Int,
    @ColumnInfo(name = "alimento_id")
    val alimentoId: Int,
    @ColumnInfo(name = "meal_type")
    val mealType: MealType,
    val servings: Double = 1.0,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

data class DietaAlimentoWithAlimento(
    @Embedded
    val dietaAlimento: DietaAlimento,
    @Relation(
        parentColumn = "alimento_id",
        entityColumn = "id"
    )
    val alimento: Alimento
)
