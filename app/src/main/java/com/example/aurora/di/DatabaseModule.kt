package com.example.aurora.di

import android.content.Context
import androidx.room.Room
import com.example.aurora.data.CalendarManager
import com.example.aurora.data.JournalManager
import com.example.aurora.data.db.AppDatabase
import com.example.aurora.data.db.JournalDao

import com.example.aurora.data.db.MoodDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideJournalDao(database: AppDatabase): JournalDao {
        return database.journalDao()
    }

    @Provides
    @Singleton
    fun provideMoodDao(database: AppDatabase): MoodDao {
        return database.moodDao()
    }

    @Provides
    @Singleton
    fun provideJournalManager(journalDao: JournalDao, moodDao: MoodDao): JournalManager {
        return JournalManager(journalDao, moodDao)
    }

    @Provides
    @Singleton
    fun provideCalendarManager(journalDao: JournalDao, moodDao: MoodDao): CalendarManager {
        return CalendarManager(journalDao, moodDao)
    }
}
