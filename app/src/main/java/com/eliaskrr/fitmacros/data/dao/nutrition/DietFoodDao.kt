package com.eliaskrr.fitmacros.data.dao.nutrition

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.eliaskrr.fitmacros.data.entity.nutrition.DietFood
import com.eliaskrr.fitmacros.data.entity.nutrition.Meal
import com.eliaskrr.fitmacros.data.entity.nutrition.type.MealType
import kotlinx.coroutines.flow.Flow

@Dao
interface DietFoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dietFood: DietFood)

    @Query(
        "DELETE FROM tb_diet_food WHERE dietId = :dietId AND foodId = :foodId AND mealType = :mealType"
    )
    suspend fun delete(dietId: Int, foodId: Int, mealType: MealType)

    @Query(
        """
        UPDATE tb_diet_food
        SET amount = :amount
        WHERE dietId = :dietId AND foodId = :foodId AND mealType = :mealType
        """
    )
    suspend fun updateCantidad(dietId: Int, foodId: Int, mealType: MealType, amount: Double)

    @Query(
        """
        SELECT a.*, da.amount AS amount, da.amount AS unit FROM tb_food a
        INNER JOIN tb_diet_food da ON a.id = da.foodId
        WHERE da.dietId = :dietId
    """
    )
    fun getAlimentosForDieta(dietId: Int): Flow<List<Meal>>

    @Query(
        """
        SELECT a.*, da.amount AS amount, da.unit AS unit FROM tb_food a
        INNER JOIN tb_diet_food da ON a.id = da.foodId
        WHERE da.dietId = :dietId AND da.mealType = :mealType
    """
    )
    fun getAlimentosForDietaAndMeal(dietId: Int, mealType: MealType): Flow<List<Meal>>

    @Query("SELECT SUM(a.calories * da.amount / 100) FROM tb_food a INNER JOIN tb_diet_food da ON a.id = da.foodId WHERE da.dietId = :dietId AND da.mealType = :mealType")
    fun gettotalCaloriesForMeal(dietId: Int, mealType: MealType): Flow<Double?>
}
