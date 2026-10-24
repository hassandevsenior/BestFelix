package com.felix.bestfelix.domain

import com.felix.bestfelix.domain.pain.PainBalanceCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PainBalanceCalculatorTest {

    @Test
    fun `chaque jour tenu termine rapporte 5 XP, aujourd'hui ne compte pas encore`() {
        val h = quitHistory(startDate = TODAY.minusDays(10))

        // 10 jours terminés (J-10 à J-1), aujourd'hui en cours.
        assertEquals(50, PainBalanceCalculator.balance(h, TODAY))
        assertEquals(10, PainBalanceCalculator.countHeldFinishedDays(h, TODAY))
    }

    @Test
    fun `un jour non tenu efface 35 XP`() {
        val h = quitHistory(startDate = TODAY.minusDays(10), relapseDates = listOf(TODAY.minusDays(3)))

        // 9 jours tenus (45) − 1 jour non tenu (35).
        assertEquals(45 - 35, PainBalanceCalculator.balance(h, TODAY))
    }

    @Test
    fun `plusieurs rechutes le meme jour ne comptent que pour un jour non tenu`() {
        val date = TODAY.minusDays(3)
        val une = quitHistory(startDate = TODAY.minusDays(10), relapseDates = listOf(date))
        val trois = quitHistory(startDate = TODAY.minusDays(10), relapseDates = listOf(date, date, date))

        assertEquals(
            PainBalanceCalculator.balance(une, TODAY),
            PainBalanceCalculator.balance(trois, TODAY),
        )
        assertEquals(1, PainBalanceCalculator.state(trois, TODAY).brokenDaysCount)
    }

    @Test
    fun `le solde peut devenir une dette`() {
        val h = quitHistory(startDate = TODAY.minusDays(2), relapseDates = listOf(TODAY))

        // 2 jours tenus (10) − 1 jour non tenu (35) = −25.
        val state = PainBalanceCalculator.state(h, TODAY)
        assertEquals(-25, state.balance)
        assertTrue(state.isDebt)
    }

    @Test
    fun `une rechute aujourd'hui penalise immediatement`() {
        val sans = quitHistory(startDate = TODAY.minusDays(10))
        val avec = quitHistory(startDate = TODAY.minusDays(10), relapseDates = listOf(TODAY))

        assertEquals(50, PainBalanceCalculator.balance(sans, TODAY))
        assertEquals(50 - 35, PainBalanceCalculator.balance(avec, TODAY))
    }

    @Test
    fun `le meilleur solde est le maximum historique, pas le solde actuel`() {
        val h = quitHistory(startDate = TODAY.minusDays(10), relapseDates = listOf(TODAY.minusDays(1)))

        // Le solde monte à 45 (J-10 à J-2), puis tombe à 10 avec la rechute d'hier.
        assertEquals(45, PainBalanceCalculator.bestBalance(h, TODAY))
        assertEquals(10, PainBalanceCalculator.balance(h, TODAY))
    }

    @Test
    fun `jours tenus d'affilee remontent jusqu'a la derniere rechute`() {
        val h = quitHistory(startDate = TODAY.minusDays(10), relapseDates = listOf(TODAY.minusDays(3)))

        assertEquals(2, PainBalanceCalculator.heldStreak(h, TODAY))
    }

    @Test
    fun `une rechute aujourd'hui remet les jours tenus a zero`() {
        val h = quitHistory(startDate = TODAY.minusDays(10), relapseDates = listOf(TODAY))

        assertEquals(0, PainBalanceCalculator.heldStreak(h, TODAY))
    }

    @Test
    fun `la courbe a un point par jour depuis le debut`() {
        val h = quitHistory(startDate = TODAY.minusDays(4))
        val series = PainBalanceCalculator.series(h, TODAY)

        assertEquals(5, series.size)
        assertEquals(listOf(5, 10, 15, 20, 20), series.map { it.balance })
        assertEquals(TODAY, series.last().date)
    }

    @Test
    fun `un compteur qui demarre aujourd'hui vaut zero`() {
        val h = quitHistory(startDate = TODAY)

        assertEquals(0, PainBalanceCalculator.balance(h, TODAY))
        assertEquals(0, PainBalanceCalculator.state(h, TODAY).bestBalance)
    }
}
