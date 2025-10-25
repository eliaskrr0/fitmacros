package com.eliaskrr.fitmacros.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.eliaskrr.fitmacros.data.model.Alimento
import kotlinx.coroutines.flow.Flow

@Dao
interface AlimentoDao {

    @Query("SELECT * FROM tb_alimentos ORDER BY nombre ASC")
    fun getAll(): Flow<List<Alimento>>

    @Query("SELECT * FROM tb_alimentos WHERE id = :id")
    fun getById(id: Int): Flow<Alimento?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alimento: Alimento)

    @Update
    suspend fun update(alimento: Alimento)

    @Delete
    suspend fun delete(alimento: Alimento)
}
