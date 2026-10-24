package com.felix.bestfelix.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.felix.bestfelix.domain.reward.LevelProgress
import com.felix.bestfelix.ui.theme.BestFelixTheme
import com.felix.bestfelix.ui.theme.tabular

/** Niveau et barre d'XP, en or : c'est la couleur de la récompense. */
@Composable
fun XpBar(
    progress: LevelProgress,
    modifier: Modifier = Modifier,
) {
    val accents = BestFelixTheme.accents
    val animated by animateFloatAsState(
        targetValue = progress.fraction,
        animationSpec = tween(durationMillis = 600),
        label = "xp",
    )

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = "Niveau ${progress.level}",
                style = MaterialTheme.typography.titleMedium.tabular(),
            )
            Text(
                text = "${progress.xp} XP",
                style = MaterialTheme.typography.labelLarge.tabular(),
                color = accents.reward,
            )
        }
        LinearProgressIndicator(
            progress = { animated },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .semantics {
                    contentDescription =
                        "Niveau ${progress.level}, ${progress.xpIntoLevel} XP sur ${progress.xpSpanOfLevel}"
                },
            color = accents.reward,
            trackColor = accents.neutralTrack,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
        Text(
            text = "Encore ${progress.xpToNextLevel} XP pour le niveau ${progress.level + 1}",
            style = MaterialTheme.typography.bodySmall.tabular(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
