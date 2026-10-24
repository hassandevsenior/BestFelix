package com.felix.bestfelix.ui.projects.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.bestfelix.core.time.DateProvider
import com.felix.bestfelix.domain.model.Priority
import com.felix.bestfelix.domain.model.Project
import com.felix.bestfelix.domain.model.ProjectStatus
import com.felix.bestfelix.domain.project.ProjectSorter
import com.felix.bestfelix.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ProjectRow(
    val project: Project,
    val overdue: Boolean,
)

data class ProjectsUiState(
    val active: List<ProjectRow> = emptyList(),
    val done: List<ProjectRow> = emptyList(),
    val loading: Boolean = true,
)

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val repository: ProjectRepository,
    private val dateProvider: DateProvider,
) : ViewModel() {

    val uiState: StateFlow<ProjectsUiState> = repository.observeProjects()
        .map { projects ->
            val today = dateProvider.today()
            ProjectsUiState(
                active = ProjectSorter.active(projects).map { ProjectRow(it, ProjectSorter.isOverdue(it, today)) },
                done = ProjectSorter.done(projects).map { ProjectRow(it, false) },
                loading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProjectsUiState(),
        )

    fun today(): LocalDate = dateProvider.today()

    fun create(
        title: String,
        context: String,
        currentTask: String?,
        priority: Priority,
        status: ProjectStatus,
        dueDate: LocalDate?,
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.create(title, context, currentTask, priority, status, dueDate)
        }
    }

    fun update(project: Project) {
        if (project.title.isBlank()) return
        viewModelScope.launch { repository.update(project) }
    }

    fun delete(project: Project) {
        viewModelScope.launch { repository.delete(project) }
    }
}
