package com.felix.bestfelix.ui.habits.quit

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
import com.felix.bestfelix.domain.model.QuitHabit
import com.felix.bestfelix.ui.components.ConfirmDialog
import com.felix.bestfelix.ui.components.DateField
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuitHabitSheet(
    editing: QuitHabit?,
    today: LocalDate,
    onDismiss: () -> Unit,
    onCreate: (name: String, startDate: LocalDate) -> Unit,
    onUpdate: (QuitHabit) -> Unit,
    onDelete: (QuitHabit) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by rememberSaveable(editing?.id) { mutableStateOf(editing?.name.orEmpty()) }
    var startDate by remember { mutableStateOf(editing?.startDate ?: today) }
    var confirmDelete by remember { mutableStateOf(false) }

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
                text = if (editing == null) "Habitude à abandonner" else "Modifier l'habitude",
                style = MaterialTheme.typography.titleLarge,
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom") },
                placeholder = { Text("Cigarette") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Début du compteur", style = MaterialTheme.typography.labelLarge)
                DateField(
                    label = "Choisir une date",
                    date = startDate,
                    onDateChange = { it?.let { date -> startDate = date } },
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
                            onCreate(name.trim(), startDate)
                        } else {
                            onUpdate(editing.copy(name = name.trim(), startDate = startDate))
                        }
                        onDismiss()
                    },
                    enabled = name.isNotBlank(),
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
            title = "Supprimer « ${editing.name} » ?",
            message = "Son compteur et toutes ses rechutes seront effacés.",
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
