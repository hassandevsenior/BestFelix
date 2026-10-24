package com.felix.bestfelix.domain.pain

/**
 * Le barème de la douleur. Ces deux valeurs ne sont jamais écrites en dur dans l'UI :
 * un jour non tenu efface exactement 7 jours d'effort.
 */
object PainConstants {

    /** Chaque jour tenu (terminé sans rechute) rapporte +5 XP. */
    const val XP_PER_HELD_DAY = 5

    /** Chaque jour non tenu efface 35 XP, soit 7 × 5. */
    const val XP_PENALTY_PER_BROKEN_DAY = 35

    /** Nombre de jours d'effort effacés par une rechute : 35 / 5. */
    const val DAYS_ERASED_BY_RELAPSE = XP_PENALTY_PER_BROKEN_DAY / XP_PER_HELD_DAY
}
