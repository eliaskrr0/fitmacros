package com.eliaskrr.fitmacros.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.eliaskrr.fitmacros.data.model.Dieta
import kotlinx.coroutines.flow.Flow

@Dao
interface DietaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dieta: Dieta)

    @Query("SELECT * FROM tb_dietas ORDER BY nombre ASC")
    fun getAll(): Flow<List<Dieta>>
}
