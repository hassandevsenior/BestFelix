package com.felix.bestfelix.domain

import com.felix.bestfelix.domain.model.Priority
import com.felix.bestfelix.domain.model.Project
import com.felix.bestfelix.domain.model.ProjectStatus
import com.felix.bestfelix.domain.project.ProjectSorter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ProjectSorterTest {

    private fun project(
        title: String,
        priority: Priority,
        status: ProjectStatus = ProjectStatus.ACTIVE,
        dueDate: LocalDate? = null,
    ) = Project(
        id = title.hashCode().toLong(),
        title = title,
        context = "JDS",
        currentTask = null,
        priority = priority,
        status = status,
        dueDate = dueDate,
    )

    @Test
    fun `tri par priorite puis par echeance, sans echeance en dernier`() {
        val projects = listOf(
            project("Basse", Priority.LOW),
            project("Haute sans date", Priority.HIGH),
            project("Haute tard", Priority.HIGH, dueDate = TODAY.plusDays(30)),
            project("Haute bientot", Priority.HIGH, dueDate = TODAY.plusDays(2)),
            project("Moyenne", Priority.MEDIUM),
        )

        assertEquals(
            listOf("Haute bientot", "Haute tard", "Haute sans date", "Moyenne", "Basse"),
            ProjectSorter.sorted(projects).map { it.title },
        )
    }

    @Test
    fun `filtres actifs et termines`() {
        val projects = listOf(
            project("En cours", Priority.HIGH),
            project("En pause", Priority.MEDIUM, status = ProjectStatus.PAUSED),
            project("Fini", Priority.HIGH, status = ProjectStatus.DONE),
        )

        assertEquals(listOf("En cours", "En pause"), ProjectSorter.active(projects).map { it.title })
        assertEquals(listOf("Fini"), ProjectSorter.done(projects).map { it.title })
    }

    @Test
    fun `en retard seulement si l'echeance est passee et le projet non termine`() {
        val tard = project("Tard", Priority.HIGH, dueDate = TODAY.minusDays(1))
        val fini = project("Fini", Priority.HIGH, status = ProjectStatus.DONE, dueDate = TODAY.minusDays(1))

        assertTrue(ProjectSorter.isOverdue(tard, TODAY))
        assertFalse(ProjectSorter.isOverdue(fini, TODAY))
        assertFalse(ProjectSorter.isOverdue(project("Sans date", Priority.HIGH), TODAY))
    }

    @Test
    fun `les trois projets en cours les plus prioritaires pour l'ecran Aujourd'hui`() {
        val projects = listOf(
            project("A", Priority.LOW),
            project("B", Priority.HIGH, dueDate = TODAY.plusDays(5)),
            project("C", Priority.HIGH, dueDate = TODAY.plusDays(1)),
            project("D", Priority.MEDIUM),
            project("E", Priority.HIGH, status = ProjectStatus.PAUSED),
        )

        assertEquals(listOf("C", "B", "D"), ProjectSorter.topActive(projects).map { it.title })
    }
}
