package com.felix.bestfelix.domain

import com.felix.bestfelix.domain.reward.JokerEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class JokerEngineTest {

    @Test
    fun `un joker gagne tous les 7 jours de serie, plafonne a 2`() {
        val sept = history(validated(consecutiveDays(TODAY, 7)))
        val quatorze = history(validated(consecutiveDays(TODAY, 14)))
        val vingtUn = history(validated(consecutiveDays(TODAY, 21)))

        assertEquals(0, JokerEngine.stock(history(validated(consecutiveDays(TODAY, 6))), TODAY))
        assertEquals(1, JokerEngine.stock(sept, TODAY))
        assertEquals(2, JokerEngine.stock(quatorze, TODAY))
        assertEquals(2, JokerEngine.stock(vingtUn, TODAY))
    }

    @Test
    fun `un jour sauve consomme un joker`() {
        // 7 jours validés (1 joker gagné), puis un jour sauvé par ce joker, puis 2 jours validés.
        val days = validated(consecutiveDays(TODAY.minusDays(3), 7)) +
            savedByJoker(TODAY.minusDays(2)) +
            validated(consecutiveDays(TODAY, 2))

        assertEquals(0, JokerEngine.stock(history(days), TODAY))
    }

    @Test
    fun `la serie repart de zero apres un trou, donc pas de nouveau joker`() {
        val days = validated(consecutiveDays(TODAY.minusDays(10), 6)) +
            validated(consecutiveDays(TODAY, 6))

        assertEquals(0, JokerEngine.stock(history(days), TODAY))
    }

    @Test
    fun `hier est sauve automatiquement si avant-hier est valide et qu'il reste un joker`() {
        val days = validated(consecutiveDays(TODAY.minusDays(2), 7))

        assertTrue(JokerEngine.shouldSaveYesterday(history(days), TODAY))
    }

    @Test
    fun `pas de sauvetage sans joker en stock`() {
        val days = validated(consecutiveDays(TODAY.minusDays(2), 6))

        assertFalse(JokerEngine.shouldSaveYesterday(history(days), TODAY))
    }

    @Test
    fun `pas de sauvetage si hier est deja valide`() {
        val days = validated(consecutiveDays(TODAY.minusDays(1), 8))

        assertFalse(JokerEngine.shouldSaveYesterday(history(days), TODAY))
    }

    @Test
    fun `pas de sauvetage si avant-hier n'est pas valide`() {
        val days = validated(consecutiveDays(TODAY.minusDays(3), 7))

        assertFalse(JokerEngine.shouldSaveYesterday(history(days), TODAY))
    }
}
