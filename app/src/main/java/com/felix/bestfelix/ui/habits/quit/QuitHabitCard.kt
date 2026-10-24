package com.felix.bestfelix.ui.habits.quit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felix.bestfelix.domain.pain.PainConstants
import com.felix.bestfelix.ui.components.BalanceChart
import com.felix.bestfelix.ui.components.InfoButton
import com.felix.bestfelix.ui.theme.BestFelixTheme
import com.felix.bestfelix.ui.theme.tabular

/** Explication de la règle, construite depuis les constantes du domaine. */
private val RULE_EXPLANATION =
    "+${PainConstants.XP_PER_HELD_DAY} XP par jour tenu. " +
        "−${PainConstants.XP_PENALTY_PER_BROKEN_DAY} XP par jour non tenu, " +
        "soit ${PainConstants.DAYS_ERASED_BY_RELAPSE} jours d'effort effacés."

@Composable
fun QuitHabitCard(
    row: QuitRow,
    onEdit: () -> Unit,
    onRelapse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accents = BestFelixTheme.accents
    val balanceColor = if (row.pain.isDebt) accents.pain else accents.success

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = row.quitHabit.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onEdit),
                )
                InfoButton(
                    title = "Comment se calcule le solde",
                    explanation = RULE_EXPLANATION,
                )
            }

            Column {
                Text(
                    text = if (row.pain.isDebt) {
                        "Dette : ${-row.pain.balance} XP à rembourser"
                    } else {
                        "${row.pain.balance} XP"
                    },
                    style = MaterialTheme.typography.headlineMedium.tabular(),
                    fontWeight = FontWeight.Bold,
                    color = balanceColor,
                )
                Text(
                    text = "Meilleur solde atteint : ${row.pain.bestBalance} XP",
                    style = MaterialTheme.typography.bodySmall.tabular(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            BalanceChart(points = row.pain.series)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "${row.pain.heldStreak} jours tenus d'affilée",
                    style = MaterialTheme.typography.bodyMedium.tabular(),
                )
                Text(
                    text = if (row.pain.brokenDaysCount == 1) {
                        "1 jour non tenu"
                    } else {
                        "${row.pain.brokenDaysCount} jours non tenus"
                    },
                    style = MaterialTheme.typography.bodyMedium.tabular(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Button(
                onClick = onRelapse,
                colors = ButtonDefaults.buttonColors(
                    containerColor = accents.pain,
                    contentColor = accents.onPain,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Filled.HeartBroken,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Text("J'ai rechuté", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
