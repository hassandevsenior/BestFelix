package com.felix.bestfelix.domain.repository

import com.felix.bestfelix.domain.model.Credit
import com.felix.bestfelix.domain.model.CreditDirection
import com.felix.bestfelix.domain.model.CreditWithRepayments
import com.felix.bestfelix.domain.model.Income
import com.felix.bestfelix.domain.model.Repayment
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface FinanceRepository {

    fun observeCredits(): Flow<List<CreditWithRepayments>>

    fun observeIncomes(): Flow<List<Income>>

    suspend fun createCredit(
        direction: CreditDirection,
        counterparty: String,
        amount: Long,
        reason: String?,
        dueDate: LocalDate?,
    ): Long

    suspend fun updateCredit(credit: Credit)

    suspend fun deleteCredit(credit: Credit)

    suspend fun addRepayment(creditId: Long, date: LocalDate, amount: Long)

    suspend fun deleteRepayment(repayment: Repayment)

    suspend fun createIncome(amount: Long, date: LocalDate, source: String): Long

    suspend fun updateIncome(income: Income)

    suspend fun deleteIncome(income: Income)
}
