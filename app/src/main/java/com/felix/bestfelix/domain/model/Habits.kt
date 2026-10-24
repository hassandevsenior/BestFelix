package com.felix.bestfelix.domain.model

import java.time.LocalDate

/** Habitude à prendre. */
data class Habit(
    val id: Long,
    val name: String,
    val why: String?,
    val createdAt: LocalDate,
    val position: Int = 0,
)

/**
 * Historique complet d'une habitude. Tout (série, XP, jokers, badges) se déduit de [days] :
 * aucun compteur n'est stocké, pour qu'une correction d'un jour passé donne un résultat juste.
 */
data class HabitHistory(
    val habit: Habit,
    val days: Map<LocalDate, DayStatus>,
) {
    /** Un jour compte pour la série qu'il soit validé ou sauvé par un joker. */
    fun countsForStreak(date: LocalDate): Boolean = days.containsKey(date)

    fun isValidated(date: LocalDate): Boolean = days[date] == DayStatus.VALIDATED
}
