package com.eliaskrr.fitmacros.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.eliaskrr.fitmacros.data.dao.nutrition.FoodDao
import com.eliaskrr.fitmacros.data.dao.nutrition.DietFoodDao
import com.eliaskrr.fitmacros.data.dao.nutrition.DietDao
import com.eliaskrr.fitmacros.data.entity.nutrition.Food
import com.eliaskrr.fitmacros.data.entity.nutrition.Diet
import com.eliaskrr.fitmacros.data.entity.nutrition.DietFood

@Database(entities = [Food::class, Diet::class, DietFood::class], version = 7, exportSchema = false)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alimentoDao(): FoodDao
    abstract fun dietaDao(): DietDao
    abstract fun dietaAlimentoDao(): DietFoodDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitmacros_db"
                )
                .addMigrations(*MIGRATIONS)
                .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tb_food ADD COLUMN price REAL")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tb_food ADD COLUMN brand TEXT")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS tb_diet (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS tb_diet_food (
                        dietId INTEGER NOT NULL,
                        foodId INTEGER NOT NULL,
                        mealType TEXT NOT NULL,
                        amount REAL NOT NULL,
                        unit TEXT NOT NULL DEFAULT 'GRAMS',
                        PRIMARY KEY(dietId, foodId, mealType),
                        FOREIGN KEY(dietId) REFERENCES tb_diet(id) ON DELETE CASCADE,
                        FOREIGN KEY(foodId) REFERENCES tb_food(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tb_food ADD COLUMN amount_base REAL NOT NULL DEFAULT 100.0")
                db.execSQL("ALTER TABLE tb_food ADD COLUMN unit_base TEXT NOT NULL DEFAULT 'GRAMS'")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tb_food ADD COLUMN creation_date INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE tb_food ADD COLUMN update_date INTEGER NOT NULL DEFAULT 0")
                db.execSQL(
                    """
                    UPDATE tb_food
                    SET creation_date = CASE
                        WHEN creation_date = 0 THEN CAST(strftime('%s','now') AS INTEGER) * 1000
                        ELSE creation_date
                    END
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    UPDATE tb_food
                    SET update_date = CASE
                        WHEN update_date = 0 THEN CAST(strftime('%s','now') AS INTEGER) * 1000
                        ELSE update_date
                    END
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS tb_food_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        price REAL,
                        brand TEXT,
                        proteins REAL NOT NULL,
                        carbs REAL NOT NULL,
                        fats REAL NOT NULL,
                        amount_base REAL NOT NULL,
                        unit_base INTEGER NOT NULL DEFAULT 0,
                        calories REAL NOT NULL,
                        creation_date INTEGER NOT NULL,
                        update_date INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO tb_food_new (
                        id,
                        name,
                        price,
                        brand,
                        proteins,
                        carbs,
                        fats,
                        amount_base,
                        unit_base,
                        calories,
                        creation_date,
                        update_date
                    )
                    SELECT
                        id,
                        name,
                        price,
                        brand,
                        proteins,
                        carbs,
                        fats,
                        amount_base,
                        CASE unit_base
                            WHEN 'GRAMS' THEN 0
                            WHEN 'MILLILITERS' THEN 1
                            ELSE 0
                        END,
                        calories,
                        creation_date,
                        update_date
                    FROM tb_food
                    """.trimIndent()
                )

                db.execSQL("DROP TABLE tb_food")
                db.execSQL("ALTER TABLE tb_food_new RENAME TO tb_food")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS tb_diet_food_new (
                        dietId INTEGER NOT NULL,
                        foodId INTEGER NOT NULL,
                        mealType INTEGER NOT NULL,
                        amount REAL NOT NULL,
                        unit INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(dietId, foodId, mealType),
                        FOREIGN KEY(dietId) REFERENCES tb_diet(id) ON DELETE CASCADE,
                        FOREIGN KEY(foodId) REFERENCES tb_food(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO tb_diet_food_new (
                        dietId,
                        foodId,
                        mealType,
                        amount,
                        unit
                    )
                    SELECT
                        dietId,
                        foodId,
                        CASE mealType
                            WHEN 'BREAKFAST' THEN 0
                            WHEN 'LUNCH' THEN 1
                            WHEN 'AFTERNOON_SNACK' THEN 2
                            WHEN 'DINNER' THEN 3
                            ELSE 0
                        END,
                        amount,
                        CASE unit
                            WHEN 'GRAMS' THEN 0
                            WHEN 'MILLILITERS' THEN 1
                            ELSE 0
                        END
                    FROM tb_diet_food
                    """.trimIndent()
                )

                db.execSQL("DROP TABLE tb_diet_food")
                db.execSQL("ALTER TABLE tb_diet_food_new RENAME TO tb_diet_food")
            }
        }

        val MIGRATIONS = arrayOf(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5,
            MIGRATION_5_6,
            MIGRATION_6_7
        )
    }
}
