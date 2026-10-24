package com.felix.bestfelix.domain.model

import java.time.LocalDate

data class Project(
    val id: Long,
    val title: String,
    val context: String,
    val currentTask: String?,
    val priority: Priority,
    val status: ProjectStatus,
    val dueDate: LocalDate?,
)

data class SavingGoal(
    val id: Long,
    val title: String,
    val purpose: String?,
    val targetAmount: Long,
    val dueDate: LocalDate?,
)

data class Deposit(
    val id: Long,
    val goalId: Long,
    val date: LocalDate,
    val amount: Long,
)

data class SavingGoalWithDeposits(
    val goal: SavingGoal,
    val deposits: List<Deposit>,
)
