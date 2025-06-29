package com.students.spacegame.di


import android.content.Context
import androidx.room.Room
import com.students.spacegame.database.AppDatabase
import com.students.spacegame.database.GameScoreDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "space_game_database"
        ).build()
    }

    @Provides
    fun provideGameScoreDao(database: AppDatabase): GameScoreDao {
        return database.gameScoreDao()
    }
}
