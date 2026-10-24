package com.felix.bestfelix.domain.repository

import com.felix.bestfelix.domain.model.Note
import com.felix.bestfelix.domain.model.NoteCategory
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun observeNotes(): Flow<List<Note>>

    suspend fun create(category: NoteCategory, title: String?, body: String): Long

    suspend fun update(note: Note)

    suspend fun delete(note: Note)
}
