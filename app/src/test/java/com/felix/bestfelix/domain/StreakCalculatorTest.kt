package com.felix.bestfelix.domain

import com.felix.bestfelix.domain.reward.StreakCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class StreakCalculatorTest {

    @Test
    fun `serie compte depuis aujourd'hui quand aujourd'hui est valide`() {
        val h = history(validated(consecutiveDays(TODAY, 5)))

        assertEquals(5, StreakCalculator.currentStreak(h, TODAY))
    }

    @Test
    fun `serie compte depuis hier quand aujourd'hui n'est pas encore valide`() {
        val h = history(validated(consecutiveDays(TODAY.minusDays(1), 5)))

        assertEquals(5, StreakCalculator.currentStreak(h, TODAY))
    }

    @Test
    fun `serie nulle si ni aujourd'hui ni hier`() {
        val h = history(validated(consecutiveDays(TODAY.minusDays(2), 10)))

        assertEquals(0, StreakCalculator.currentStreak(h, TODAY))
    }

    @Test
    fun `un trou casse la serie`() {
        val days = validated(consecutiveDays(TODAY, 3)) + validated(consecutiveDays(TODAY.minusDays(4), 5))

        assertEquals(3, StreakCalculator.currentStreak(history(days), TODAY))
    }

    @Test
    fun `un jour sauve par un joker garde la serie`() {
        // 3 jours validés, hier sauvé par un joker, aujourd'hui pas encore validé.
        val days = validated(consecutiveDays(TODAY.minusDays(2), 3)) +
            savedByJoker(TODAY.minusDays(1))

        assertEquals(4, StreakCalculator.currentStreak(history(days), TODAY))
    }

    @Test
    fun `le record de serie se lit sur tout l'historique`() {
        val days = validated(consecutiveDays(TODAY.minusDays(20), 9)) +
            validated(consecutiveDays(TODAY, 4))

        assertEquals(9, StreakCalculator.bestStreak(history(days)))
    }

    @Test
    fun `record nul sans aucun jour`() {
        assertEquals(0, StreakCalculator.bestStreak(history(emptyMap())))
    }
}
