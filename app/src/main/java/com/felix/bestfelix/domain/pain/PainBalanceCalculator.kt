package com.felix.bestfelix.domain.pain

import com.felix.bestfelix.domain.model.QuitHabitHistory
import com.felix.bestfelix.domain.pain.PainConstants.XP_PENALTY_PER_BROKEN_DAY
import com.felix.bestfelix.domain.pain.PainConstants.XP_PER_HELD_DAY
import java.time.LocalDate

/** Un point de la courbe : le solde à la fin de la journée [date]. */
data class BalancePoint(
    val date: LocalDate,
    val balance: Int,
)

/** État complet d'une habitude à abandonner, entièrement recalculé depuis l'historique. */
data class PainState(
    val balance: Int,
    val bestBalance: Int,
    val heldStreak: Int,
    val brokenDaysCount: Int,
    val series: List<BalancePoint>,
) {
    val isDebt: Boolean get() = balance < 0
}

object PainBalanceCalculator {

    /**
     * Solde en XP. Un jour tenu ne compte qu'une fois la journée finie : aujourd'hui ne
     * rapporte rien. Une rechute, elle, pénalise dès le jour où elle a lieu.
     */
    fun balance(history: QuitHabitHistory, today: LocalDate): Int {
        val start = history.quitHabit.startDate
        if (start.isAfter(today)) return 0
        val broken = history.brokenDays.count { !it.isBefore(start) && !it.isAfter(today) }
        val heldFinished = countHeldFinishedDays(history, today)
        return heldFinished * XP_PER_HELD_DAY - broken * XP_PENALTY_PER_BROKEN_DAY
    }

    /** Jours tenus déjà terminés, de la date de début à hier inclus. */
    fun countHeldFinishedDays(history: QuitHabitHistory, today: LocalDate): Int {
        val start = history.quitHabit.startDate
        var cursor = start
        var held = 0
        while (cursor.isBefore(today)) {
            if (cursor !in history.brokenDays) held++
            cursor = cursor.plusDays(1)
        }
        return held
    }

    /**
     * Jours tenus d'affilée : jours terminés sans rechute en remontant depuis hier.
     * Une rechute aujourd'hui remet le compteur à zéro.
     */
    fun heldStreak(history: QuitHabitHistory, today: LocalDate): Int {
        if (today in history.brokenDays) return 0
        val start = history.quitHabit.startDate
        var cursor = today.minusDays(1)
        var streak = 0
        while (!cursor.isBefore(start) && cursor !in history.brokenDays) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    /** Courbe du solde, un point par jour, de la date de début à aujourd'hui. */
    fun series(history: QuitHabitHistory, today: LocalDate): List<BalancePoint> {
        val start = history.quitHabit.startDate
        if (start.isAfter(today)) return emptyList()
        val points = mutableListOf<BalancePoint>()
        var cursor = start
        var running = 0
        while (!cursor.isAfter(today)) {
            running += when {
                cursor in history.brokenDays -> -XP_PENALTY_PER_BROKEN_DAY
                // Aujourd'hui n'est pas fini : il ne rapporte pas encore ses +5 XP.
                cursor.isBefore(today) -> XP_PER_HELD_DAY
                else -> 0
            }
            points += BalancePoint(cursor, running)
            cursor = cursor.plusDays(1)
        }
        return points
    }

    /** Meilleur solde jamais atteint, recalculé depuis la courbe (jamais négatif au départ). */
    fun bestBalance(history: QuitHabitHistory, today: LocalDate): Int =
        series(history, today).maxOfOrNull { it.balance }?.coerceAtLeast(0) ?: 0

    fun state(history: QuitHabitHistory, today: LocalDate): PainState {
        val points = series(history, today)
        return PainState(
            balance = balance(history, today),
            bestBalance = points.maxOfOrNull { it.balance }?.coerceAtLeast(0) ?: 0,
            heldStreak = heldStreak(history, today),
            brokenDaysCount = history.brokenDays.count {
                !it.isBefore(history.quitHabit.startDate) && !it.isAfter(today)
            },
            series = points,
        )
    }
}
