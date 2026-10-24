package com.felix.bestfelix.data

import com.felix.bestfelix.data.backup.BackupFile
import com.felix.bestfelix.data.backup.BackupFormatException
import com.felix.bestfelix.data.backup.CURRENT_BACKUP_VERSION
import com.felix.bestfelix.data.backup.NoteBackup
import com.felix.bestfelix.data.backup.toBackup
import com.felix.bestfelix.data.backup.toEntity
import com.felix.bestfelix.data.db.entity.CreditEntity
import com.felix.bestfelix.data.db.entity.DepositEntity
import com.felix.bestfelix.data.db.entity.HabitDayEntity
import com.felix.bestfelix.data.db.entity.HabitEntity
import com.felix.bestfelix.data.db.entity.IncomeEntity
import com.felix.bestfelix.data.db.entity.NoteEntity
import com.felix.bestfelix.data.db.entity.ProjectEntity
import com.felix.bestfelix.data.db.entity.QuitHabitEntity
import com.felix.bestfelix.data.db.entity.RelapseEntity
import com.felix.bestfelix.data.db.entity.RepaymentEntity
import com.felix.bestfelix.data.db.entity.SavingGoalEntity
import com.felix.bestfelix.domain.model.CreditDirection
import com.felix.bestfelix.domain.model.DayStatus
import com.felix.bestfelix.domain.model.NoteCategory
import com.felix.bestfelix.domain.model.Priority
import com.felix.bestfelix.domain.model.ProjectStatus
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class BackupRoundTripTest {

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true; encodeDefaults = true }

    private val habit = HabitEntity(1, "Lire", "Finir un livre", LocalDate.of(2026, 9, 1), 0)
    private val habitDays = listOf(
        HabitDayEntity(1, LocalDate.of(2026, 10, 22), DayStatus.VALIDATED),
        HabitDayEntity(1, LocalDate.of(2026, 10, 21), DayStatus.SAVED_BY_JOKER),
    )
    private val quitHabit = QuitHabitEntity(2, "Cigarette", LocalDate.of(2026, 10, 1), 1)
    private val relapse = RelapseEntity(3, 2, LocalDate.of(2026, 10, 19), "Soirée difficile")
    private val project = ProjectEntity(
        id = 4,
        title = "Refonte API",
        context = "JDS",
        currentTask = "Migrer l'auth",
        priority = Priority.HIGH,
        status = ProjectStatus.ACTIVE,
        dueDate = LocalDate.of(2026, 11, 2),
    )
    private val goal = SavingGoalEntity(5, "Ordinateur", "Travail", 500_000, LocalDate.of(2027, 2, 1))
    private val deposit = DepositEntity(6, 5, LocalDate.of(2026, 10, 3), 100_000)
    private val credit = CreditEntity(
        id = 7,
        direction = CreditDirection.I_OWE,
        counterparty = "Issa",
        amount = 75_000,
        reason = "Avance",
        dueDate = null,
    )
    private val repayment = RepaymentEntity(8, 7, LocalDate.of(2026, 10, 20), 25_000)
    private val income = IncomeEntity(9, 150_000, LocalDate.of(2026, 10, 5), "JDS")
    private val note = NoteEntity(
        id = 10,
        category = NoteCategory.FINANCE,
        title = "Rappel",
        body = "Relancer Awa",
        createdAt = Instant.parse("2026-10-20T08:30:00Z"),
        updatedAt = Instant.parse("2026-10-21T09:00:00Z"),
    )

    private fun backupFile() = BackupFile(
        exportedAt = Instant.parse("2026-10-23T18:00:00Z").toString(),
        habits = listOf(habit.toBackup()),
        habitDays = habitDays.map { it.toBackup() },
        quitHabits = listOf(quitHabit.toBackup()),
        relapses = listOf(relapse.toBackup()),
        projects = listOf(project.toBackup()),
        savingGoals = listOf(goal.toBackup()),
        deposits = listOf(deposit.toBackup()),
        credits = listOf(credit.toBackup()),
        repayments = listOf(repayment.toBackup()),
        incomes = listOf(income.toBackup()),
        notes = listOf(note.toBackup()),
    )

    @Test
    fun `aller-retour JSON sans perte`() {
        val decoded = json.decodeFromString<BackupFile>(json.encodeToString(backupFile()))

        assertEquals(CURRENT_BACKUP_VERSION, decoded.version)
        assertEquals(habit, decoded.habits.single().toEntity())
        assertEquals(habitDays, decoded.habitDays.map { it.toEntity() })
        assertEquals(quitHabit, decoded.quitHabits.single().toEntity())
        assertEquals(relapse, decoded.relapses.single().toEntity())
        assertEquals(project, decoded.projects.single().toEntity())
        assertEquals(goal, decoded.savingGoals.single().toEntity())
        assertEquals(deposit, decoded.deposits.single().toEntity())
        assertEquals(credit, decoded.credits.single().toEntity())
        assertEquals(repayment, decoded.repayments.single().toEntity())
        assertEquals(income, decoded.incomes.single().toEntity())
        assertEquals(note, decoded.notes.single().toEntity())
    }

    @Test
    fun `le compte d'elements couvre toutes les tables`() {
        assertEquals(12, backupFile().itemCount)
    }

    @Test
    fun `les dates sont ecrites en ISO, lisibles dans le fichier`() {
        val text = json.encodeToString(backupFile())

        assertTrue(text.contains("\"createdAt\": \"2026-09-01\""))
        assertTrue(text.contains("\"date\": \"2026-10-22\""))
        assertTrue(text.contains("\"status\": \"VALIDATED\""))
    }

    @Test
    fun `une rubrique inconnue donne une erreur lisible`() {
        val broken = NoteBackup(
            id = 1,
            category = "RUBRIQUE_INVENTEE",
            title = null,
            body = "texte",
            createdAt = "2026-10-20T08:30:00Z",
            updatedAt = "2026-10-20T08:30:00Z",
        )

        val error = assertThrows(BackupFormatException::class.java) { broken.toEntity() }
        assertTrue(error.message!!.contains("RUBRIQUE_INVENTEE"))
    }

    @Test
    fun `une date illisible donne une erreur lisible`() {
        val broken = habit.toBackup().copy(createdAt = "23-10-2026")

        val error = assertThrows(BackupFormatException::class.java) { broken.toEntity() }
        assertTrue(error.message!!.contains("23-10-2026"))
    }
}
