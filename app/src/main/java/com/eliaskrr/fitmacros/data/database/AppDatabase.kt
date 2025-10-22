package com.eliaskrr.fitmacros.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.eliaskrr.fitmacros.data.dao.AlimentoDao
import com.eliaskrr.fitmacros.data.model.Alimento

@Database(entities = [Alimento::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alimentoDao(): AlimentoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "db_fitmacros"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
