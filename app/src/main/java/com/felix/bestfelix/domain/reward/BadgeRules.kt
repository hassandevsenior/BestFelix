package com.felix.bestfelix.domain.reward

import com.felix.bestfelix.domain.reward.RewardConstants.BADGE_THRESHOLDS

data class Badge(
    /** Record de série nécessaire, en jours. */
    val days: Int,
    val earned: Boolean,
)

object BadgeRules {

    /** Les six badges, dans l'ordre, avec ceux déjà décrochés. */
    fun badges(bestStreak: Int): List<Badge> =
        BADGE_THRESHOLDS.map { Badge(days = it, earned = bestStreak >= it) }

    /** Prochain palier à atteindre, ou null si les six sont décrochés. */
    fun nextBadge(bestStreak: Int): Int? = BADGE_THRESHOLDS.firstOrNull { it > bestStreak }
}
