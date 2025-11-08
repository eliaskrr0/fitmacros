package com.eliaskrr.fitmacros.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.eliaskrr.fitmacros.data.model.DietFood
import com.eliaskrr.fitmacros.data.model.RegisterDietFood
import com.eliaskrr.fitmacros.data.model.MealType
import kotlinx.coroutines.flow.Flow

@Dao
interface DietFoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dietFood: DietFood)

    @Query(
        "DELETE FROM tb_dieta_alimentos WHERE dietaId = :dietaId AND alimentoId = :alimentoId AND mealType = :mealType"
    )
    suspend fun delete(dietaId: Int, alimentoId: Int, mealType: MealType)

    @Query(
        """
        UPDATE tb_dieta_alimentos
        SET cantidad = :cantidad
        WHERE dietaId = :dietaId AND alimentoId = :alimentoId AND mealType = :mealType
        """
    )
    suspend fun updateCantidad(dietaId: Int, alimentoId: Int, mealType: MealType, cantidad: Double)

    @Query(
        """
        SELECT a.*, da.cantidad AS cantidad, da.unidad AS unidad FROM tb_alimentos a
        INNER JOIN tb_dieta_alimentos da ON a.id = da.alimentoId
        WHERE da.dietaId = :dietaId
    """
    )
    fun getAlimentosForDieta(dietaId: Int): Flow<List<RegisterDietFood>>

    @Query(
        """
        SELECT a.*, da.cantidad AS cantidad, da.unidad AS unidad FROM tb_alimentos a
        INNER JOIN tb_dieta_alimentos da ON a.id = da.alimentoId
        WHERE da.dietaId = :dietaId AND da.mealType = :mealType
    """
    )
    fun getAlimentosForDietaAndMeal(dietaId: Int, mealType: MealType): Flow<List<RegisterDietFood>>

    @Query("SELECT SUM(a.calorias * da.cantidad / 100) FROM tb_alimentos a INNER JOIN tb_dieta_alimentos da ON a.id = da.alimentoId WHERE da.dietaId = :dietaId AND da.mealType = :mealType")
    fun getTotalCaloriasForMeal(dietaId: Int, mealType: MealType): Flow<Double?>
}
