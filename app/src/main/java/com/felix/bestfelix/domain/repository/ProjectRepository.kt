package com.felix.bestfelix.domain.repository

import com.felix.bestfelix.domain.model.Priority
import com.felix.bestfelix.domain.model.Project
import com.felix.bestfelix.domain.model.ProjectStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ProjectRepository {

    fun observeProjects(): Flow<List<Project>>

    suspend fun create(
        title: String,
        context: String,
        currentTask: String?,
        priority: Priority,
        status: ProjectStatus,
        dueDate: LocalDate?,
    ): Long

    suspend fun update(project: Project)

    suspend fun delete(project: Project)
}
