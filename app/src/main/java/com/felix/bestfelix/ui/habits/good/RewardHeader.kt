package com.felix.bestfelix.ui.habits.good

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.felix.bestfelix.domain.reward.Badge
import com.felix.bestfelix.ui.components.XpBar
import com.felix.bestfelix.ui.theme.BestFelixTheme
import com.felix.bestfelix.ui.theme.tabular

/** En-tête de la récompense : niveau, XP, record de série et badges. */
@Composable
fun RewardHeader(
    state: GoodHabitsUiState,
    modifier: Modifier = Modifier,
) {
    val accents = BestFelixTheme.accents

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            XpBar(progress = state.level)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = accents.reward,
                    )
                    Text(
                        text = "${state.validatedTodayCount} / ${state.habitCount} aujourd'hui",
                        style = MaterialTheme.typography.bodyMedium.tabular(),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = accents.reward,
                    )
                    Text(
                        text = "Record : ${state.bestStreakOverall} j",
                        style = MaterialTheme.typography.bodyMedium.tabular(),
                    )
                }
            }

            if (state.perfectToday) {
                Surface(
                    color = accents.rewardContainer,
                    contentColor = accents.onRewardContainer,
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        text = "Journée parfaite · +20 XP",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }

            BadgeRow(badges = state.badges)
        }
    }
}

@Composable
private fun BadgeRow(
    badges: List<Badge>,
    modifier: Modifier = Modifier,
) {
    val accents = BestFelixTheme.accents
    val earned = badges.count { it.earned }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "$earned badges de série sur ${badges.size}"
            },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        badges.forEach { badge ->
            Surface(
                shape = CircleShape,
                color = if (badge.earned) accents.rewardContainer else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (badge.earned) accents.onRewardContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(40.dp)
                    .alpha(if (badge.earned) 1f else 0.5f)
                    .clearAndSetSemantics { },
            ) {
                Column(
                    modifier = Modifier.padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = "${badge.days}",
                        style = MaterialTheme.typography.labelSmall.tabular(),
                    )
                }
            }
        }
    }
}
