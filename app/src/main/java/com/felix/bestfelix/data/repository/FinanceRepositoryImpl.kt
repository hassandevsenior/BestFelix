package com.felix.bestfelix.data.repository

import com.felix.bestfelix.data.db.dao.FinanceDao
import com.felix.bestfelix.data.db.entity.CreditEntity
import com.felix.bestfelix.data.db.entity.IncomeEntity
import com.felix.bestfelix.data.db.entity.RepaymentEntity
import com.felix.bestfelix.data.mapper.toDomain
import com.felix.bestfelix.data.mapper.toEntity
import com.felix.bestfelix.domain.model.Credit
import com.felix.bestfelix.domain.model.CreditDirection
import com.felix.bestfelix.domain.model.CreditWithRepayments
import com.felix.bestfelix.domain.model.Income
import com.felix.bestfelix.domain.model.Repayment
import com.felix.bestfelix.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinanceRepositoryImpl @Inject constructor(
    private val dao: FinanceDao,
) : FinanceRepository {

    override fun observeCredits(): Flow<List<CreditWithRepayments>> =
        combine(dao.observeCredits(), dao.observeAllRepayments()) { credits, repayments ->
            val byCredit = repayments.groupBy { it.creditId }
            credits.map { entity ->
                CreditWithRepayments(
                    credit = entity.toDomain(),
                    repayments = byCredit[entity.id]?.map { it.toDomain() }.orEmpty(),
                )
            }
        }

    override fun observeIncomes(): Flow<List<Income>> =
        dao.observeIncomes().map { incomes -> incomes.map { it.toDomain() } }

    override suspend fun createCredit(
        direction: CreditDirection,
        counterparty: String,
        amount: Long,
        reason: String?,
        dueDate: LocalDate?,
    ): Long = dao.upsertCredit(
        CreditEntity(
            direction = direction,
            counterparty = counterparty.trim(),
            amount = amount,
            reason = reason?.trim()?.takeIf { it.isNotEmpty() },
            dueDate = dueDate,
        ),
    )

    override suspend fun updateCredit(credit: Credit) {
        dao.upsertCredit(
            credit.copy(
                counterparty = credit.counterparty.trim(),
                reason = credit.reason?.trim()?.takeIf { it.isNotEmpty() },
            ).toEntity(),
        )
    }

    override suspend fun deleteCredit(credit: Credit) {
        dao.deleteCredit(credit.toEntity())
    }

    override suspend fun addRepayment(creditId: Long, date: LocalDate, amount: Long) {
        dao.upsertRepayment(RepaymentEntity(creditId = creditId, date = date, amount = amount))
    }

    override suspend fun deleteRepayment(repayment: Repayment) {
        dao.deleteRepayment(repayment.toEntity())
    }

    override suspend fun createIncome(amount: Long, date: LocalDate, source: String): Long =
        dao.upsertIncome(IncomeEntity(amount = amount, date = date, source = source.trim()))

    override suspend fun updateIncome(income: Income) {
        dao.upsertIncome(income.copy(source = income.source.trim()).toEntity())
    }

    override suspend fun deleteIncome(income: Income) {
        dao.deleteIncome(income.toEntity())
    }
}
