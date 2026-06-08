package com.example.swo.core.di

import android.content.Context
import androidx.room.Room
import com.example.swo.data.local.SWODatabase
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
    fun provideDatabase(@ApplicationContext context: Context): SWODatabase {
        return Room.databaseBuilder(
            context,
            SWODatabase::class.java,
            SWODatabase.DATABASE_NAME
        )
        .addMigrations(SWODatabase.MIGRATION_2_3, SWODatabase.MIGRATION_3_4, SWODatabase.MIGRATION_4_5)
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideIncidentDao(database: SWODatabase) = database.incidentDao()

    @Provides
    fun provideProjectDao(database: SWODatabase) = database.projectDao()

    @Provides
    fun provideUserDao(database: SWODatabase) = database.userDao()

    @Provides
    fun provideReportDao(database: SWODatabase) = database.reportDao()

    @Provides
    fun provideChatDao(database: SWODatabase) = database.chatDao()

    @Provides
    fun provideCategoryDao(database: SWODatabase) = database.categoryDao()

    @Provides
    fun provideComentarioDao(database: SWODatabase) = database.comentarioDao()

    @Provides
    fun provideNotificacionDao(database: SWODatabase) = database.notificacionDao()
}
