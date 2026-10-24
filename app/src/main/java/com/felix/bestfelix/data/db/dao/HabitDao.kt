package com.felix.bestfelix.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.felix.bestfelix.data.db.entity.HabitDayEntity
import com.felix.bestfelix.data.db.entity.HabitEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY position ASC, id ASC")
    fun observeHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habit_days")
    fun observeAllDays(): Flow<List<HabitDayEntity>>

    @Query("SELECT * FROM habit_days WHERE habitId = :habitId ORDER BY date ASC")
    fun observeDays(habitId: Long): Flow<List<HabitDayEntity>>

    @Query("SELECT * FROM habits ORDER BY position ASC, id ASC")
    suspend fun getHabits(): List<HabitEntity>

    @Query("SELECT * FROM habit_days")
    suspend fun getAllDays(): List<HabitDayEntity>

    @Upsert
    suspend fun upsertHabit(habit: HabitEntity): Long

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM habits")
    suspend fun nextPosition(): Int

    @Upsert
    suspend fun upsertDay(day: HabitDayEntity)

    @Query("DELETE FROM habit_days WHERE habitId = :habitId AND date = :date")
    suspend fun deleteDay(habitId: Long, date: LocalDate)

    @Query("DELETE FROM habits")
    suspend fun clear()

    // --- Import de sauvegarde : les identifiants du fichier sont conservés tels quels. ---

    @Insert
    suspend fun insertHabits(habits: List<HabitEntity>)

    @Insert
    suspend fun insertDays(days: List<HabitDayEntity>)
}
