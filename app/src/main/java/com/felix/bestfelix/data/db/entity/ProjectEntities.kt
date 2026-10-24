package com.felix.bestfelix.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.felix.bestfelix.domain.model.Priority
import com.felix.bestfelix.domain.model.ProjectStatus
import java.time.LocalDate

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    /** Entreprise ou contexte : JDS, PrimeTech, un client… */
    val context: String,
    /** « Je suis en cours sur… » : la tâche du moment. */
    val currentTask: String? = null,
    val priority: Priority,
    val status: ProjectStatus,
    val dueDate: LocalDate? = null,
)
