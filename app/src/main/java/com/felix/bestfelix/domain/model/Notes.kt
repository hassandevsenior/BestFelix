package com.felix.bestfelix.domain.model

import java.time.Instant

data class Note(
    val id: Long,
    val category: NoteCategory,
    val title: String?,
    val body: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)
