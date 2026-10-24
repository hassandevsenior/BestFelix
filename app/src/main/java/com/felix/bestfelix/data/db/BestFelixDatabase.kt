package com.felix.bestfelix.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.felix.bestfelix.data.db.dao.FinanceDao
import com.felix.bestfelix.data.db.dao.HabitDao
import com.felix.bestfelix.data.db.dao.NoteDao
import com.felix.bestfelix.data.db.dao.ProjectDao
import com.felix.bestfelix.data.db.dao.QuitHabitDao
import com.felix.bestfelix.data.db.dao.SavingDao
import com.felix.bestfelix.data.db.entity.CreditEntity
import com.felix.bestfelix.data.db.entity.DepositEntity
import com.felix.bestfelix.data.db.entity.HabitDayEntity
import com.felix.bestfelix.data.db.entity.HabitEntity
import com.felix.bestfelix.data.db.entity.IncomeEntity
import com.felix.bestfelix.data.db.entity.NoteEntity
import com.felix.bestfelix.data.db.entity.ProjectEntity
import com.felix.bestfelix.data.db.entity.QuitHabitEntity
import com.felix.bestfelix.data.db.entity.RelapseEntity
import com.felix.bestfelix.data.db.entity.RepaymentEntity
import com.felix.bestfelix.data.db.entity.SavingGoalEntity

/**
 * Schéma exporté dans app/schemas : chaque montée de version doit s'accompagner d'une
 * migration écrite à la main. Pas de fallbackToDestructiveMigration, jamais.
 */
@Database(
    entities = [
        HabitEntity::class,
        HabitDayEntity::class,
        QuitHabitEntity::class,
        RelapseEntity::class,
        ProjectEntity::class,
        SavingGoalEntity::class,
        DepositEntity::class,
        CreditEntity::class,
        RepaymentEntity::class,
        IncomeEntity::class,
        NoteEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class BestFelixDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun quitHabitDao(): QuitHabitDao
    abstract fun projectDao(): ProjectDao
    abstract fun savingDao(): SavingDao
    abstract fun financeDao(): FinanceDao
    abstract fun noteDao(): NoteDao

    companion object {
        const val NAME = "bestfelix.db"
    }
}
