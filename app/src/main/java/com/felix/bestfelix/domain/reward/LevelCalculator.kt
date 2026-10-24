package com.felix.bestfelix.domain.reward

import com.felix.bestfelix.domain.reward.RewardConstants.LEVEL_FACTOR

/** Progression de niveau, dérivée des XP cumulés. */
data class LevelProgress(
    val level: Int,
    val xp: Int,
    /** XP cumulés exigés pour le niveau en cours. */
    val levelFloor: Int,
    /** XP cumulés exigés pour le niveau suivant. */
    val nextLevelAt: Int,
) {
    val xpIntoLevel: Int get() = xp - levelFloor
    val xpSpanOfLevel: Int get() = nextLevelAt - levelFloor
    val xpToNextLevel: Int get() = nextLevelAt - xp

    /** Entre 0 et 1, pour la barre d'XP. */
    val fraction: Float
        get() = if (xpSpanOfLevel <= 0) 0f else xpIntoLevel.toFloat() / xpSpanOfLevel
}

object LevelCalculator {

    /** XP cumulés nécessaires pour atteindre le niveau [level] : 50 × n × (n − 1). */
    fun xpRequiredFor(level: Int): Int {
        val n = level.coerceAtLeast(1)
        return LEVEL_FACTOR * n * (n - 1)
    }

    /** Niveau atteint avec [xp] XP cumulés. Le niveau 1 commence à 0 XP. */
    fun levelFor(xp: Int): Int {
        if (xp <= 0) return 1
        var level = 1
        while (xpRequiredFor(level + 1) <= xp) level++
        return level
    }

    fun progressFor(xp: Int): LevelProgress {
        val safeXp = xp.coerceAtLeast(0)
        val level = levelFor(safeXp)
        return LevelProgress(
            level = level,
            xp = safeXp,
            levelFloor = xpRequiredFor(level),
            nextLevelAt = xpRequiredFor(level + 1),
        )
    }
}
