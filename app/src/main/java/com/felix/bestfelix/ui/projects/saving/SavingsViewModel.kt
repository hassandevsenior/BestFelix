package com.felix.bestfelix.ui.projects.saving

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.bestfelix.core.time.DateProvider
import com.felix.bestfelix.domain.finance.SavingCalculator
import com.felix.bestfelix.domain.finance.SavingProgress
import com.felix.bestfelix.domain.model.Deposit
import com.felix.bestfelix.domain.model.SavingGoal
import com.felix.bestfelix.domain.repository.SavingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class GoalRow(
    val goal: SavingGoal,
    val deposits: List<Deposit>,
    val progress: SavingProgress,
)

data class SavingsUiState(
    val rows: List<GoalRow> = emptyList(),
    val loading: Boolean = true,
)

@HiltViewModel
class SavingsViewModel @Inject constructor(
    private val repository: SavingRepository,
    private val dateProvider: DateProvider,
) : ViewModel() {

    val uiState: StateFlow<SavingsUiState> = repository.observeGoals()
        .map { goals ->
            val today = dateProvider.today()
            SavingsUiState(
                rows = goals
                    // Les objectifs avec échéance d'abord, la plus proche en tête.
                    .sortedWith(
                        compareBy<com.felix.bestfelix.domain.model.SavingGoalWithDeposits> {
                            it.goal.dueDate ?: LocalDate.MAX
                        }.thenBy { it.goal.title.lowercase() },
                    )
                    .map { goal ->
                        GoalRow(
                            goal = goal.goal,
                            deposits = goal.deposits.sortedByDescending { it.date },
                            progress = SavingCalculator.progress(goal, today),
                        )
                    },
                loading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SavingsUiState(),
        )

    fun today(): LocalDate = dateProvider.today()

    fun create(title: String, purpose: String?, targetAmount: Long, dueDate: LocalDate?) {
        if (title.isBlank() || targetAmount <= 0L) return
        viewModelScope.launch { repository.create(title, purpose, targetAmount, dueDate) }
    }

    fun update(goal: SavingGoal) {
        if (goal.title.isBlank() || goal.targetAmount <= 0L) return
        viewModelScope.launch { repository.update(goal) }
    }

    fun delete(goal: SavingGoal) {
        viewModelScope.launch { repository.delete(goal) }
    }

    fun addDeposit(goalId: Long, date: LocalDate, amount: Long) {
        if (amount <= 0L) return
        viewModelScope.launch { repository.addDeposit(goalId, date, amount) }
    }

    fun deleteDeposit(deposit: Deposit) {
        viewModelScope.launch { repository.deleteDeposit(deposit) }
    }
}
