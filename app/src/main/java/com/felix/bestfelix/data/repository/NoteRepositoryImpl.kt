package com.felix.bestfelix.data.repository

import com.felix.bestfelix.data.db.dao.NoteDao
import com.felix.bestfelix.data.db.entity.NoteEntity
import com.felix.bestfelix.data.mapper.toDomain
import com.felix.bestfelix.data.mapper.toEntity
import com.felix.bestfelix.domain.model.Note
import com.felix.bestfelix.domain.model.NoteCategory
import com.felix.bestfelix.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val dao: NoteDao,
) : NoteRepository {

    override fun observeNotes(): Flow<List<Note>> =
        dao.observeNotes().map { notes -> notes.map { it.toDomain() } }

    override suspend fun create(category: NoteCategory, title: String?, body: String): Long {
        val now = Instant.now()
        return dao.upsert(
            NoteEntity(
                category = category,
                title = title?.trim()?.takeIf { it.isNotEmpty() },
                body = body.trim(),
                createdAt = now,
                updatedAt = now,
            ),
        )
    }

    override suspend fun update(note: Note) {
        dao.upsert(note.copy(updatedAt = Instant.now()).toEntity())
    }

    override suspend fun delete(note: Note) {
        dao.delete(note.toEntity())
    }
}
