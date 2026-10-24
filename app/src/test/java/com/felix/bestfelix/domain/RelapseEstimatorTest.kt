package com.felix.bestfelix.domain

import com.felix.bestfelix.domain.pain.RelapseEstimator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RelapseEstimatorTest {

    @Test
    fun `jours pour revenir au record = ceil de l'ecart divise par 5`() {
        // L'exemple du cahier des charges : de 85 XP vers un record de 180 XP.
        assertEquals(19, RelapseEstimator.daysToRecord(best = 180, balance = 85))
        assertEquals(1, RelapseEstimator.daysToRecord(best = 100, balance = 96))
        assertEquals(8, RelapseEstimator.daysToRecord(best = 100, balance = 62))
    }

    @Test
    fun `aucun jour a rattraper si le solde reste au-dessus du record`() {
        assertNull(RelapseEstimator.daysToRecord(best = 100, balance = 100))
        assertNull(RelapseEstimator.daysToRecord(best = 100, balance = 120))
    }

    @Test
    fun `rechuter aujourd'hui coute exactement 35 XP`() {
        val h = quitHistory(startDate = TODAY.minusDays(30))
        val preview = RelapseEstimator.preview(h, date = TODAY, today = TODAY)

        assertFalse(preview.alreadyBroken)
        assertEquals(150, preview.balanceBefore)
        assertEquals(115, preview.balanceAfter)
        assertEquals(35, preview.penalty)
        assertEquals(7, RelapseEstimator.daysToRecord(preview.bestBalance, preview.balanceAfter))
    }

    @Test
    fun `rechuter sur un jour passe deja tenu coute 40 XP, car ce jour perd son +5`() {
        val h = quitHistory(startDate = TODAY.minusDays(30))
        val preview = RelapseEstimator.preview(h, date = TODAY.minusDays(5), today = TODAY)

        assertEquals(150, preview.balanceBefore)
        assertEquals(110, preview.balanceAfter)
        assertEquals(40, preview.penalty)
    }

    @Test
    fun `un jour deja non tenu n'est pas penalise deux fois`() {
        val date = TODAY.minusDays(5)
        val h = quitHistory(startDate = TODAY.minusDays(30), relapseDates = listOf(date))
        val preview = RelapseEstimator.preview(h, date = date, today = TODAY)

        assertTrue(preview.alreadyBroken)
        assertEquals(preview.balanceBefore, preview.balanceAfter)
        assertEquals(0, preview.penalty)
    }
}
