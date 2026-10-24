package com.felix.bestfelix.domain.model

/** Statut d'un jour pour une habitude à prendre. Un jour absent de la base = non validé. */
enum class DayStatus {
    /** Validé par moi. Rapporte de l'XP. */
    VALIDATED,

    /** Sauvé par un joker : garde la série, mais ne rapporte aucun XP. */
    SAVED_BY_JOKER,
}

enum class Priority { HIGH, MEDIUM, LOW }

enum class ProjectStatus { ACTIVE, PAUSED, DONE }

enum class CreditDirection {
    /** Je dois de l'argent. */
    I_OWE,

    /** On me doit de l'argent. */
    OWED_TO_ME,
}

enum class NoteCategory {
    GOOD_HABITS,
    QUIT_HABITS,
    FINANCE,
    PROJECTS,
    GENERAL,
}
