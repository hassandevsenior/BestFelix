package com.felix.bestfelix.ui.finance.credits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felix.bestfelix.core.format.DateFormatFr
import com.felix.bestfelix.core.format.MoneyFormat
import com.felix.bestfelix.domain.model.CreditDirection
import com.felix.bestfelix.ui.finance.CreditRow
import com.felix.bestfelix.ui.theme.BestFelixTheme
import com.felix.bestfelix.ui.theme.tabular

@Composable
fun CreditCard(
    row: CreditRow,
    onClick: () -> Unit,
    onRepay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accents = BestFelixTheme.accents
    val credit = row.credit
    val iOwe = credit.direction == CreditDirection.I_OWE

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (iOwe) {
                        "Je dois à ${credit.counterparty}"
                    } else {
                        "${credit.counterparty} me doit"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                when {
                    row.settled -> Pill(
                        text = "Soldé",
                        container = accents.successContainer,
                        content = accents.onSuccessContainer,
                    )
                    row.overdue -> Pill(
                        text = "En retard",
                        container = accents.painContainer,
                        content = accents.onPainContainer,
                    )
                }
            }

            Text(
                text = if (row.settled) {
                    MoneyFormat.format(credit.amount)
                } else {
                    "Reste ${MoneyFormat.format(row.remaining)}"
                },
                style = MaterialTheme.typography.headlineSmall.tabular(),
                fontWeight = FontWeight.Bold,
                color = if (iOwe) accents.pain else accents.success,
            )

            if (row.paid > 0 && !row.settled) {
                Text(
                    text = "Déjà ${if (iOwe) "remboursé" else "reçu"} : " +
                        "${MoneyFormat.grouped(row.paid)} / ${MoneyFormat.format(credit.amount)}",
                    style = MaterialTheme.typography.bodySmall.tabular(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (!credit.reason.isNullOrBlank()) {
                Text(
                    text = credit.reason,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            credit.dueDate?.let { due ->
                Text(
                    text = "Date limite : ${DateFormatFr.short(due)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (row.overdue) accents.pain else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (!row.settled) {
                Button(onClick = onRepay, modifier = Modifier.fillMaxWidth()) {
                    Text(if (iOwe) "Rembourser" else "Reçu")
                }
            }
        }
    }
}

@Composable
private fun Pill(text: String, container: Color, content: Color) {
    Surface(color = container, contentColor = content, shape = MaterialTheme.shapes.small) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}
