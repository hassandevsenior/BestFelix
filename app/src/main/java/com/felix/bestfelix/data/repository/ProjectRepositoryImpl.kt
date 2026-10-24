package com.felix.bestfelix.data.repository

import com.felix.bestfelix.data.db.dao.ProjectDao
import com.felix.bestfelix.data.db.entity.ProjectEntity
import com.felix.bestfelix.data.mapper.toDomain
import com.felix.bestfelix.data.mapper.toEntity
import com.felix.bestfelix.domain.model.Priority
import com.felix.bestfelix.domain.model.Project
import com.felix.bestfelix.domain.model.ProjectStatus
import com.felix.bestfelix.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepositoryImpl @Inject constructor(
    private val dao: ProjectDao,
) : ProjectRepository {

    override fun observeProjects(): Flow<List<Project>> =
        dao.observeProjects().map { projects -> projects.map { it.toDomain() } }

    override suspend fun create(
        title: String,
        context: String,
        currentTask: String?,
        priority: Priority,
        status: ProjectStatus,
        dueDate: LocalDate?,
    ): Long = dao.upsert(
        ProjectEntity(
            title = title.trim(),
            context = context.trim(),
            currentTask = currentTask?.trim()?.takeIf { it.isNotEmpty() },
            priority = priority,
            status = status,
            dueDate = dueDate,
        ),
    )

    override suspend fun update(project: Project) {
        dao.upsert(
            project.copy(
                title = project.title.trim(),
                context = project.context.trim(),
                currentTask = project.currentTask?.trim()?.takeIf { it.isNotEmpty() },
            ).toEntity(),
        )
    }

    override suspend fun delete(project: Project) {
        dao.delete(project.toEntity())
    }
}
