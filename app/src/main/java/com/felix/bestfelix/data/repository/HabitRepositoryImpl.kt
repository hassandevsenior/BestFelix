package com.felix.bestfelix.data.repository

import com.felix.bestfelix.data.db.dao.HabitDao
import com.felix.bestfelix.data.db.entity.HabitDayEntity
import com.felix.bestfelix.data.mapper.toDomain
import com.felix.bestfelix.data.mapper.toEntity
import com.felix.bestfelix.domain.model.DayStatus
import com.felix.bestfelix.domain.model.Habit
import com.felix.bestfelix.domain.model.HabitHistory
import com.felix.bestfelix.domain.repository.HabitRepository
import com.felix.bestfelix.domain.reward.JokerEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val dao: HabitDao,
) : HabitRepository {

    override fun observeHistories(): Flow<List<HabitHistory>> =
        combine(dao.observeHabits(), dao.observeAllDays()) { habits, days ->
            val daysByHabit = days.groupBy { it.habitId }
            habits.map { entity ->
                HabitHistory(
                    habit = entity.toDomain(),
                    days = daysByHabit[entity.id]
                        ?.associate { it.date to it.status }
                        .orEmpty(),
                )
            }
        }

    override suspend fun createHabit(name: String, why: String?, createdAt: LocalDate): Long =
        dao.upsertHabit(
            Habit(
                id = 0,
                name = name.trim(),
                why = why?.trim()?.takeIf { it.isNotEmpty() },
                createdAt = createdAt,
                position = dao.nextPosition(),
            ).toEntity(),
        )

    override suspend fun updateHabit(habit: Habit) {
        dao.upsertHabit(
            habit.copy(
                name = habit.name.trim(),
                why = habit.why?.trim()?.takeIf { it.isNotEmpty() },
            ).toEntity(),
        )
    }

    override suspend fun deleteHabit(habit: Habit) {
        dao.deleteHabit(habit.toEntity())
    }

    override suspend fun setValidated(habitId: Long, date: LocalDate, validated: Boolean) {
        if (validated) {
            dao.upsertDay(HabitDayEntity(habitId = habitId, date = date, status = DayStatus.VALIDATED))
        } else {
            dao.deleteDay(habitId = habitId, date = date)
        }
    }

    override suspend fun applyAutomaticJokers(today: LocalDate): Int {
        val habits = dao.getHabits()
        val daysByHabit = dao.getAllDays().groupBy { it.habitId }
        var saved = 0
        habits.forEach { entity ->
            val history = HabitHistory(
                habit = entity.toDomain(),
                days = daysByHabit[entity.id]?.associate { it.date to it.status }.orEmpty(),
            )
            if (JokerEngine.shouldSaveYesterday(history, today)) {
                dao.upsertDay(
                    HabitDayEntity(
                        habitId = entity.id,
                        date = today.minusDays(1),
                        status = DayStatus.SAVED_BY_JOKER,
                    ),
                )
                saved++
            }
        }
        return saved
    }
}
