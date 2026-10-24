package com.felix.bestfelix.domain.repository

import com.felix.bestfelix.domain.model.QuitHabit
import com.felix.bestfelix.domain.model.QuitHabitHistory
import com.felix.bestfelix.domain.model.Relapse
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface QuitHabitRepository {

    fun observeHistories(): Flow<List<QuitHabitHistory>>

    suspend fun create(name: String, startDate: LocalDate): Long

    suspend fun update(quitHabit: QuitHabit)

    suspend fun delete(quitHabit: QuitHabit)

    /**
     * Enregistre une rechute. Si une note est saisie, elle est aussi ajoutée au bloc-notes
     * dans la rubrique « Habitudes à abandonner ».
     */
    suspend fun addRelapse(quitHabitId: Long, date: LocalDate, note: String?)

    suspend fun deleteRelapse(relapse: Relapse)
}
