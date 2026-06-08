package com.example.swo.core.di

import com.example.swo.data.chatbot.repository.ChatRepositoryImpl
import com.example.swo.data.projects.repository.ProjectRepositoryImpl
import com.example.swo.domain.chatbot.ChatRepository
import com.example.swo.domain.projects.ProjectRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindIncidentRepository(
        incidentRepositoryImpl: com.example.swo.data.incidents.repository.IncidentRepositoryImpl
    ): com.example.swo.domain.incidents.IncidentRepository

    @Binds
    @Singleton
    abstract fun bindLegacyIncidentRepository(
        incidentRepositoryImpl: com.example.swo.data.repository.IncidentRepositoryImpl
    ): com.example.swo.domain.repository.IncidentRepository

    @Binds
    @Singleton
    abstract fun bindProjectRepository(
        projectRepositoryImpl: ProjectRepositoryImpl
    ): ProjectRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: com.example.swo.data.users.repository.UserRepositoryImpl
    ): com.example.swo.domain.users.UserRepository

    @Binds
    @Singleton
    abstract fun bindLegacyUserRepository(
        userRepositoryImpl: com.example.swo.data.repository.MockUserRepositoryImpl
    ): com.example.swo.domain.repository.UserRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository
}
