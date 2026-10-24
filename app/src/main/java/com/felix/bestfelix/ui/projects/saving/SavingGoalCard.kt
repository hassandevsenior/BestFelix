package com.felix.bestfelix.ui.projects.saving

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felix.bestfelix.core.format.DateFormatFr
import com.felix.bestfelix.core.format.MoneyFormat
import com.felix.bestfelix.ui.theme.BestFelixTheme
import com.felix.bestfelix.ui.theme.tabular

@Composable
fun SavingGoalCard(
    row: GoalRow,
    onClick: () -> Unit,
    onAddDeposit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accents = BestFelixTheme.accents
    val progress = row.progress
    val barColor = if (progress.isReached) accents.success else accents.reward

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = row.goal.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (!row.goal.purpose.isNullOrBlank()) {
                        Text(
                            text = row.goal.purpose,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                if (progress.isReached) {
                    Surface(
                        color = accents.successContainer,
                        contentColor = accents.onSuccessContainer,
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text(
                            text = "Atteint",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        )
                    }
                }
            }

            LinearProgressIndicator(
                progress = { progress.fraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = barColor,
                trackColor = accents.neutralTrack,
                gapSize = 0.dp,
                drawStopIndicator = {},
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${MoneyFormat.grouped(progress.saved)} / ${MoneyFormat.format(progress.target)}",
                    style = MaterialTheme.typography.bodyMedium.tabular(),
                )
                Text(
                    text = "${progress.percent} %",
                    style = MaterialTheme.typography.bodyMedium.tabular(),
                    color = barColor,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            progress.monthlyNeeded?.let { monthly ->
                val due = row.goal.dueDate
                Text(
                    text = "À mettre de côté : ${MoneyFormat.format(monthly)} par mois" +
                        if (due != null) " jusqu'au ${DateFormatFr.short(due)}" else "",
                    style = MaterialTheme.typography.bodySmall.tabular(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (progress.monthlyNeeded == null && row.goal.dueDate != null && !progress.isReached) {
                Text(
                    text = "Échéance dépassée : ${DateFormatFr.short(row.goal.dueDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = accents.pain,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onAddDeposit) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Text("Versement", modifier = Modifier.padding(start = 6.dp))
                }
            }
        }
    }
}
