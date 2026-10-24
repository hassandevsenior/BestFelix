package com.felix.bestfelix.ui.habits.good

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.bestfelix.core.time.DateProvider
import com.felix.bestfelix.domain.model.DayStatus
import com.felix.bestfelix.domain.model.Habit
import com.felix.bestfelix.domain.model.HabitHistory
import com.felix.bestfelix.domain.repository.HabitRepository
import com.felix.bestfelix.domain.reward.Badge
import com.felix.bestfelix.domain.reward.BadgeRules
import com.felix.bestfelix.domain.reward.HabitToggle
import com.felix.bestfelix.domain.reward.JokerEngine
import com.felix.bestfelix.domain.reward.LevelCalculator
import com.felix.bestfelix.domain.reward.LevelProgress
import com.felix.bestfelix.domain.reward.StreakCalculator
import com.felix.bestfelix.domain.reward.XpCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/** Une pastille de la ligne des 7 jours. */
data class DayCell(
    val date: LocalDate,
    val initial: String,
    val status: DayStatus?,
    val isToday: Boolean,
    /** Faux pour un jour antérieur à la création de l'habitude. */
    val editable: Boolean,
)

data class HabitRow(
    val habit: Habit,
    val validatedToday: Boolean,
    val streak: Int,
    val bestStreak: Int,
    val jokers: Int,
    val week: List<DayCell>,
)

data class GoodHabitsUiState(
    val rows: List<HabitRow> = emptyList(),
    val level: LevelProgress = LevelCalculator.progressFor(0),
    val badges: List<Badge> = BadgeRules.badges(0),
    val bestStreakOverall: Int = 0,
    val validatedTodayCount: Int = 0,
    val perfectToday: Boolean = false,
    val loading: Boolean = true,
) {
    val habitCount: Int get() = rows.size
}

sealed interface GoodHabitEvent {
    /** Retour visuel après une validation. */
    data class XpGained(val amount: Int, val perfectDay: Boolean) : GoodHabitEvent
    data class JokersUsed(val count: Int) : GoodHabitEvent
}

@HiltViewModel
class GoodHabitsViewModel @Inject constructor(
    private val repository: HabitRepository,
    private val dateProvider: DateProvider,
) : ViewModel() {

    private val _events = MutableSharedFlow<GoodHabitEvent>(
        extraBufferCapacity = 4,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events = _events.asSharedFlow()

    /** Dernier historique connu : sert à calculer l'XP gagné avant même d'écrire en base. */
    private var histories: List<HabitHistory> = emptyList()

    /** Recalcule la fenêtre de 7 jours au retour au premier plan (passage de minuit). */
    private val refreshTicker = MutableStateFlow(0)

    val uiState: StateFlow<GoodHabitsUiState> = combine(
        repository.observeHistories().onEach { histories = it },
        refreshTicker,
    ) { histories, _ -> toState(histories, dateProvider.today()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GoodHabitsUiState(),
        )

    init {
        viewModelScope.launch {
            val saved = repository.applyAutomaticJokers(dateProvider.today())
            if (saved > 0) _events.emit(GoodHabitEvent.JokersUsed(saved))
        }
    }

    /** Valide ou corrige un jour, puis annonce l'XP réellement gagné. */
    fun toggleDay(habitId: Long, date: LocalDate) {
        val outcome = HabitToggle.simulate(histories, habitId, date) ?: return
        viewModelScope.launch {
            repository.setValidated(habitId, date, outcome.willBeMarked)
            if (outcome.xpGained > 0) {
                _events.emit(GoodHabitEvent.XpGained(outcome.xpGained, outcome.becomesPerfectDay))
            }
        }
    }

    fun refresh() {
        refreshTicker.value++
    }

    fun createHabit(name: String, why: String?) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.createHabit(name, why, dateProvider.today())
        }
    }

    fun updateHabit(habit: Habit) {
        if (habit.name.isBlank()) return
        viewModelScope.launch { repository.updateHabit(habit) }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch { repository.deleteHabit(habit) }
    }

    private fun toState(histories: List<HabitHistory>, today: LocalDate): GoodHabitsUiState {
        val rows = histories.map { history -> toRow(history, today) }
        val totalXp = XpCalculator.totalXp(histories)
        val bestStreak = histories.maxOfOrNull { StreakCalculator.bestStreak(it) } ?: 0
        return GoodHabitsUiState(
            rows = rows,
            level = LevelCalculator.progressFor(totalXp),
            badges = BadgeRules.badges(bestStreak),
            bestStreakOverall = bestStreak,
            validatedTodayCount = rows.count { it.validatedToday },
            perfectToday = XpCalculator.isPerfectDay(histories, today),
            loading = false,
        )
    }

    private fun toRow(history: HabitHistory, today: LocalDate): HabitRow {
        // Fenêtre glissante : aujourd'hui à droite, les 6 jours corrigeables à sa gauche.
        val week = (6 downTo 0).map { offset ->
            val date = today.minusDays(offset.toLong())
            DayCell(
                date = date,
                initial = com.felix.bestfelix.core.format.DateFormatFr.weekdayInitial(date),
                status = history.days[date],
                isToday = date == today,
                editable = !history.habit.createdAt.isAfter(date),
            )
        }
        return HabitRow(
            habit = history.habit,
            validatedToday = history.countsForStreak(today),
            streak = StreakCalculator.currentStreak(history, today),
            bestStreak = StreakCalculator.bestStreak(history),
            jokers = JokerEngine.stock(history, today),
            week = week,
        )
    }
}
