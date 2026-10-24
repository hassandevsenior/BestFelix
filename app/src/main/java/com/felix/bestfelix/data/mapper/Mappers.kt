package com.felix.bestfelix.data.mapper

import com.felix.bestfelix.data.db.entity.CreditEntity
import com.felix.bestfelix.data.db.entity.DepositEntity
import com.felix.bestfelix.data.db.entity.HabitEntity
import com.felix.bestfelix.data.db.entity.IncomeEntity
import com.felix.bestfelix.data.db.entity.NoteEntity
import com.felix.bestfelix.data.db.entity.ProjectEntity
import com.felix.bestfelix.data.db.entity.QuitHabitEntity
import com.felix.bestfelix.data.db.entity.RelapseEntity
import com.felix.bestfelix.data.db.entity.RepaymentEntity
import com.felix.bestfelix.data.db.entity.SavingGoalEntity
import com.felix.bestfelix.domain.model.Credit
import com.felix.bestfelix.domain.model.Deposit
import com.felix.bestfelix.domain.model.Habit
import com.felix.bestfelix.domain.model.Income
import com.felix.bestfelix.domain.model.Note
import com.felix.bestfelix.domain.model.Project
import com.felix.bestfelix.domain.model.QuitHabit
import com.felix.bestfelix.domain.model.Relapse
import com.felix.bestfelix.domain.model.Repayment
import com.felix.bestfelix.domain.model.SavingGoal

fun HabitEntity.toDomain(): Habit = Habit(
    id = id,
    name = name,
    why = why,
    createdAt = createdAt,
    position = position,
)

fun Habit.toEntity(): HabitEntity = HabitEntity(
    id = id,
    name = name,
    why = why,
    createdAt = createdAt,
    position = position,
)

fun QuitHabitEntity.toDomain(): QuitHabit = QuitHabit(
    id = id,
    name = name,
    startDate = startDate,
    position = position,
)

fun QuitHabit.toEntity(): QuitHabitEntity = QuitHabitEntity(
    id = id,
    name = name,
    startDate = startDate,
    position = position,
)

fun RelapseEntity.toDomain(): Relapse = Relapse(id = id, date = date, note = note)

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    category = category,
    title = title,
    body = body,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    category = category,
    title = title,
    body = body,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun ProjectEntity.toDomain(): Project = Project(
    id = id,
    title = title,
    context = context,
    currentTask = currentTask,
    priority = priority,
    status = status,
    dueDate = dueDate,
)

fun Project.toEntity(): ProjectEntity = ProjectEntity(
    id = id,
    title = title,
    context = context,
    currentTask = currentTask,
    priority = priority,
    status = status,
    dueDate = dueDate,
)

fun SavingGoalEntity.toDomain(): SavingGoal = SavingGoal(
    id = id,
    title = title,
    purpose = purpose,
    targetAmount = targetAmount,
    dueDate = dueDate,
)

fun SavingGoal.toEntity(): SavingGoalEntity = SavingGoalEntity(
    id = id,
    title = title,
    purpose = purpose,
    targetAmount = targetAmount,
    dueDate = dueDate,
)

fun DepositEntity.toDomain(): Deposit = Deposit(id = id, goalId = goalId, date = date, amount = amount)

fun Deposit.toEntity(): DepositEntity =
    DepositEntity(id = id, goalId = goalId, date = date, amount = amount)

fun CreditEntity.toDomain(): Credit = Credit(
    id = id,
    direction = direction,
    counterparty = counterparty,
    amount = amount,
    reason = reason,
    dueDate = dueDate,
)

fun Credit.toEntity(): CreditEntity = CreditEntity(
    id = id,
    direction = direction,
    counterparty = counterparty,
    amount = amount,
    reason = reason,
    dueDate = dueDate,
)

fun RepaymentEntity.toDomain(): Repayment =
    Repayment(id = id, creditId = creditId, date = date, amount = amount)

fun Repayment.toEntity(): RepaymentEntity =
    RepaymentEntity(id = id, creditId = creditId, date = date, amount = amount)

fun IncomeEntity.toDomain(): Income = Income(id = id, amount = amount, date = date, source = source)

fun Income.toEntity(): IncomeEntity =
    IncomeEntity(id = id, amount = amount, date = date, source = source)
