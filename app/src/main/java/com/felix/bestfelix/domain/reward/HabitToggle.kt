package com.felix.bestfelix.domain.reward

import com.felix.bestfelix.domain.model.DayStatus
import com.felix.bestfelix.domain.model.HabitHistory
import java.time.LocalDate

/** Ce que produirait un tap sur un jour, calculé avant d'écrire quoi que ce soit en base. */
data class ToggleOutcome(
    val willBeMarked: Boolean,
    val xpGained: Int,
    val becomesPerfectDay: Boolean,
)

object HabitToggle {

    /**
     * Simule la validation (ou l'annulation) d'un jour sur tout l'historique, pour annoncer
     * l'XP réellement gagné. Renvoie null si l'habitude n'existe pas ou n'existait pas encore
     * à cette date.
     */
    fun simulate(
        histories: List<HabitHistory>,
        habitId: Long,
        date: LocalDate,
    ): ToggleOutcome? {
        val history = histories.firstOrNull { it.habit.id == habitId } ?: return null
        if (history.habit.createdAt.isAfter(date)) return null

        val wasMarked = history.countsForStreak(date)
        val simulated = histories.map { candidate ->
            if (candidate.habit.id != habitId) {
                candidate
            } else {
                val days = if (wasMarked) {
                    candidate.days - date
                } else {
                    candidate.days + (date to DayStatus.VALIDATED)
                }
                candidate.copy(days = days)
            }
        }

        return ToggleOutcome(
            willBeMarked = !wasMarked,
            xpGained = XpCalculator.totalXp(simulated) - XpCalculator.totalXp(histories),
            becomesPerfectDay = !XpCalculator.isPerfectDay(histories, date) &&
                XpCalculator.isPerfectDay(simulated, date),
        )
    }
}
