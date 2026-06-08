package com.example.swo.di

import android.app.Application
import androidx.room.Room
import com.example.swo.data.local.TaskDatabase
import com.example.swo.data.remote.TaskApi
import com.example.swo.data.repository.TaskRepositoryImpl
import com.example.swo.domain.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTaskApi(): TaskApi {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/") // Placeholder URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TaskApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTaskDatabase(app: Application): TaskDatabase {
        return Room.databaseBuilder(
            app,
            TaskDatabase::class.java,
            "task_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        api: TaskApi,
        db: TaskDatabase
    ): TaskRepository {
        return TaskRepositoryImpl(api, db.dao)
    }
}
