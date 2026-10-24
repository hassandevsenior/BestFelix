package com.felix.bestfelix.domain.project

import com.felix.bestfelix.domain.model.Priority
import com.felix.bestfelix.domain.model.Project
import com.felix.bestfelix.domain.model.ProjectStatus
import java.time.LocalDate

object ProjectSorter {

    private val priorityOrder = compareBy<Project> {
        when (it.priority) {
            Priority.HIGH -> 0
            Priority.MEDIUM -> 1
            Priority.LOW -> 2
        }
    }

    /** Tri par priorité, puis par échéance (les projets sans échéance passent après). */
    fun sorted(projects: List<Project>): List<Project> =
        projects.sortedWith(
            priorityOrder
                .thenBy { it.dueDate ?: LocalDate.MAX }
                .thenBy { it.title.lowercase() },
        )

    fun active(projects: List<Project>): List<Project> =
        sorted(projects.filter { it.status != ProjectStatus.DONE })

    fun done(projects: List<Project>): List<Project> =
        sorted(projects.filter { it.status == ProjectStatus.DONE })

    /** En retard : échéance dépassée sur un projet non terminé. */
    fun isOverdue(project: Project, today: LocalDate): Boolean {
        val dueDate = project.dueDate ?: return false
        return dueDate.isBefore(today) && project.status != ProjectStatus.DONE
    }

    /** Les trois projets en cours les plus prioritaires, pour l'écran Aujourd'hui. */
    fun topActive(projects: List<Project>, count: Int = 3): List<Project> =
        sorted(projects.filter { it.status == ProjectStatus.ACTIVE }).take(count)
}
