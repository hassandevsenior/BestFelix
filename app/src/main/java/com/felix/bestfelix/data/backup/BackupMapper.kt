package com.felix.bestfelix.data.backup

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
import java.time.Instant
import java.time.LocalDate

/** Échec de lecture d'un fichier de sauvegarde, avec un message affichable tel quel. */
class BackupFormatException(message: String) : Exception(message)

private fun date(value: String): LocalDate =
    runCatching { LocalDate.parse(value) }
        .getOrElse { throw BackupFormatException("Date illisible dans le fichier : « $value »") }

private fun instant(value: String): Instant =
    runCatching { Instant.parse(value) }
        .getOrElse { throw BackupFormatException("Horodatage illisible dans le fichier : « $value »") }

private inline fun <reified T : Enum<T>> enum(value: String): T =
    runCatching { enumValueOf<T>(value) }
        .getOrElse { throw BackupFormatException("Valeur inconnue dans le fichier : « $value »") }

// --- Export ---

fun HabitEntity.toBackup() = HabitBackup(id, name, why, createdAt.toString(), position)

fun HabitDayEntity.toBackup() = HabitDayBackup(habitId, date.toString(), status.name)

fun QuitHabitEntity.toBackup() = QuitHabitBackup(id, name, startDate.toString(), position)

fun RelapseEntity.toBackup() = RelapseBackup(id, quitHabitId, date.toString(), note)

fun ProjectEntity.toBackup() = ProjectBackup(
    id = id,
    title = title,
    context = context,
    currentTask = currentTask,
    priority = priority.name,
    status = status.name,
    dueDate = dueDate?.toString(),
)

fun SavingGoalEntity.toBackup() =
    SavingGoalBackup(id, title, purpose, targetAmount, dueDate?.toString())

fun DepositEntity.toBackup() = DepositBackup(id, goalId, date.toString(), amount)

fun CreditEntity.toBackup() = CreditBackup(
    id = id,
    direction = direction.name,
    counterparty = counterparty,
    amount = amount,
    reason = reason,
    dueDate = dueDate?.toString(),
)

fun RepaymentEntity.toBackup() = RepaymentBackup(id, creditId, date.toString(), amount)

fun IncomeEntity.toBackup() = IncomeBackup(id, amount, date.toString(), source)

fun NoteEntity.toBackup() = NoteBackup(
    id = id,
    category = category.name,
    title = title,
    body = body,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString(),
)

// --- Import ---

fun HabitBackup.toEntity() = HabitEntity(id, name, why, date(createdAt), position)

fun HabitDayBackup.toEntity() =
    HabitDayEntity(habitId, date(date), enum<DayStatus>(status))

fun QuitHabitBackup.toEntity() = QuitHabitEntity(id, name, date(startDate), position)

fun RelapseBackup.toEntity() = RelapseEntity(id, quitHabitId, date(date), note)

fun ProjectBackup.toEntity() = ProjectEntity(
    id = id,
    title = title,
    context = context,
    currentTask = currentTask,
    priority = enum<Priority>(priority),
    status = enum<ProjectStatus>(status),
    dueDate = dueDate?.let(::date),
)

fun SavingGoalBackup.toEntity() =
    SavingGoalEntity(id, title, purpose, targetAmount, dueDate?.let(::date))

fun DepositBackup.toEntity() = DepositEntity(id, goalId, date(date), amount)

fun CreditBackup.toEntity() = CreditEntity(
    id = id,
    direction = enum<CreditDirection>(direction),
    counterparty = counterparty,
    amount = amount,
    reason = reason,
    dueDate = dueDate?.let(::date),
)

fun RepaymentBackup.toEntity() = RepaymentEntity(id, creditId, date(date), amount)

fun IncomeBackup.toEntity() = IncomeEntity(id, amount, date(date), source)

fun NoteBackup.toEntity() = NoteEntity(
    id = id,
    category = enum<NoteCategory>(category),
    title = title,
    body = body,
    createdAt = instant(createdAt),
    updatedAt = instant(updatedAt),
)
