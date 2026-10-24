package com.felix.bestfelix.domain.repository

import com.felix.bestfelix.domain.model.Habit
import com.felix.bestfelix.domain.model.HabitHistory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HabitRepository {

    /** Habitudes à prendre avec tout leur historique : la seule source des XP et des séries. */
    fun observeHistories(): Flow<List<HabitHistory>>

    suspend fun createHabit(name: String, why: String?, createdAt: LocalDate): Long

    suspend fun updateHabit(habit: Habit)

    suspend fun deleteHabit(habit: Habit)

    /** Valide ou dévalide un jour (les 6 jours précédents sont corrigeables). */
    suspend fun setValidated(habitId: Long, date: LocalDate, validated: Boolean)

    /**
     * Au lancement : consomme un joker pour sauver hier quand c'est possible.
     * Renvoie le nombre d'habitudes sauvées.
     */
    suspend fun applyAutomaticJokers(today: LocalDate): Int
}
