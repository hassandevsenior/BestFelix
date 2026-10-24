package com.felix.bestfelix.domain

import com.felix.bestfelix.domain.reward.LevelCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class LevelCalculatorTest {

    @Test
    fun `seuils de niveau = 50 x n x (n - 1)`() {
        assertEquals(0, LevelCalculator.xpRequiredFor(1))
        assertEquals(100, LevelCalculator.xpRequiredFor(2))
        assertEquals(300, LevelCalculator.xpRequiredFor(3))
        assertEquals(600, LevelCalculator.xpRequiredFor(4))
        assertEquals(1000, LevelCalculator.xpRequiredFor(5))
    }

    @Test
    fun `niveau atteint selon les XP cumules`() {
        assertEquals(1, LevelCalculator.levelFor(0))
        assertEquals(1, LevelCalculator.levelFor(99))
        assertEquals(2, LevelCalculator.levelFor(100))
        assertEquals(2, LevelCalculator.levelFor(299))
        assertEquals(3, LevelCalculator.levelFor(300))
        assertEquals(4, LevelCalculator.levelFor(600))
    }

    @Test
    fun `progression dans le niveau en cours`() {
        val progress = LevelCalculator.progressFor(200)

        assertEquals(2, progress.level)
        assertEquals(100, progress.levelFloor)
        assertEquals(300, progress.nextLevelAt)
        assertEquals(100, progress.xpIntoLevel)
        assertEquals(100, progress.xpToNextLevel)
        assertEquals(0.5f, progress.fraction, 0.0001f)
    }
}
