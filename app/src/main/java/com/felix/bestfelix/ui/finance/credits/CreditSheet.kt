package com.felix.bestfelix.ui.finance.credits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.felix.bestfelix.core.format.DateFormatFr
import com.felix.bestfelix.core.format.MoneyFormat
import com.felix.bestfelix.domain.model.Credit
import com.felix.bestfelix.domain.model.CreditDirection
import com.felix.bestfelix.domain.model.Repayment
import com.felix.bestfelix.ui.components.ConfirmDialog
import com.felix.bestfelix.ui.components.DateField
import com.felix.bestfelix.ui.components.MoneyField
import com.felix.bestfelix.ui.finance.CreditRow
import com.felix.bestfelix.ui.theme.tabular
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditSheet(
    editing: CreditRow?,
    onDismiss: () -> Unit,
    onCreate: (CreditDirection, String, Long, String?, LocalDate?) -> Unit,
    onUpdate: (Credit) -> Unit,
    onDelete: (Credit) -> Unit,
    onDeleteRepayment: (Repayment) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val credit = editing?.credit
    var direction by remember { mutableStateOf(credit?.direction ?: CreditDirection.I_OWE) }
    var counterparty by rememberSaveable(credit?.id) { mutableStateOf(credit?.counterparty.orEmpty()) }
    var digits by rememberSaveable(credit?.id) { mutableStateOf(credit?.amount?.toString().orEmpty()) }
    var reason by rememberSaveable(credit?.id) { mutableStateOf(credit?.reason.orEmpty()) }
    var dueDate by remember { mutableStateOf(credit?.dueDate) }
    var confirmDelete by remember { mutableStateOf(false) }

    val amount = MoneyFormat.parse(digits) ?: 0L

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = if (credit == null) "Nouveau crédit" else "Modifier le crédit",
                style = MaterialTheme.typography.titleLarge,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = direction == CreditDirection.I_OWE,
                    onClick = { direction = CreditDirection.I_OWE },
                    label = { Text("Je dois") },
                )
                FilterChip(
                    selected = direction == CreditDirection.OWED_TO_ME,
                    onClick = { direction = CreditDirection.OWED_TO_ME },
                    label = { Text("On me doit") },
                )
            }

            OutlinedTextField(
                value = counterparty,
                onValueChange = { counterparty = it },
                label = { Text("Qui") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            MoneyField(label = "Montant", digits = digits, onDigitsChange = { digits = it })

            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Motif (optionnel)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Date limite (optionnelle)", style = MaterialTheme.typography.labelLarge)
                DateField(
                    label = "Ajouter une date limite",
                    date = dueDate,
                    onDateChange = { dueDate = it },
                    clearable = true,
                )
            }

            if (editing != null && editing.repayments.isNotEmpty()) {
                HorizontalDivider()
                Text(
                    text = "Remboursements (${editing.repayments.size})",
                    style = MaterialTheme.typography.labelLarge,
                )
                editing.repayments.forEach { repayment ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = DateFormatFr.short(repayment.date),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = MoneyFormat.format(repayment.amount),
                            style = MaterialTheme.typography.bodyMedium.tabular(),
                        )
                        IconButton(onClick = { onDeleteRepayment(repayment) }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Supprimer ce remboursement",
                            )
                        }
                    }
                }
                HorizontalDivider()
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = {
                        val cleanReason = reason.trim().takeIf { it.isNotEmpty() }
                        if (credit == null) {
                            onCreate(direction, counterparty.trim(), amount, cleanReason, dueDate)
                        } else {
                            onUpdate(
                                credit.copy(
                                    direction = direction,
                                    counterparty = counterparty.trim(),
                                    amount = amount,
                                    reason = cleanReason,
                                    dueDate = dueDate,
                                ),
                            )
                        }
                        onDismiss()
                    },
                    enabled = counterparty.isNotBlank() && amount > 0L,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Enregistrer")
                }
                if (credit != null) {
                    TextButton(onClick = { confirmDelete = true }) {
                        Text("Supprimer", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    if (confirmDelete && credit != null) {
        ConfirmDialog(
            title = "Supprimer ce crédit ?",
            message = "Le crédit de ${credit.counterparty} et ses remboursements seront effacés.",
            confirmLabel = "Supprimer",
            onConfirm = {
                confirmDelete = false
                onDelete(credit)
                onDismiss()
            },
            onDismiss = { confirmDelete = false },
        )
    }
}
