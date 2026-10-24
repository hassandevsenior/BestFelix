package com.felix.bestfelix.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.bestfelix.core.time.DateProvider
import com.felix.bestfelix.data.prefs.SettingsStore
import com.felix.bestfelix.data.sample.SampleDataLoader
import com.felix.bestfelix.domain.finance.CreditCalculator
import com.felix.bestfelix.domain.finance.IncomeCalculator
import com.felix.bestfelix.domain.finance.SavingCalculator
import com.felix.bestfelix.domain.finance.SavingProgress
import com.felix.bestfelix.domain.model.HabitHistory
import com.felix.bestfelix.domain.model.Project
import com.felix.bestfelix.domain.model.SavingGoal
import com.felix.bestfelix.domain.pain.PainBalanceCalculator
import com.felix.bestfelix.domain.project.ProjectSorter
import com.felix.bestfelix.domain.repository.FinanceRepository
import com.felix.bestfelix.domain.repository.HabitRepository
import com.felix.bestfelix.domain.repository.ProjectRepository
import com.felix.bestfelix.domain.repository.QuitHabitRepository
import com.felix.bestfelix.domain.repository.SavingRepository
import com.felix.bestfelix.domain.reward.HabitToggle
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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TodayHabit(
    val id: Long,
    val name: String,
    val marked: Boolean,
    val streak: Int,
)

data class TodayQuitHabit(
    val id: Long,
    val name: String,
    val balance: Int,
    val bestBalance: Int,
    val heldStreak: Int,
) {
    val isDebt: Boolean get() = balance < 0
}

data class TodayProject(
    val project: Project,
    val overdue: Boolean,
)

data class TodayGoal(
    val goal: SavingGoal,
    val progress: SavingProgress,
)

data class TodayUiState(
    val loading: Boolean = true,
    val onboardingDone: Boolean = false,
    val date: LocalDate = LocalDate.ofEpochDay(0),
    val habits: List<TodayHabit> = emptyList(),
    val validatedCount: Int = 0,
    val level: LevelProgress = LevelCalculator.progressFor(0),
    val perfectToday: Boolean = false,
    val quitHabits: List<TodayQuitHabit> = emptyList(),
    val projects: List<TodayProject> = emptyList(),
    val goals: List<TodayGoal> = emptyList(),
    val monthIncome: Long = 0,
    val totalIOwe: Long = 0,
    val totalOwedToMe: Long = 0,
) {
    val habitCount: Int get() = habits.size
}

sealed interface TodayEvent {
    data class XpGained(val amount: Int, val perfectDay: Boolean) : TodayEvent
    data class JokersUsed(val count: Int) : TodayEvent
}

/** Les deux moitiés « habitudes » du tableau de bord, regroupées pour limiter les combine. */
private data class HabitsPart(
    val histories: List<HabitHistory>,
    val habits: List<TodayHabit>,
    val validatedCount: Int,
    val level: LevelProgress,
    val perfectToday: Boolean,
    val quitHabits: List<TodayQuitHabit>,
)

private data class WorkPart(
    val projects: List<TodayProject>,
    val goals: List<TodayGoal>,
)

