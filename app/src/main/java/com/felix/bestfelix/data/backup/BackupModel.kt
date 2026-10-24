package com.felix.bestfelix.data.backup

import kotlinx.serialization.Serializable

/**
 * Format du fichier de sauvegarde. Les dates sont écrites en ISO (« 2026-10-23 ») pour que
 * le fichier reste lisible à l'œil nu, et les énumérations par leur nom.
 *
 * Toute évolution du schéma incrémente [CURRENT_BACKUP_VERSION] et doit savoir relire
 * les versions précédentes.
 */
const val CURRENT_BACKUP_VERSION = 1

@Serializable
data class BackupFile(
    val version: Int = CURRENT_BACKUP_VERSION,
    val exportedAt: String,
    val habits: List<HabitBackup> = emptyList(),
    val habitDays: List<HabitDayBackup> = emptyList(),
    val quitHabits: List<QuitHabitBackup> = emptyList(),
    val relapses: List<RelapseBackup> = emptyList(),
    val projects: List<ProjectBackup> = emptyList(),
    val savingGoals: List<SavingGoalBackup> = emptyList(),
    val deposits: List<DepositBackup> = emptyList(),
    val credits: List<CreditBackup> = emptyList(),
    val repayments: List<RepaymentBackup> = emptyList(),
    val incomes: List<IncomeBackup> = emptyList(),
    val notes: List<NoteBackup> = emptyList(),
) {
    val itemCount: Int
        get() = habits.size + habitDays.size + quitHabits.size + relapses.size +
            projects.size + savingGoals.size + deposits.size + credits.size +
            repayments.size + incomes.size + notes.size
}

@Serializable
data class HabitBackup(
    val id: Long,
    val name: String,
    val why: String? = null,
    val createdAt: String,
    val position: Int = 0,
)

@Serializable
data class HabitDayBackup(
    val habitId: Long,
    val date: String,
    val status: String,
)

@Serializable
data class QuitHabitBackup(
    val id: Long,
    val name: String,
    val startDate: String,
    val position: Int = 0,
)

@Serializable
data class RelapseBackup(
    val id: Long,
    val quitHabitId: Long,
    val date: String,
    val note: String? = null,
)

@Serializable
data class ProjectBackup(
    val id: Long,
    val title: String,
    val context: String,
    val currentTask: String? = null,
    val priority: String,
    val status: String,
    val dueDate: String? = null,
)

@Serializable
data class SavingGoalBackup(
    val id: Long,
    val title: String,
    val purpose: String? = null,
    val targetAmount: Long,
    val dueDate: String? = null,
)

@Serializable
data class DepositBackup(
    val id: Long,
    val goalId: Long,
    val date: String,
    val amount: Long,
)

@Serializable
data class CreditBackup(
    val id: Long,
    val direction: String,
    val counterparty: String,
    val amount: Long,
    val reason: String? = null,
    val dueDate: String? = null,
)

@Serializable
data class RepaymentBackup(
    val id: Long,
    val creditId: Long,
    val date: String,
    val amount: Long,
)

@Serializable
data class IncomeBackup(
    val id: Long,
    val amount: Long,
    val date: String,
    val source: String,
)

@Serializable
data class NoteBackup(
    val id: Long,
    val category: String,
    val title: String? = null,
    val body: String,
    val createdAt: String,
    val updatedAt: String,
)
