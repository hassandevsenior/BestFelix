package com.felix.bestfelix.domain

import com.felix.bestfelix.domain.finance.CreditCalculator
import com.felix.bestfelix.domain.model.Credit
import com.felix.bestfelix.domain.model.CreditDirection
import com.felix.bestfelix.domain.model.CreditWithRepayments
import com.felix.bestfelix.domain.model.Repayment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class CreditCalculatorTest {

    private fun credit(
        amount: Long,
        direction: CreditDirection = CreditDirection.I_OWE,
        dueDate: LocalDate? = null,
        repayments: List<Long> = emptyList(),
    ) = CreditWithRepayments(
        credit = Credit(
            id = 1,
            direction = direction,
            counterparty = "Issa",
            amount = amount,
            reason = null,
            dueDate = dueDate,
        ),
        repayments = repayments.mapIndexed { index, montant ->
            Repayment(id = index.toLong(), creditId = 1, date = TODAY, amount = montant)
        },
    )

    @Test
    fun `reste a payer = montant moins les remboursements`() {
        val c = credit(amount = 100_000, repayments = listOf(25_000, 15_000))

        assertEquals(60_000, CreditCalculator.remaining(c))
        assertFalse(CreditCalculator.isSettled(c))
    }

    @Test
    fun `solde quand tout est rembourse`() {
        val c = credit(amount = 50_000, repayments = listOf(20_000, 30_000))

        assertEquals(0, CreditCalculator.remaining(c))
        assertTrue(CreditCalculator.isSettled(c))
    }

    @Test
    fun `un trop-percu ne rend pas le reste negatif`() {
        val c = credit(amount = 50_000, repayments = listOf(60_000))

        assertEquals(0, CreditCalculator.remaining(c))
    }

    @Test
    fun `en retard seulement si la date est passee et qu'il reste a payer`() {
        val enRetard = credit(amount = 10_000, dueDate = TODAY.minusDays(1))
        val aJour = credit(amount = 10_000, dueDate = TODAY.plusDays(1))
        val soldeEnRetard = credit(
            amount = 10_000,
            dueDate = TODAY.minusDays(1),
            repayments = listOf(10_000),
        )

        assertTrue(CreditCalculator.isOverdue(enRetard, TODAY))
        assertFalse(CreditCalculator.isOverdue(aJour, TODAY))
        assertFalse(CreditCalculator.isOverdue(soldeEnRetard, TODAY))
    }

    @Test
    fun `totaux par sens, sur les restes a payer`() {
        val credits = listOf(
            credit(amount = 100_000, direction = CreditDirection.I_OWE, repayments = listOf(40_000)),
            credit(amount = 25_000, direction = CreditDirection.I_OWE),
            credit(amount = 75_000, direction = CreditDirection.OWED_TO_ME, repayments = listOf(75_000)),
            credit(amount = 30_000, direction = CreditDirection.OWED_TO_ME),
        )

        assertEquals(85_000, CreditCalculator.totalIOwe(credits))
        assertEquals(30_000, CreditCalculator.totalOwedToMe(credits))
    }
}
