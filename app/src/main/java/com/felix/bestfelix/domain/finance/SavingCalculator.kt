package com.felix.bestfelix.domain.finance

import com.felix.bestfelix.domain.model.SavingGoalWithDeposits
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class SavingProgress(
    val saved: Long,
    val target: Long,
    val remaining: Long,
    /** Entre 0 et 1, pour la barre de progression. */
    val fraction: Float,
    val percent: Int,
    /** Montant à mettre de côté chaque mois pour tenir l'échéance, si elle existe. */
    val monthlyNeeded: Long?,
    val isReached: Boolean,
)

object SavingCalculator {

    fun saved(goal: SavingGoalWithDeposits): Long = goal.deposits.sumOf { it.amount }

    /**
     * Mois qui restent avant l'échéance, arrondis au mois supérieur (minimum 1).
     * Null si l'échéance est déjà passée : il n'y a plus de mensualité à calculer.
     */
    fun monthsLeft(today: LocalDate, dueDate: LocalDate): Int? {
        if (dueDate.isBefore(today)) return null
        val wholeMonths = ChronoUnit.MONTHS.between(today, dueDate)
        val hasRemainder = today.plusMonths(wholeMonths).isBefore(dueDate)
        return (wholeMonths + if (hasRemainder) 1 else 0).toInt().coerceAtLeast(1)
    }

    /** Montant mensuel nécessaire : le reste à épargner étalé sur les mois restants. */
    fun monthlyNeeded(remaining: Long, today: LocalDate, dueDate: LocalDate?): Long? {
        if (remaining <= 0L || dueDate == null) return null
        val months = monthsLeft(today, dueDate) ?: return null
        // Division entière arrondie au supérieur : mieux vaut viser un peu haut que manquer.
        return (remaining + months - 1) / months
    }

    fun progress(goal: SavingGoalWithDeposits, today: LocalDate): SavingProgress {
        val saved = saved(goal)
        val target = goal.goal.targetAmount
        val remaining = (target - saved).coerceAtLeast(0)
        val fraction = if (target <= 0L) 0f else (saved.toFloat() / target).coerceIn(0f, 1f)
        return SavingProgress(
            saved = saved,
            target = target,
            remaining = remaining,
            fraction = fraction,
            percent = (fraction * 100).toInt(),
            monthlyNeeded = monthlyNeeded(remaining, today, goal.goal.dueDate),
            isReached = remaining == 0L && target > 0L,
        )
    }
}
