package com.felix.bestfelix.domain.reward

/** Toutes les valeurs du système de récompense. Rien de tout cela n'est écrit en dur dans l'UI. */
object RewardConstants {

    /** XP de base d'un jour validé, avant multiplicateur. */
    const val XP_PER_VALIDATED_DAY = 10

    /** Bonus d'une journée parfaite (toutes les habitudes du jour validées). */
    const val PERFECT_DAY_BONUS_XP = 20

    /** Tous les 7 jours de série, le multiplicateur gagne un cran. */
    const val MULTIPLIER_STEP_DAYS = 7

    /**
     * Un cran de multiplicateur = +0,5, soit +5 XP sur une base de 10.
     * On raisonne en XP entiers plutôt qu'en nombres à virgule : 10 × 1,5 = 15 = 10 + 5.
     */
    const val XP_PER_MULTIPLIER_STEP = 5

    /** Multiplicateur plafonné à 3, soit 4 crans (1 + 4 × 0,5). */
    const val MAX_MULTIPLIER_STEPS = 4

    /** Un joker gagné chaque fois que la série atteint un multiple de 7 jours. */
    const val JOKER_PERIOD_DAYS = 7

    /** Stock de jokers plafonné. */
    const val MAX_JOKERS = 2

    /** Il faut 50 × n × (n − 1) XP cumulés pour atteindre le niveau n. */
    const val LEVEL_FACTOR = 50

    /** Badges de record de série, en jours. */
    val BADGE_THRESHOLDS = listOf(3, 7, 21, 30, 66, 100)
}
