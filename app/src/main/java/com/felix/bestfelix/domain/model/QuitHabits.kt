package com.felix.bestfelix.domain.model

import java.time.LocalDate

/** Habitude à abandonner. */
data class QuitHabit(
    val id: Long,
    val name: String,
    val startDate: LocalDate,
    val position: Int = 0,
)

data class Relapse(
    val id: Long,
    val date: LocalDate,
    val note: String?,
)

data class QuitHabitHistory(
    val quitHabit: QuitHabit,
    val relapses: List<Relapse>,
) {
    /** Plusieurs rechutes le même jour ne comptent que pour un seul jour non tenu. */
    val brokenDays: Set<LocalDate> = relapses.map { it.date }.toSet()
}
