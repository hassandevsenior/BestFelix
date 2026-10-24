package com.felix.bestfelix.domain.pain

import com.felix.bestfelix.domain.model.QuitHabitHistory
import com.felix.bestfelix.domain.model.Relapse
import com.felix.bestfelix.domain.pain.PainConstants.XP_PENALTY_PER_BROKEN_DAY
import com.felix.bestfelix.domain.pain.PainConstants.XP_PER_HELD_DAY
import java.time.LocalDate
import kotlin.math.ceil

/**
 * Ce que la bottom sheet affiche **avant** de confirmer une rechute : le prix à payer,
 * en XP et en jours.
 */
data class RelapsePreview(
    val date: LocalDate,
    /** Ce jour-là compte déjà comme non tenu : pas de pénalité supplémentaire. */
    val alreadyBroken: Boolean,
    val balanceBefore: Int,
    val balanceAfter: Int,
    val bestBalance: Int,
    /** Jours tenus nécessaires pour revenir au record, ou null si le record reste battu. */
    val daysToRecord: Int?,
) {
    /**
     * Ce que la rechute coûte réellement. Sur aujourd'hui, c'est exactement
     * [XP_PENALTY_PER_BROKEN_DAY] ; sur un jour passé déjà tenu, c'est 40 XP, parce que ce
     * jour perd aussi son +5 de jour tenu.
     */
    val penalty: Int get() = balanceBefore - balanceAfter
}

object RelapseEstimator {

    /** Jours tenus nécessaires pour remonter de [balance] à [best] : ceil((best − balance) / 5). */
    fun daysToRecord(best: Int, balance: Int): Int? {
        if (balance >= best) return null
        return ceil((best - balance).toDouble() / XP_PER_HELD_DAY).toInt()
    }

    fun preview(
        history: QuitHabitHistory,
        date: LocalDate,
        today: LocalDate,
    ): RelapsePreview {
        val alreadyBroken = date in history.brokenDays
        val before = PainBalanceCalculator.balance(history, today)
        val best = PainBalanceCalculator.bestBalance(history, today)

        val after = if (alreadyBroken) {
            before
        } else {
            val simulated = history.copy(
                relapses = history.relapses + Relapse(id = 0, date = date, note = null),
            )
            PainBalanceCalculator.balance(simulated, today)
        }

        return RelapsePreview(
            date = date,
            alreadyBroken = alreadyBroken,
            balanceBefore = before,
            balanceAfter = after,
            bestBalance = best,
            daysToRecord = daysToRecord(best, after),
        )
    }
}
