package com.felix.bestfelix.ui.projects.saving

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.unit.dp
import com.felix.bestfelix.core.format.MoneyFormat
import com.felix.bestfelix.ui.components.DateField
import com.felix.bestfelix.ui.components.MoneyField
import java.time.LocalDate

/** Ajout d'un versement sur un objectif d'épargne. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositSheet(
    goalTitle: String,
    today: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (date: LocalDate, amount: Long) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var digits by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(today) }
    val amount = MoneyFormat.parse(digits) ?: 0L

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
                text = "Versement · $goalTitle",
                style = MaterialTheme.typography.titleLarge,
            )

            MoneyField(label = "Montant", digits = digits, onDigitsChange = { digits = it })

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Date", style = MaterialTheme.typography.labelLarge)
                DateField(
                    label = "Choisir une date",
                    date = date,
                    onDateChange = { it?.let { chosen -> date = chosen } },
                    maxDate = today,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = {
                        onConfirm(date, amount)
                        onDismiss()
                    },
                    enabled = amount > 0L,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Enregistrer")
                }
                TextButton(onClick = onDismiss) { Text("Annuler") }
            }
        }
    }
}
