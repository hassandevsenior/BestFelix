package com.felix.bestfelix.ui.finance.incomes

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.felix.bestfelix.core.format.MoneyFormat
import com.felix.bestfelix.domain.model.Income
import com.felix.bestfelix.ui.components.ConfirmDialog
import com.felix.bestfelix.ui.components.DateField
import com.felix.bestfelix.ui.components.MoneyField
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeSheet(
    editing: Income?,
    today: LocalDate,
    onDismiss: () -> Unit,
    onCreate: (amount: Long, date: LocalDate, source: String) -> Unit,
    onUpdate: (Income) -> Unit,
    onDelete: (Income) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var digits by rememberSaveable(editing?.id) { mutableStateOf(editing?.amount?.toString().orEmpty()) }
    var source by rememberSaveable(editing?.id) { mutableStateOf(editing?.source.orEmpty()) }
    var date by remember { mutableStateOf(editing?.date ?: today) }
    var confirmDelete by remember { mutableStateOf(false) }

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
                text = if (editing == null) "Nouvelle entrée" else "Modifier l'entrée",
                style = MaterialTheme.typography.titleLarge,
            )

            MoneyField(label = "Montant", digits = digits, onDigitsChange = { digits = it })

            OutlinedTextField(
                value = source,
                onValueChange = { source = it },
                label = { Text("Source") },
                placeholder = { Text("JDS, freelance, vente…") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

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
                        if (editing == null) {
                            onCreate(amount, date, source.trim())
                        } else {
                            onUpdate(editing.copy(amount = amount, date = date, source = source.trim()))
                        }
                        onDismiss()
                    },
                    enabled = amount > 0L && source.isNotBlank(),
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Enregistrer")
                }
                if (editing != null) {
                    TextButton(onClick = { confirmDelete = true }) {
                        Text("Supprimer", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    if (confirmDelete && editing != null) {
        ConfirmDialog(
            title = "Supprimer cette entrée ?",
            message = "${MoneyFormat.format(editing.amount)} · ${editing.source}",
            confirmLabel = "Supprimer",
            onConfirm = {
                confirmDelete = false
                onDelete(editing)
                onDismiss()
            },
            onDismiss = { confirmDelete = false },
        )
    }
}
