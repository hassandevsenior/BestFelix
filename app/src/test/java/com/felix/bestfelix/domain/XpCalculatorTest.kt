package com.felix.bestfelix.domain

import com.felix.bestfelix.domain.reward.RewardConstants
import com.felix.bestfelix.domain.reward.XpCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class XpCalculatorTest {

    @Test
    fun `multiplicateur par paliers de 7 jours, plafonne a 3`() {
        assertEquals(1.0, XpCalculator.multiplier(1), 0.0)
        assertEquals(1.0, XpCalculator.multiplier(6), 0.0)
        assertEquals(1.5, XpCalculator.multiplier(7), 0.0)
        assertEquals(2.0, XpCalculator.multiplier(14), 0.0)
        assertEquals(3.0, XpCalculator.multiplier(28), 0.0)
        assertEquals(3.0, XpCalculator.multiplier(100), 0.0)
    }

    @Test
    fun `les six premiers jours rapportent 10 XP, le septieme 15`() {
        val h = history(validated(consecutiveDays(TODAY, 7)))

        assertEquals(10, XpCalculator.xpForDay(h, TODAY.minusDays(6)))
        assertEquals(10, XpCalculator.xpForDay(h, TODAY.minusDays(1)))
        assertEquals(15, XpCalculator.xpForDay(h, TODAY))
        assertEquals(6 * 10 + 15, XpCalculator.xpForHabit(h))
    }

    @Test
    fun `un jour sauve par un joker ne rapporte rien mais nourrit le multiplicateur`() {
        // 6 jours validés, 7e jour sauvé par un joker, 8e jour validé (série de 8).
        val days = validated(consecutiveDays(TODAY.minusDays(2), 6)) +
            savedByJoker(TODAY.minusDays(1)) +
            validated(TODAY)

        val h = history(days)

        assertEquals(0, XpCalculator.xpForDay(h, TODAY.minusDays(1)))
        assertEquals(15, XpCalculator.xpForDay(h, TODAY))
        assertEquals(6 * 10 + 15, XpCalculator.xpForHabit(h))
    }

    @Test
    fun `journee parfaite quand toutes les habitudes du jour sont validees`() {
        val a = history(validated(TODAY), id = 1)
        val b = history(validated(TODAY), id = 2)

        assertTrue(XpCalculator.isPerfectDay(listOf(a, b), TODAY))
        assertEquals(
            2 * 10 + RewardConstants.PERFECT_DAY_BONUS_XP,
            XpCalculator.totalXp(listOf(a, b)),
        )
    }

    @Test
    fun `pas de journee parfaite si une habitude n'est que sauvee par un joker`() {
        val a = history(validated(TODAY), id = 1)
        val b = history(savedByJoker(TODAY), id = 2)

        assertFalse(XpCalculator.isPerfectDay(listOf(a, b), TODAY))
        assertEquals(10, XpCalculator.totalXp(listOf(a, b)))
    }

    @Test
    fun `une habitude creee apres ce jour-la n'empeche pas la journee parfaite`() {
        val ancienne = history(validated(TODAY.minusDays(1)), createdAt = TODAY.minusDays(10), id = 1)
        val nouvelle = history(validated(TODAY), createdAt = TODAY, id = 2)

        assertTrue(XpCalculator.isPerfectDay(listOf(ancienne, nouvelle), TODAY.minusDays(1)))
    }

    @Test
    fun `pas de journee parfaite sans aucune habitude ce jour-la`() {
        val h = history(validated(TODAY), createdAt = TODAY)

        assertFalse(XpCalculator.isPerfectDay(listOf(h), TODAY.minusDays(1)))
    }
}
