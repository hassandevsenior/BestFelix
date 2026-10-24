package com.felix.bestfelix.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.felix.bestfelix.data.db.entity.QuitHabitEntity
import com.felix.bestfelix.data.db.entity.RelapseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuitHabitDao {

    @Query("SELECT * FROM quit_habits ORDER BY position ASC, id ASC")
    fun observeQuitHabits(): Flow<List<QuitHabitEntity>>

    @Query("SELECT * FROM relapses ORDER BY date ASC")
    fun observeAllRelapses(): Flow<List<RelapseEntity>>

    @Query("SELECT * FROM quit_habits ORDER BY position ASC, id ASC")
    suspend fun getQuitHabits(): List<QuitHabitEntity>

    @Query("SELECT * FROM relapses ORDER BY date ASC")
    suspend fun getRelapses(): List<RelapseEntity>

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM quit_habits")
    suspend fun nextPosition(): Int

    @Upsert
    suspend fun upsertQuitHabit(habit: QuitHabitEntity): Long

    @Delete
    suspend fun deleteQuitHabit(habit: QuitHabitEntity)

    @Upsert
    suspend fun upsertRelapse(relapse: RelapseEntity): Long

    @Delete
    suspend fun deleteRelapse(relapse: RelapseEntity)

    @Query("DELETE FROM quit_habits")
    suspend fun clear()

    @Insert
    suspend fun insertQuitHabits(habits: List<QuitHabitEntity>)

    @Insert
    suspend fun insertRelapses(relapses: List<RelapseEntity>)
}
