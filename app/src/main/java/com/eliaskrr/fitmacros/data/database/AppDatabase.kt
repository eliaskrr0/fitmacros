package com.eliaskrr.fitmacros.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.eliaskrr.fitmacros.data.dao.AlimentoDao
import com.eliaskrr.fitmacros.data.dao.DietaAlimentoDao
import com.eliaskrr.fitmacros.data.dao.DietaDao
import com.eliaskrr.fitmacros.data.model.Alimento
import com.eliaskrr.fitmacros.data.model.Dieta
import com.eliaskrr.fitmacros.data.model.DietaAlimento

@Database(entities = [Alimento::class, Dieta::class, DietaAlimento::class], version = 6, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alimentoDao(): AlimentoDao
    abstract fun dietaDao(): DietaDao
    abstract fun dietaAlimentoDao(): DietaAlimentoDao

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
                .addMigrations(*MIGRATIONS)
                .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tb_alimentos ADD COLUMN precio REAL")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tb_alimentos ADD COLUMN marca TEXT")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS tb_dietas (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS tb_dieta_alimentos (
                        dietaId INTEGER NOT NULL,
                        alimentoId INTEGER NOT NULL,
                        mealType TEXT NOT NULL,
                        cantidad REAL NOT NULL,
                        unidad TEXT NOT NULL DEFAULT 'GRAMS',
                        PRIMARY KEY(dietaId, alimentoId, mealType),
                        FOREIGN KEY(dietaId) REFERENCES tb_dietas(id) ON DELETE CASCADE,
                        FOREIGN KEY(alimentoId) REFERENCES tb_alimentos(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tb_alimentos ADD COLUMN cantidad_base REAL NOT NULL DEFAULT 100.0")
                db.execSQL("ALTER TABLE tb_alimentos ADD COLUMN unidad_base TEXT NOT NULL DEFAULT 'GRAMS'")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tb_alimentos ADD COLUMN fecha_creacion INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE tb_alimentos ADD COLUMN fecha_actualizacion INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE tb_alimentos ADD COLUMN detalles TEXT")
                db.execSQL(
                    """
                    UPDATE tb_alimentos
                    SET fecha_creacion = CASE
                        WHEN fecha_creacion = 0 THEN CAST(strftime('%s','now') AS INTEGER) * 1000
                        ELSE fecha_creacion
                    END
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    UPDATE tb_alimentos
                    SET fecha_actualizacion = CASE
                        WHEN fecha_actualizacion = 0 THEN CAST(strftime('%s','now') AS INTEGER) * 1000
                        ELSE fecha_actualizacion
                    END
                    """.trimIndent()
                )
            }
        }

        val MIGRATIONS = arrayOf(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5,
            MIGRATION_5_6
        )
    }
}
