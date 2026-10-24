package com.felix.bestfelix.data.repository

import com.felix.bestfelix.core.format.DateFormatFr
import com.felix.bestfelix.data.db.dao.QuitHabitDao
import com.felix.bestfelix.data.db.entity.RelapseEntity
import com.felix.bestfelix.data.mapper.toDomain
import com.felix.bestfelix.data.mapper.toEntity
import com.felix.bestfelix.domain.model.NoteCategory
import com.felix.bestfelix.domain.model.QuitHabit
import com.felix.bestfelix.domain.model.QuitHabitHistory
import com.felix.bestfelix.domain.model.Relapse
import com.felix.bestfelix.domain.repository.NoteRepository
import com.felix.bestfelix.domain.repository.QuitHabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuitHabitRepositoryImpl @Inject constructor(
    private val dao: QuitHabitDao,
    private val noteRepository: NoteRepository,
) : QuitHabitRepository {

    override fun observeHistories(): Flow<List<QuitHabitHistory>> =
        combine(dao.observeQuitHabits(), dao.observeAllRelapses()) { habits, relapses ->
            val byHabit = relapses.groupBy { it.quitHabitId }
            habits.map { entity ->
                QuitHabitHistory(
                    quitHabit = entity.toDomain(),
                    relapses = byHabit[entity.id]?.map { it.toDomain() }.orEmpty(),
                )
            }
        }

    override suspend fun create(name: String, startDate: LocalDate): Long =
        dao.upsertQuitHabit(
            QuitHabit(
                id = 0,
                name = name.trim(),
                startDate = startDate,
                position = dao.nextPosition(),
            ).toEntity(),
        )

    override suspend fun update(quitHabit: QuitHabit) {
        dao.upsertQuitHabit(quitHabit.copy(name = quitHabit.name.trim()).toEntity())
    }

    override suspend fun delete(quitHabit: QuitHabit) {
        dao.deleteQuitHabit(quitHabit.toEntity())
    }

    override suspend fun addRelapse(quitHabitId: Long, date: LocalDate, note: String?) {
        val cleanNote = note?.trim()?.takeIf { it.isNotEmpty() }
        dao.upsertRelapse(
            RelapseEntity(quitHabitId = quitHabitId, date = date, note = cleanNote),
        )
        if (cleanNote != null) {
            val name = dao.getQuitHabits().firstOrNull { it.id == quitHabitId }?.name
            noteRepository.create(
                category = NoteCategory.QUIT_HABITS,
                title = listOfNotNull("Rechute", name).joinToString(" · ") +
                    " — ${DateFormatFr.short(date)}",
                body = cleanNote,
            )
        }
    }

    override suspend fun deleteRelapse(relapse: Relapse) {
        dao.getRelapses()
            .firstOrNull { it.id == relapse.id }
            ?.let { dao.deleteRelapse(it) }
    }
}
