package com.felix.bestfelix.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.bestfelix.core.time.DateProvider
import com.felix.bestfelix.domain.finance.CreditCalculator
import com.felix.bestfelix.domain.finance.IncomeCalculator
import com.felix.bestfelix.domain.finance.IncomeMonth
import com.felix.bestfelix.domain.model.Credit
import com.felix.bestfelix.domain.model.CreditDirection
import com.felix.bestfelix.domain.model.Income
import com.felix.bestfelix.domain.model.Repayment
import com.felix.bestfelix.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class CreditRow(
    val credit: Credit,
    val repayments: List<Repayment>,
    val remaining: Long,
    val settled: Boolean,
    val overdue: Boolean,
) {
    val paid: Long get() = repayments.sumOf { it.amount }
}

data class FinanceUiState(
    val monthIncome: Long = 0,
    val totalIOwe: Long = 0,
    val totalOwedToMe: Long = 0,
    val credits: List<CreditRow> = emptyList(),
    val incomeMonths: List<IncomeMonth> = emptyList(),
    val loading: Boolean = true,
)

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val dateProvider: DateProvider,
) : ViewModel() {

    val uiState: StateFlow<FinanceUiState> =
        combine(
            repository.observeCredits(),
            repository.observeIncomes(),
        ) { credits, incomes ->
            val today = dateProvider.today()
            FinanceUiState(
                monthIncome = IncomeCalculator.monthTotal(incomes, today),
                totalIOwe = CreditCalculator.totalIOwe(credits),
                totalOwedToMe = CreditCalculator.totalOwedToMe(credits),
                credits = credits
                    .map { credit ->
                        CreditRow(
                            credit = credit.credit,
                            repayments = credit.repayments.sortedByDescending { it.date },
                            remaining = CreditCalculator.remaining(credit),
                            settled = CreditCalculator.isSettled(credit),
                            overdue = CreditCalculator.isOverdue(credit, today),
                        )
                    }
                    // D'abord ce qui reste à régler, les retards en tête.
                    .sortedWith(
                        compareBy<CreditRow> { it.settled }
                            .thenByDescending { it.overdue }
                            .thenBy { it.credit.dueDate ?: LocalDate.MAX }
                            .thenBy { it.credit.counterparty.lowercase() },
                    ),
                incomeMonths = IncomeCalculator.groupByMonth(incomes),
                loading = false,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FinanceUiState(),
        )

    fun today(): LocalDate = dateProvider.today()

    fun createCredit(
        direction: CreditDirection,
        counterparty: String,
        amount: Long,
        reason: String?,
        dueDate: LocalDate?,
    ) {
        if (counterparty.isBlank() || amount <= 0L) return
        viewModelScope.launch {
            repository.createCredit(direction, counterparty, amount, reason, dueDate)
        }
    }

    fun updateCredit(credit: Credit) {
        if (credit.counterparty.isBlank() || credit.amount <= 0L) return
        viewModelScope.launch { repository.updateCredit(credit) }
    }

    fun deleteCredit(credit: Credit) {
        viewModelScope.launch { repository.deleteCredit(credit) }
    }

    fun addRepayment(creditId: Long, date: LocalDate, amount: Long) {
        if (amount <= 0L) return
        viewModelScope.launch { repository.addRepayment(creditId, date, amount) }
    }

    fun deleteRepayment(repayment: Repayment) {
        viewModelScope.launch { repository.deleteRepayment(repayment) }
    }

    fun createIncome(amount: Long, date: LocalDate, source: String) {
        if (amount <= 0L || source.isBlank()) return
        viewModelScope.launch { repository.createIncome(amount, date, source) }
    }

    fun updateIncome(income: Income) {
        if (income.amount <= 0L || income.source.isBlank()) return
        viewModelScope.launch { repository.updateIncome(income) }
    }

    fun deleteIncome(income: Income) {
        viewModelScope.launch { repository.deleteIncome(income) }
    }
}
