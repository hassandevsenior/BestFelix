package com.felix.bestfelix.data.repository

import com.felix.bestfelix.data.db.dao.SavingDao
import com.felix.bestfelix.data.db.entity.DepositEntity
import com.felix.bestfelix.data.db.entity.SavingGoalEntity
import com.felix.bestfelix.data.mapper.toDomain
import com.felix.bestfelix.data.mapper.toEntity
import com.felix.bestfelix.domain.model.Deposit
import com.felix.bestfelix.domain.model.SavingGoal
import com.felix.bestfelix.domain.model.SavingGoalWithDeposits
import com.felix.bestfelix.domain.repository.SavingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavingRepositoryImpl @Inject constructor(
    private val dao: SavingDao,
) : SavingRepository {

    override fun observeGoals(): Flow<List<SavingGoalWithDeposits>> =
        combine(dao.observeGoals(), dao.observeAllDeposits()) { goals, deposits ->
            val byGoal = deposits.groupBy { it.goalId }
            goals.map { entity ->
                SavingGoalWithDeposits(
                    goal = entity.toDomain(),
                    deposits = byGoal[entity.id]?.map { it.toDomain() }.orEmpty(),
                )
            }
        }

    override suspend fun create(
        title: String,
        purpose: String?,
        targetAmount: Long,
        dueDate: LocalDate?,
    ): Long = dao.upsertGoal(
        SavingGoalEntity(
            title = title.trim(),
            purpose = purpose?.trim()?.takeIf { it.isNotEmpty() },
            targetAmount = targetAmount,
            dueDate = dueDate,
        ),
    )

    override suspend fun update(goal: SavingGoal) {
        dao.upsertGoal(
            goal.copy(
                title = goal.title.trim(),
                purpose = goal.purpose?.trim()?.takeIf { it.isNotEmpty() },
            ).toEntity(),
        )
    }

    override suspend fun delete(goal: SavingGoal) {
        dao.deleteGoal(goal.toEntity())
    }

    override suspend fun addDeposit(goalId: Long, date: LocalDate, amount: Long) {
        dao.upsertDeposit(DepositEntity(goalId = goalId, date = date, amount = amount))
    }

    override suspend fun deleteDeposit(deposit: Deposit) {
        dao.deleteDeposit(deposit.toEntity())
    }
}
