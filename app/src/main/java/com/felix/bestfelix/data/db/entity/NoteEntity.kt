package com.felix.bestfelix.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.felix.bestfelix.domain.model.NoteCategory
import java.time.Instant

@Entity(tableName = "notes", indices = [Index("category")])
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: NoteCategory,
    val title: String? = null,
    val body: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)
