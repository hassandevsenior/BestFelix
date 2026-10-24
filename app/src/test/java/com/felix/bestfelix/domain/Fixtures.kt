package com.felix.bestfelix.domain

import com.felix.bestfelix.domain.model.DayStatus
import com.felix.bestfelix.domain.model.Habit
import com.felix.bestfelix.domain.model.HabitHistory
import com.felix.bestfelix.domain.model.QuitHabit
import com.felix.bestfelix.domain.model.QuitHabitHistory
import com.felix.bestfelix.domain.model.Relapse
import java.time.LocalDate

/** Date fixe : aucune règle ne doit dépendre du jour où les tests tournent. */
val TODAY: LocalDate = LocalDate.of(2026, 10, 23)

fun habit(
    id: Long = 1,
    name: String = "Sport",
    createdAt: LocalDate = TODAY.minusDays(120),
): Habit = Habit(id = id, name = name, why = null, createdAt = createdAt)

fun history(
    days: Map<LocalDate, DayStatus>,
    createdAt: LocalDate = TODAY.minusDays(120),
    id: Long = 1,
): HabitHistory = HabitHistory(habit(id = id, createdAt = createdAt), days)

/** [count] jours consécutifs se terminant à [endingAt] (incluse). */
fun consecutiveDays(endingAt: LocalDate, count: Int): List<LocalDate> =
    (0 until count).map { endingAt.minusDays(it.toLong()) }

fun validated(dates: List<LocalDate>): Map<LocalDate, DayStatus> =
    dates.associateWith { DayStatus.VALIDATED }

fun validated(vararg dates: LocalDate): Map<LocalDate, DayStatus> = validated(dates.toList())

fun savedByJoker(vararg dates: LocalDate): Map<LocalDate, DayStatus> =
    dates.associateWith { DayStatus.SAVED_BY_JOKER }

fun quitHistory(
    startDate: LocalDate,
    relapseDates: List<LocalDate> = emptyList(),
    name: String = "Cigarette",
): QuitHabitHistory = QuitHabitHistory(
    quitHabit = QuitHabit(id = 1, name = name, startDate = startDate),
    relapses = relapseDates.mapIndexed { index, date ->
        Relapse(id = index.toLong() + 1, date = date, note = null)
    },
)
