package com.felix.bestfelix.ui.habits.quit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felix.bestfelix.domain.pain.PainConstants
import com.felix.bestfelix.domain.pain.RelapsePreview
import com.felix.bestfelix.ui.components.DateField
import com.felix.bestfelix.ui.theme.BestFelixTheme
import com.felix.bestfelix.ui.theme.tabular
import java.time.LocalDate

/**
 * Enregistrer une rechute. L'avertissement vient **d'abord** : on voit ce que ça coûte avant
 * de choisir la date, d'écrire une note et de confirmer.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelapseSheet(
    habitName: String,
    today: LocalDate,
    startDate: LocalDate,
    preview: RelapsePreview?,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    onConfirm: (date: LocalDate, note: String?) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val accents = BestFelixTheme.accents
    var note by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Rechute · $habitName",
                style = MaterialTheme.typography.titleLarge,
            )

            Surface(
                color = accents.painContainer,
                contentColor = accents.onPainContainer,
                shape = MaterialTheme.shapes.medium,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            text = "Rechuter revient à saccager " +
                                "${PainConstants.DAYS_ERASED_BY_RELAPSE} jours d'efforts, " +
                                "soit ${PainConstants.XP_PENALTY_PER_BROKEN_DAY} XP.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    if (preview != null) {
                        if (preview.alreadyBroken) {
                            Text(
                                text = "Ce jour est déjà compté comme non tenu : pas de pénalité en plus.",
                                style = MaterialTheme.typography.bodyMedium.tabular(),
                            )
                        } else {
                            Text(
                                text = "Ton solde passera de ${preview.balanceBefore} XP " +
                                    "à ${preview.balanceAfter} XP.",
                                style = MaterialTheme.typography.bodyMedium.tabular(),
                            )
                            preview.daysToRecord?.let { days ->
                                Text(
                                    text = "Il te faudra $days jours sans rechute pour revenir " +
                                        "à ton record de ${preview.bestBalance} XP.",
                                    style = MaterialTheme.typography.bodyMedium.tabular(),
                                )
                            }
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Quand ?", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedDate == today,
                        onClick = { onDateChange(today) },
                        label = { Text("Aujourd'hui") },
                    )
                    val yesterday = today.minusDays(1)
                    if (!yesterday.isBefore(startDate)) {
                        FilterChip(
                            selected = selectedDate == yesterday,
                            onClick = { onDateChange(yesterday) },
                            label = { Text("Hier") },
                        )
                    }
                }
                DateField(
                    label = "Choisir une date",
                    date = selectedDate,
                    onDateChange = { it?.let(onDateChange) },
                    minDate = startDate,
                    maxDate = today,
                )
            }

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (optionnelle)") },
                placeholder = { Text("Ce qui s'est passé, le déclencheur…") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = "Une note sera aussi ajoutée au bloc-notes, rubrique « Habitudes à abandonner ».",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = {
                        onConfirm(selectedDate, note.takeIf { it.isNotBlank() })
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accents.pain,
                        contentColor = accents.onPain,
                    ),
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Confirmer la rechute")
                }
                TextButton(onClick = onDismiss) { Text("Annuler") }
            }
        }
    }
}