private data class MoneyPart(
    val monthIncome: Long,
    val totalIOwe: Long,
    val totalOwedToMe: Long,
)

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    quitHabitRepository: QuitHabitRepository,
    projectRepository: ProjectRepository,
    savingRepository: SavingRepository,
    financeRepository: FinanceRepository,
    private val settingsStore: SettingsStore,
    private val sampleDataLoader: SampleDataLoader,
    private val dateProvider: DateProvider,
) : ViewModel() {

    private val _events = MutableSharedFlow<TodayEvent>(
        extraBufferCapacity = 4,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events = _events.asSharedFlow()

    private var histories: List<HabitHistory> = emptyList()

    /**
     * Incrémenté à chaque retour au premier plan : sans lui, « aujourd'hui » resterait figé
     * sur la date de la dernière modification de données (passage de minuit, appli ouverte).
     */
    private val refreshTicker = MutableStateFlow(0)

    private val habitsPart = combine(
        habitRepository.observeHistories(),
        quitHabitRepository.observeHistories(),
    ) { habitHistories, quitHistories ->
        val today = dateProvider.today()
        HabitsPart(
            histories = habitHistories,
            habits = habitHistories.map { history ->
                TodayHabit(
                    id = history.habit.id,
                    name = history.habit.name,
                    marked = history.countsForStreak(today),
                    streak = StreakCalculator.currentStreak(history, today),
                )
            },
            validatedCount = habitHistories.count { it.countsForStreak(today) },
            level = LevelCalculator.progressFor(XpCalculator.totalXp(habitHistories)),
            perfectToday = XpCalculator.isPerfectDay(habitHistories, today),
            quitHabits = quitHistories.map { history ->
                val pain = PainBalanceCalculator.state(history, today)
                TodayQuitHabit(
                    id = history.quitHabit.id,
                    name = history.quitHabit.name,
                    balance = pain.balance,
                    bestBalance = pain.bestBalance,
                    heldStreak = pain.heldStreak,
                )
            },
        )
    }.onEach { histories = it.histories }

    private val workPart = combine(
        projectRepository.observeProjects(),
        savingRepository.observeGoals(),
    ) { projects, goals ->
        val today = dateProvider.today()
        WorkPart(
            projects = ProjectSorter.topActive(projects).map {
                TodayProject(it, ProjectSorter.isOverdue(it, today))
            },
            goals = goals.map { TodayGoal(it.goal, SavingCalculator.progress(it, today)) },
        )
    }

    private val moneyPart = combine(
        financeRepository.observeCredits(),
        financeRepository.observeIncomes(),
    ) { credits, incomes ->
        MoneyPart(
            monthIncome = IncomeCalculator.monthTotal(incomes, dateProvider.today()),
            totalIOwe = CreditCalculator.totalIOwe(credits),
            totalOwedToMe = CreditCalculator.totalOwedToMe(credits),
        )
    }

    val uiState: StateFlow<TodayUiState> = combine(
        habitsPart,
        workPart,
        moneyPart,
        settingsStore.onboardingDone,
        refreshTicker,
    ) { habitsState, work, money, onboardingDone, _ ->
        TodayUiState(
            loading = false,
            onboardingDone = onboardingDone,
            date = dateProvider.today(),
            habits = habitsState.habits,
            validatedCount = habitsState.validatedCount,
            level = habitsState.level,
            perfectToday = habitsState.perfectToday,
            quitHabits = habitsState.quitHabits,
            projects = work.projects,
            goals = work.goals,
            monthIncome = money.monthIncome,
            totalIOwe = money.totalIOwe,
            totalOwedToMe = money.totalOwedToMe,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TodayUiState(),
    )

    init {
        viewModelScope.launch {
            val saved = habitRepository.applyAutomaticJokers(dateProvider.today())
            if (saved > 0) _events.emit(TodayEvent.JokersUsed(saved))
        }
    }

    /** À appeler quand l'appli revient au premier plan. */
    fun refresh() {
        refreshTicker.value++
    }

    fun toggleToday(habitId: Long) {
        val today = dateProvider.today()
        val outcome = HabitToggle.simulate(histories, habitId, today) ?: return
        viewModelScope.launch {
            habitRepository.setValidated(habitId, today, outcome.willBeMarked)
            if (outcome.xpGained > 0) {
                _events.emit(TodayEvent.XpGained(outcome.xpGained, outcome.becomesPerfectDay))
            }
        }
    }

    /** Premier lancement : « Charger des exemples ». */
    fun loadSamples() {
        viewModelScope.launch {
            sampleDataLoader.load(dateProvider.today())
            settingsStore.setOnboardingDone(true)
        }
    }

    /** Premier lancement : « Partir de zéro ». */
    fun startEmpty() {
        viewModelScope.launch { settingsStore.setOnboardingDone(true) }
    }
}
