package com.felix.bestfelix.ui.projects.saving

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
import com.felix.bestfelix.domain.model.Deposit
import com.felix.bestfelix.domain.model.SavingGoal
import com.felix.bestfelix.ui.components.ConfirmDialog
import com.felix.bestfelix.ui.components.DateField
import com.felix.bestfelix.ui.components.MoneyField
import com.felix.bestfelix.ui.theme.tabular
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingGoalSheet(
    editing: GoalRow?,
    onDismiss: () -> Unit,
    onCreate: (title: String, purpose: String?, target: Long, dueDate: LocalDate?) -> Unit,
    onUpdate: (SavingGoal) -> Unit,
    onDelete: (SavingGoal) -> Unit,
    onDeleteDeposit: (Deposit) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val goal = editing?.goal
    var title by rememberSaveable(goal?.id) { mutableStateOf(goal?.title.orEmpty()) }
    var purpose by rememberSaveable(goal?.id) { mutableStateOf(goal?.purpose.orEmpty()) }
    var target by rememberSaveable(goal?.id) {
        mutableStateOf(goal?.targetAmount?.toString().orEmpty())
    }
    var dueDate by remember { mutableStateOf(goal?.dueDate) }
    var confirmDelete by remember { mutableStateOf(false) }

    val targetAmount = MoneyFormat.parse(target) ?: 0L

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
                text = if (goal == null) "Nouvel objectif d'épargne" else "Modifier l'objectif",
                style = MaterialTheme.typography.titleLarge,
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titre") },
                placeholder = { Text("Ordinateur portable") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = purpose,
                onValueChange = { purpose = it },
                label = { Text("Pour quoi (optionnel)") },
                placeholder = { Text("JDS, perso…") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            MoneyField(
                label = "Montant cible",
                digits = target,
                onDigitsChange = { target = it },
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Échéance (optionnelle)", style = MaterialTheme.typography.labelLarge)
                DateField(
                    label = "Ajouter une échéance",
                    date = dueDate,
                    onDateChange = { dueDate = it },
                    clearable = true,
                )
            }

            if (editing != null && editing.deposits.isNotEmpty()) {
                HorizontalDivider()
                Text(
                    text = "Versements (${editing.deposits.size})",
                    style = MaterialTheme.typography.labelLarge,
                )
                editing.deposits.forEach { deposit ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = DateFormatFr.short(deposit.date),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = MoneyFormat.format(deposit.amount),
                            style = MaterialTheme.typography.bodyMedium.tabular(),
                        )
                        IconButton(onClick = { onDeleteDeposit(deposit) }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Supprimer ce versement",
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
                        val cleanPurpose = purpose.trim().takeIf { it.isNotEmpty() }
                        if (goal == null) {
                            onCreate(title.trim(), cleanPurpose, targetAmount, dueDate)
                        } else {
                            onUpdate(
                                goal.copy(
                                    title = title.trim(),
                                    purpose = cleanPurpose,
                                    targetAmount = targetAmount,
                                    dueDate = dueDate,
                                ),
                            )
                        }
                        onDismiss()
                    },
                    enabled = title.isNotBlank() && targetAmount > 0L,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Enregistrer")
                }
                if (goal != null) {
                    TextButton(onClick = { confirmDelete = true }) {
                        Text("Supprimer", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    if (confirmDelete && goal != null) {
        ConfirmDialog(
            title = "Supprimer « ${goal.title} » ?",
            message = "L'objectif et tous ses versements seront effacés.",
            confirmLabel = "Supprimer",
            onConfirm = {
                confirmDelete = false
                onDelete(goal)
                onDismiss()
            },
            onDismiss = { confirmDelete = false },
        )
    }
}
