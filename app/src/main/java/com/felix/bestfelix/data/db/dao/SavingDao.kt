package com.felix.bestfelix.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.felix.bestfelix.data.db.entity.DepositEntity
import com.felix.bestfelix.data.db.entity.SavingGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingDao {

    @Query("SELECT * FROM saving_goals")
    fun observeGoals(): Flow<List<SavingGoalEntity>>

    @Query("SELECT * FROM deposits ORDER BY date ASC")
    fun observeAllDeposits(): Flow<List<DepositEntity>>

    @Query("SELECT * FROM saving_goals")
    suspend fun getGoals(): List<SavingGoalEntity>

    @Query("SELECT * FROM deposits ORDER BY date ASC")
    suspend fun getDeposits(): List<DepositEntity>

    @Upsert
    suspend fun upsertGoal(goal: SavingGoalEntity): Long

    @Delete
    suspend fun deleteGoal(goal: SavingGoalEntity)

    @Upsert
    suspend fun upsertDeposit(deposit: DepositEntity): Long

    @Delete
    suspend fun deleteDeposit(deposit: DepositEntity)

    @Query("DELETE FROM saving_goals")
    suspend fun clear()

    @Insert
    suspend fun insertGoals(goals: List<SavingGoalEntity>)

    @Insert
    suspend fun insertDeposits(deposits: List<DepositEntity>)
}
