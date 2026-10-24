package com.felix.bestfelix.domain.model

import java.time.LocalDate

data class Credit(
    val id: Long,
    val direction: CreditDirection,
    val counterparty: String,
    val amount: Long,
    val reason: String?,
    val dueDate: LocalDate?,
)

data class Repayment(
    val id: Long,
    val creditId: Long,
    val date: LocalDate,
    val amount: Long,
)

data class CreditWithRepayments(
    val credit: Credit,
    val repayments: List<Repayment>,
)

data class Income(
    val id: Long,
    val amount: Long,
    val date: LocalDate,
    val source: String,
)
