package com.felix.bestfelix.domain

import com.felix.bestfelix.domain.finance.SavingCalculator
import com.felix.bestfelix.domain.model.Deposit
import com.felix.bestfelix.domain.model.SavingGoal
import com.felix.bestfelix.domain.model.SavingGoalWithDeposits
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class SavingCalculatorTest {

    private fun goal(
        target: Long,
        dueDate: LocalDate? = null,
        deposits: List<Long> = emptyList(),
    ) = SavingGoalWithDeposits(
        goal = SavingGoal(id = 1, title = "Ordinateur", purpose = "JDS", targetAmount = target, dueDate = dueDate),
        deposits = deposits.mapIndexed { index, montant ->
            Deposit(id = index.toLong(), goalId = 1, date = TODAY, amount = montant)
        },
    )

    @Test
    fun `progression en montant et en pourcentage`() {
        val progress = SavingCalculator.progress(
            goal(target = 500_000, deposits = listOf(100_000, 50_000)),
            TODAY,
        )

        assertEquals(150_000, progress.saved)
        assertEquals(350_000, progress.remaining)
        assertEquals(30, progress.percent)
        assertEquals(0.3f, progress.fraction, 0.0001f)
    }

    @Test
    fun `objectif atteint`() {
        val progress = SavingCalculator.progress(
            goal(target = 100_000, deposits = listOf(100_000)),
            TODAY,
        )

        assertEquals(0, progress.remaining)
        assertEquals(100, progress.percent)
        assertTrue(progress.isReached)
        assertNull(progress.monthlyNeeded)
    }

    @Test
    fun `mois restants arrondis au mois superieur`() {
        // TODAY = 23 octobre 2026.
        assertEquals(3, SavingCalculator.monthsLeft(TODAY, LocalDate.of(2027, 1, 23)))
        assertEquals(4, SavingCalculator.monthsLeft(TODAY, LocalDate.of(2027, 1, 24)))
        assertEquals(1, SavingCalculator.monthsLeft(TODAY, TODAY))
        assertEquals(1, SavingCalculator.monthsLeft(TODAY, TODAY.plusDays(3)))
        assertNull(SavingCalculator.monthsLeft(TODAY, TODAY.minusDays(1)))
    }

    @Test
    fun `epargne mensuelle necessaire pour tenir l'echeance`() {
        val progress = SavingCalculator.progress(
            goal(target = 400_000, dueDate = LocalDate.of(2027, 1, 23), deposits = listOf(100_000)),
            TODAY,
        )

        // 300 000 FCFA à trouver en 3 mois.
        assertEquals(100_000L, progress.monthlyNeeded)
    }

    @Test
    fun `l'epargne mensuelle est arrondie au superieur`() {
        val progress = SavingCalculator.progress(
            goal(target = 100_000, dueDate = LocalDate.of(2027, 1, 23)),
            TODAY,
        )

        // 100 000 / 3 = 33 333,33 → on vise 33 334.
        assertEquals(33_334L, progress.monthlyNeeded)
    }

    @Test
    fun `pas de mensualite sans echeance ni apres l'echeance`() {
        assertNull(SavingCalculator.progress(goal(target = 100_000), TODAY).monthlyNeeded)
        assertNull(
            SavingCalculator.progress(
                goal(target = 100_000, dueDate = TODAY.minusDays(1)),
                TODAY,
            ).monthlyNeeded,
        )
    }
}
