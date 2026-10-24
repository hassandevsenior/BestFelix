package com.felix.bestfelix.domain.repository

import com.felix.bestfelix.domain.model.Deposit
import com.felix.bestfelix.domain.model.SavingGoal
import com.felix.bestfelix.domain.model.SavingGoalWithDeposits
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface SavingRepository {

    fun observeGoals(): Flow<List<SavingGoalWithDeposits>>

    suspend fun create(title: String, purpose: String?, targetAmount: Long, dueDate: LocalDate?): Long

    suspend fun update(goal: SavingGoal)

    suspend fun delete(goal: SavingGoal)

    suspend fun addDeposit(goalId: Long, date: LocalDate, amount: Long)

    suspend fun deleteDeposit(deposit: Deposit)
}
