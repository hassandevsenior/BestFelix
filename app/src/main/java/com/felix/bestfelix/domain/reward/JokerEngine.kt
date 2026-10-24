package com.felix.bestfelix.domain.reward

import com.felix.bestfelix.domain.model.DayStatus
import com.felix.bestfelix.domain.model.HabitHistory
import com.felix.bestfelix.domain.reward.RewardConstants.JOKER_PERIOD_DAYS
import com.felix.bestfelix.domain.reward.RewardConstants.MAX_JOKERS
import java.time.LocalDate

object JokerEngine {

    /**
     * Stock de jokers, rejoué depuis l'historique : +1 chaque fois que la série atteint un
     * multiple de 7 jours (plafonné à [MAX_JOKERS], un seul gain par jour), −1 pour chaque
     * jour sauvé par un joker. Rien n'est stocké, donc corriger un jour passé corrige le stock.
     */
    fun stock(history: HabitHistory, today: LocalDate): Int {
        val firstDay = history.days.keys.minOrNull()
        val start = listOfNotNull(history.habit.createdAt, firstDay).minOrNull() ?: return 0

        var stock = 0
        var streak = 0
        var cursor = start
        while (!cursor.isAfter(today)) {
            val status = history.days[cursor]
            if (status == null) {
                streak = 0
            } else {
                if (status == DayStatus.SAVED_BY_JOKER) stock = (stock - 1).coerceAtLeast(0)
                streak++
                if (streak % JOKER_PERIOD_DAYS == 0) stock = (stock + 1).coerceAtMost(MAX_JOKERS)
            }
            cursor = cursor.plusDays(1)
        }
        return stock
    }

    /**
     * Au lancement de l'appli : si hier n'est pas validé, qu'avant-hier l'est et qu'il reste
     * un joker, hier est sauvé automatiquement.
     */
    fun shouldSaveYesterday(history: HabitHistory, today: LocalDate): Boolean {
        val yesterday = today.minusDays(1)
        val dayBefore = today.minusDays(2)
        if (history.countsForStreak(yesterday)) return false
        if (!history.countsForStreak(dayBefore)) return false
        if (history.habit.createdAt.isAfter(yesterday)) return false
        return stock(history, today) >= 1
    }
}
