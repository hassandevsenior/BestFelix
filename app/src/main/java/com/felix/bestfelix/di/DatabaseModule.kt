package com.felix.bestfelix.di

import android.content.Context
import androidx.room.Room
import com.felix.bestfelix.data.db.BestFelixDatabase
import com.felix.bestfelix.data.db.dao.FinanceDao
import com.felix.bestfelix.data.db.dao.HabitDao
import com.felix.bestfelix.data.db.dao.NoteDao
import com.felix.bestfelix.data.db.dao.ProjectDao
import com.felix.bestfelix.data.db.dao.QuitHabitDao
import com.felix.bestfelix.data.db.dao.SavingDao
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
    fun provideDatabase(@ApplicationContext context: Context): BestFelixDatabase =
        Room.databaseBuilder(context, BestFelixDatabase::class.java, BestFelixDatabase.NAME)
            // Clés étrangères actives : supprimer une habitude emporte ses jours validés.
            .build()

    @Provides fun provideHabitDao(db: BestFelixDatabase): HabitDao = db.habitDao()

    @Provides fun provideQuitHabitDao(db: BestFelixDatabase): QuitHabitDao = db.quitHabitDao()

    @Provides fun provideProjectDao(db: BestFelixDatabase): ProjectDao = db.projectDao()

    @Provides fun provideSavingDao(db: BestFelixDatabase): SavingDao = db.savingDao()

    @Provides fun provideFinanceDao(db: BestFelixDatabase): FinanceDao = db.financeDao()

    @Provides fun provideNoteDao(db: BestFelixDatabase): NoteDao = db.noteDao()
}
