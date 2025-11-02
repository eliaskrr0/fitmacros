package com.eliaskrr.fitmacros.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.eliaskrr.fitmacros.data.model.Alimento
import com.eliaskrr.fitmacros.data.model.DietaAlimento
import com.eliaskrr.fitmacros.data.model.MealType
import kotlinx.coroutines.flow.Flow

@Dao
interface DietaAlimentoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dietaAlimento: DietaAlimento)

    @Query("""
        SELECT a.* FROM tb_alimentos a
        INNER JOIN tb_dieta_alimentos da ON a.id = da.alimentoId
        WHERE da.dietaId = :dietaId AND da.mealType = :mealType
    """)
    fun getAlimentosForDietaAndMeal(dietaId: Int, mealType: MealType): Flow<List<Alimento>>

    @Query("SELECT SUM(a.calorias * da.cantidad / 100) FROM tb_alimentos a INNER JOIN tb_dieta_alimentos da ON a.id = da.alimentoId WHERE da.dietaId = :dietaId AND da.mealType = :mealType")
    fun getTotalCaloriasForMeal(dietaId: Int, mealType: MealType): Flow<Double?>
}
