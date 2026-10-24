package com.felix.bestfelix.domain.finance

import com.felix.bestfelix.domain.model.CreditDirection
import com.felix.bestfelix.domain.model.CreditWithRepayments
import java.time.LocalDate

object CreditCalculator {

    /** Reste à payer : le montant moins les remboursements partiels, jamais négatif. */
    fun remaining(credit: CreditWithRepayments): Long {
        val paid = credit.repayments.sumOf { it.amount }
        return (credit.credit.amount - paid).coerceAtLeast(0)
    }

    fun isSettled(credit: CreditWithRepayments): Boolean = remaining(credit) == 0L

    /** En retard : la date limite est passée et il reste quelque chose à payer. */
    fun isOverdue(credit: CreditWithRepayments, today: LocalDate): Boolean {
        val dueDate = credit.credit.dueDate ?: return false
        return dueDate.isBefore(today) && !isSettled(credit)
    }

    /** Total que je dois : la somme des restes à payer, dans ce sens-là. */
    fun totalIOwe(credits: List<CreditWithRepayments>): Long =
        credits.filter { it.credit.direction == CreditDirection.I_OWE }.sumOf { remaining(it) }

    /** Total qu'on me doit. */
    fun totalOwedToMe(credits: List<CreditWithRepayments>): Long =
        credits.filter { it.credit.direction == CreditDirection.OWED_TO_ME }.sumOf { remaining(it) }
}
