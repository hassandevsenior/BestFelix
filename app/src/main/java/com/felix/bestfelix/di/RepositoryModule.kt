package com.felix.bestfelix.di

import com.felix.bestfelix.data.repository.FinanceRepositoryImpl
import com.felix.bestfelix.data.repository.HabitRepositoryImpl
import com.felix.bestfelix.data.repository.ProjectRepositoryImpl
import com.felix.bestfelix.data.repository.NoteRepositoryImpl
import com.felix.bestfelix.data.repository.QuitHabitRepositoryImpl
import com.felix.bestfelix.data.repository.SavingRepositoryImpl
import com.felix.bestfelix.domain.repository.FinanceRepository
import com.felix.bestfelix.domain.repository.HabitRepository
import com.felix.bestfelix.domain.repository.ProjectRepository
import com.felix.bestfelix.domain.repository.NoteRepository
import com.felix.bestfelix.domain.repository.QuitHabitRepository
import com.felix.bestfelix.domain.repository.SavingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds @Singleton
    abstract fun bindQuitHabitRepository(impl: QuitHabitRepositoryImpl): QuitHabitRepository

    @Binds @Singleton
    abstract fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository

    @Binds @Singleton
    abstract fun bindProjectRepository(impl: ProjectRepositoryImpl): ProjectRepository

    @Binds @Singleton
    abstract fun bindSavingRepository(impl: SavingRepositoryImpl): SavingRepository

    @Binds @Singleton
    abstract fun bindFinanceRepository(impl: FinanceRepositoryImpl): FinanceRepository
}
