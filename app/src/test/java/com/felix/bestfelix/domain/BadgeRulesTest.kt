package com.felix.bestfelix.domain

import com.felix.bestfelix.domain.reward.BadgeRules
import org.junit.Assert.assertEquals
import org.junit.Test

class BadgeRulesTest {

    @Test
    fun `badges decroches selon le record de serie`() {
        val badges = BadgeRules.badges(bestStreak = 21)

        assertEquals(listOf(3, 7, 21, 30, 66, 100), badges.map { it.days })
        assertEquals(listOf(true, true, true, false, false, false), badges.map { it.earned })
    }

    @Test
    fun `prochain palier`() {
        assertEquals(3, BadgeRules.nextBadge(0))
        assertEquals(30, BadgeRules.nextBadge(21))
        assertEquals(null, BadgeRules.nextBadge(100))
    }
}
