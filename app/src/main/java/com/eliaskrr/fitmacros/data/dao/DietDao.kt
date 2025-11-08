package com.eliaskrr.fitmacros.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.eliaskrr.fitmacros.data.model.Diet
import kotlinx.coroutines.flow.Flow

@Dao
interface DietDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(diet: Diet)

    @Query("SELECT * FROM tb_dietas ORDER BY nombre ASC")
    fun getAll(): Flow<List<Diet>>

    @Query("DELETE FROM tb_dietas WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)
}
