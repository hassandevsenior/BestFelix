package com.felix.bestfelix.di

import com.felix.bestfelix.core.time.DateProvider
import com.felix.bestfelix.core.time.SystemDateProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindDateProvider(impl: SystemDateProvider): DateProvider
}

@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {

    /** Portée de l'appli : pour le travail qui doit survivre à un écran, comme la planification. */
    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
