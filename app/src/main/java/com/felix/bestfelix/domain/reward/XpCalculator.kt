package com.felix.bestfelix.domain.reward

import com.felix.bestfelix.domain.model.DayStatus
import com.felix.bestfelix.domain.model.HabitHistory
import com.felix.bestfelix.domain.reward.RewardConstants.MAX_MULTIPLIER_STEPS
import com.felix.bestfelix.domain.reward.RewardConstants.MULTIPLIER_STEP_DAYS
import com.felix.bestfelix.domain.reward.RewardConstants.PERFECT_DAY_BONUS_XP
import com.felix.bestfelix.domain.reward.RewardConstants.XP_PER_MULTIPLIER_STEP
import com.felix.bestfelix.domain.reward.RewardConstants.XP_PER_VALIDATED_DAY
import java.time.LocalDate

object XpCalculator {

    /**
     * Multiplicateur d'un jour : `min(3 ; 1 + floor(série / 7) × 0,5)`, où la série est celle
     * qui se termine ce jour-là.
     */
    fun multiplierSteps(streakEndingThatDay: Int): Int =
        (streakEndingThatDay / MULTIPLIER_STEP_DAYS).coerceAtMost(MAX_MULTIPLIER_STEPS)

    fun multiplier(streakEndingThatDay: Int): Double =
        1.0 + multiplierSteps(streakEndingThatDay) * 0.5

    /** XP rapportés par un jour validé. Un jour sauvé par un joker ne rapporte rien. */
    fun xpForDay(history: HabitHistory, date: LocalDate): Int {
        if (!history.isValidated(date)) return 0
        val streak = StreakCalculator.streakEndingAt(history, date)
        return XP_PER_VALIDATED_DAY + multiplierSteps(streak) * XP_PER_MULTIPLIER_STEP
    }

    /** XP d'une habitude, recalculés depuis tout son historique. */
    fun xpForHabit(history: HabitHistory): Int =
        history.days.keys.sumOf { xpForDay(history, it) }

    /**
     * Une journée est parfaite si toutes les habitudes existant ce jour-là ont été validées
     * (un jour sauvé par un joker ne suffit pas) et qu'il en existait au moins une.
     */
    fun isPerfectDay(histories: List<HabitHistory>, date: LocalDate): Boolean {
        val existing = histories.filter { !it.habit.createdAt.isAfter(date) }
        return existing.isNotEmpty() && existing.all { it.isValidated(date) }
    }

    /** Nombre de journées parfaites sur tout l'historique. */
    fun perfectDays(histories: List<HabitHistory>): List<LocalDate> {
        val dates = histories.flatMap { history ->
            history.days.filterValues { it == DayStatus.VALIDATED }.keys
        }.distinct().sorted()
        return dates.filter { isPerfectDay(histories, it) }
    }

    /** XP totaux : les jours validés de chaque habitude, plus le bonus des journées parfaites. */
    fun totalXp(histories: List<HabitHistory>): Int {
        val habitXp = histories.sumOf { xpForHabit(it) }
        val bonus = perfectDays(histories).size * PERFECT_DAY_BONUS_XP
        return habitXp + bonus
    }
}
