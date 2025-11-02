package com.eliaskrr.fitmacros.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.eliaskrr.fitmacros.data.model.DietaAlimento
import com.eliaskrr.fitmacros.data.model.DietaAlimentoWithAlimento
import kotlinx.coroutines.flow.Flow

@Dao
interface DietaAlimentoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dietaAlimento: DietaAlimento)

    @Transaction
    @Query("SELECT * FROM tb_dieta_alimentos WHERE dieta_id = :dietaId ORDER BY created_at DESC")
    fun getByDieta(dietaId: Int): Flow<List<DietaAlimentoWithAlimento>>

    @Query("DELETE FROM tb_dieta_alimentos WHERE id = :id")
    suspend fun deleteById(id: Int)
}
