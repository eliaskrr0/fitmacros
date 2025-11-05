package com.eliaskrr.fitmacros.di

import android.content.Context
import androidx.room.Room
import com.eliaskrr.fitmacros.data.dao.AlimentoDao
import com.eliaskrr.fitmacros.data.dao.DietaAlimentoDao
import com.eliaskrr.fitmacros.data.dao.DietaDao
import com.eliaskrr.fitmacros.data.database.AppDatabase
import com.eliaskrr.fitmacros.data.repository.AlimentoRepository
import com.eliaskrr.fitmacros.data.repository.DietaAlimentoRepository
import com.eliaskrr.fitmacros.data.repository.DietaRepository
import com.eliaskrr.fitmacros.data.repository.UserDataRepository
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
    fun provideAlimentoDao(database: AppDatabase): AlimentoDao = database.alimentoDao()

    @Provides
    fun provideDietaDao(database: AppDatabase): DietaDao = database.dietaDao()

    @Provides
    fun provideDietaAlimentoDao(database: AppDatabase): DietaAlimentoDao = database.dietaAlimentoDao()

    @Provides
    @Singleton
    fun provideAlimentoRepository(alimentoDao: AlimentoDao): AlimentoRepository =
        AlimentoRepository(alimentoDao)

    @Provides
    @Singleton
    fun provideDietaRepository(dietaDao: DietaDao): DietaRepository =
        DietaRepository(dietaDao)

    @Provides
    @Singleton
    fun provideDietaAlimentoRepository(
        dietaAlimentoDao: DietaAlimentoDao
    ): DietaAlimentoRepository = DietaAlimentoRepository(dietaAlimentoDao)

    @Provides
    @Singleton
    fun provideUserDataRepository(
        @ApplicationContext context: Context
    ): UserDataRepository = UserDataRepository(context)
}
