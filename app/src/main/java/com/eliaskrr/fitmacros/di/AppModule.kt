package com.eliaskrr.fitmacros.di

import android.content.Context
import androidx.room.Room
import com.eliaskrr.fitmacros.data.dao.nutrition.FoodDao
import com.eliaskrr.fitmacros.data.dao.nutrition.DietFoodDao
import com.eliaskrr.fitmacros.data.dao.nutrition.DietDao
import com.eliaskrr.fitmacros.data.db.AppDatabase
import com.eliaskrr.fitmacros.data.repository.nutrition.FoodRepository
import com.eliaskrr.fitmacros.data.repository.nutrition.DietFoodRepository
import com.eliaskrr.fitmacros.data.repository.nutrition.DietRepository
import com.eliaskrr.fitmacros.data.repository.user.UserDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fitmacros_database"
        )
            .addMigrations(*AppDatabase.MIGRATIONS)
            .build()

    @Provides
    fun provideAlimentoDao(database: AppDatabase): FoodDao = database.alimentoDao()

    @Provides
    fun provideDietaDao(database: AppDatabase): DietDao = database.dietaDao()

    @Provides
    fun provideDietaAlimentoDao(database: AppDatabase): DietFoodDao = database.dietaAlimentoDao()

    @Provides
    @Singleton
    fun provideAlimentoRepository(foodDao: FoodDao): FoodRepository =
        FoodRepository(foodDao)

    @Provides
    @Singleton
    fun provideDietaRepository(dietaDao: DietDao): DietRepository =
        DietRepository(dietaDao)

    @Provides
    @Singleton
    fun provideDietaAlimentoRepository(
        dietFoodDao: DietFoodDao
    ): DietFoodRepository = DietFoodRepository(dietFoodDao)

    @Provides
    @Singleton
    fun provideUserDataRepository(
        @ApplicationContext context: Context
    ): UserDataRepository = UserDataRepository(context)
}
