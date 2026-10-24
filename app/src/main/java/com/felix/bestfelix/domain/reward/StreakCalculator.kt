package com.felix.bestfelix.domain.reward

import com.felix.bestfelix.domain.model.HabitHistory
import java.time.LocalDate

/** Séries : uniquement des jours consécutifs, jamais de calcul en millisecondes. */
object StreakCalculator {

    /**
     * Série en cours : jours consécutifs validés ou sauvés par un joker, comptés depuis
     * aujourd'hui, ou depuis hier si aujourd'hui n'est pas encore validé.
     */
    fun currentStreak(history: HabitHistory, today: LocalDate): Int {
        val start = if (history.countsForStreak(today)) today else today.minusDays(1)
        return streakEndingAt(history, start)
    }

    /**
     * Longueur de la série qui se termine à [date] (incluse). Sert au multiplicateur d'XP :
     * le 7ᵉ jour consécutif rapporte déjà ×1,5.
     */
    fun streakEndingAt(history: HabitHistory, date: LocalDate): Int {
        var cursor = date
        var length = 0
        while (history.countsForStreak(cursor)) {
            length++
            cursor = cursor.minusDays(1)
        }
        return length
    }

    /** Record de série sur tout l'historique : c'est lui qui débloque les badges. */
    fun bestStreak(history: HabitHistory): Int {
        if (history.days.isEmpty()) return 0
        val dates = history.days.keys.sorted()
        var best = 1
        var run = 1
        for (index in 1 until dates.size) {
            run = if (dates[index] == dates[index - 1].plusDays(1)) run + 1 else 1
            if (run > best) best = run
        }
        return best
    }
}
