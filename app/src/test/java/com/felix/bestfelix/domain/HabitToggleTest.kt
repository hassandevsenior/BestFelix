package com.felix.bestfelix.domain

import com.felix.bestfelix.domain.reward.HabitToggle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HabitToggleTest {

    /** Une deuxième habitude jamais validée : la journée ne peut donc pas devenir parfaite. */
    private fun autreHabitudeNonValidee() = history(emptyMap(), id = 2)

    @Test
    fun `valider le septieme jour d'affilee rapporte 15 XP`() {
        val h = history(validated(consecutiveDays(TODAY.minusDays(1), 6)))

        val outcome = HabitToggle.simulate(
            listOf(h, autreHabitudeNonValidee()),
            habitId = 1,
            date = TODAY,
        )!!

        assertTrue(outcome.willBeMarked)
        assertEquals(15, outcome.xpGained)
        assertFalse(outcome.becomesPerfectDay)
    }

    @Test
    fun `completer la derniere habitude du jour ajoute le bonus de journee parfaite`() {
        val deja = history(validated(TODAY), id = 1)
        val restante = autreHabitudeNonValidee()

        val outcome = HabitToggle.simulate(listOf(deja, restante), habitId = 2, date = TODAY)!!

        assertTrue(outcome.becomesPerfectDay)
        assertEquals(10 + 20, outcome.xpGained)
    }

    @Test
    fun `annuler une validation retire ses XP`() {
        val h = history(validated(TODAY))

        val outcome = HabitToggle.simulate(
            listOf(h, autreHabitudeNonValidee()),
            habitId = 1,
            date = TODAY,
        )!!

        assertFalse(outcome.willBeMarked)
        assertEquals(-10, outcome.xpGained)
    }

    @Test
    fun `annuler la seule validation du jour retire aussi le bonus de journee parfaite`() {
        val h = history(validated(TODAY))

        val outcome = HabitToggle.simulate(listOf(h), habitId = 1, date = TODAY)!!

        assertEquals(-(10 + 20), outcome.xpGained)
    }

    @Test
    fun `pas de validation avant la creation de l'habitude`() {
        val h = history(emptyMap(), createdAt = TODAY)

        assertNull(HabitToggle.simulate(listOf(h), habitId = 1, date = TODAY.minusDays(1)))
    }
}
