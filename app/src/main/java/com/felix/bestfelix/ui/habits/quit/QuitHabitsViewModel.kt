package com.felix.bestfelix.ui.habits.quit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.bestfelix.core.time.DateProvider
import com.felix.bestfelix.domain.model.QuitHabit
import com.felix.bestfelix.domain.model.QuitHabitHistory
import com.felix.bestfelix.domain.pain.PainBalanceCalculator
import com.felix.bestfelix.domain.pain.PainState
import com.felix.bestfelix.domain.pain.RelapseEstimator
import com.felix.bestfelix.domain.pain.RelapsePreview
import com.felix.bestfelix.domain.repository.QuitHabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class QuitRow(
    val quitHabit: QuitHabit,
    val pain: PainState,
)

data class QuitHabitsUiState(
    val rows: List<QuitRow> = emptyList(),
    val loading: Boolean = true,
)

@HiltViewModel
class QuitHabitsViewModel @Inject constructor(
    private val repository: QuitHabitRepository,
    private val dateProvider: DateProvider,
) : ViewModel() {

    private var histories: List<QuitHabitHistory> = emptyList()

    /** Recalcule soldes et courbes au retour au premier plan (passage de minuit). */
    private val refreshTicker = MutableStateFlow(0)

    val uiState: StateFlow<QuitHabitsUiState> = combine(
        repository.observeHistories().onEach { histories = it },
        refreshTicker,
    ) { list, _ ->
        val today = dateProvider.today()
        QuitHabitsUiState(
            rows = list.map { QuitRow(it.quitHabit, PainBalanceCalculator.state(it, today)) },
            loading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = QuitHabitsUiState(),
    )

    fun refresh() {
        refreshTicker.value++
    }

    fun today(): LocalDate = dateProvider.today()

    /** Ce que coûtera la rechute, pour l'avertissement affiché avant confirmation. */
    fun previewRelapse(quitHabitId: Long, date: LocalDate): RelapsePreview? {
        val history = histories.firstOrNull { it.quitHabit.id == quitHabitId } ?: return null
        return RelapseEstimator.preview(history, date, dateProvider.today())
    }

    fun create(name: String, startDate: LocalDate) {
        if (name.isBlank()) return
        viewModelScope.launch { repository.create(name, startDate) }
    }

    fun update(quitHabit: QuitHabit) {
        if (quitHabit.name.isBlank()) return
        viewModelScope.launch { repository.update(quitHabit) }
    }

    fun delete(quitHabit: QuitHabit) {
        viewModelScope.launch { repository.delete(quitHabit) }
    }

    fun addRelapse(quitHabitId: Long, date: LocalDate, note: String?) {
        viewModelScope.launch { repository.addRelapse(quitHabitId, date, note) }
    }
}
