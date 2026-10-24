package com.felix.bestfelix.ui.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felix.bestfelix.core.format.DateFormatFr
import com.felix.bestfelix.ui.components.ProgressRing
import com.felix.bestfelix.ui.components.XpBar
import com.felix.bestfelix.ui.theme.BestFelixTheme

/** En-tête du matin : la date, l'anneau du jour, le niveau et la barre d'XP. */
@Composable
fun TodayHeader(
    state: TodayUiState,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accents = BestFelixTheme.accents

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Salut Félix",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = DateFormatFr.full(state.date).replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Réglages",
                    )
                }
            }

            Row(
                modifier = Modifier.padding(end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProgressRing(done = state.validatedCount, total = state.habitCount)
                XpBar(progress = state.level, modifier = Modifier.weight(1f))
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
        }
    }
}
