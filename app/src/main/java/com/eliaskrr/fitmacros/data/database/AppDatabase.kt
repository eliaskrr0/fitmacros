package com.eliaskrr.fitmacros.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.eliaskrr.fitmacros.data.dao.AlimentoDao
import com.eliaskrr.fitmacros.data.dao.DietaDao
import com.eliaskrr.fitmacros.data.model.Alimento
import com.eliaskrr.fitmacros.data.model.Dieta

@Database(entities = [Alimento::class, Dieta::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alimentoDao(): AlimentoDao
    abstract fun dietaDao(): DietaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitmacros_database"
                )
                .fallbackToDestructiveMigration() // Añadimos esto para manejar la migración de forma sencilla
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
