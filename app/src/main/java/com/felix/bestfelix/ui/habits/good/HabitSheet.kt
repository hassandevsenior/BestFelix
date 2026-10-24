package com.felix.bestfelix.ui.habits.good

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
import com.felix.bestfelix.domain.model.Habit
import com.felix.bestfelix.ui.components.ConfirmDialog

/**
 * Création et modification d'une habitude. La suppression se fait depuis cette sheet,
 * avec confirmation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitSheet(
    editing: Habit?,
    onDismiss: () -> Unit,
    onCreate: (name: String, why: String?) -> Unit,
    onUpdate: (Habit) -> Unit,
    onDelete: (Habit) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by rememberSaveable(editing?.id) { mutableStateOf(editing?.name.orEmpty()) }
    var why by rememberSaveable(editing?.id) { mutableStateOf(editing?.why.orEmpty()) }
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
                text = if (editing == null) "Nouvelle habitude" else "Modifier l'habitude",
                style = MaterialTheme.typography.titleLarge,
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom") },
                placeholder = { Text("Lire 20 minutes") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = why,
                onValueChange = { why = it },
                label = { Text("Pourquoi c'est important (optionnel)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = {
                        val cleanWhy = why.trim().takeIf { it.isNotEmpty() }
                        if (editing == null) {
                            onCreate(name.trim(), cleanWhy)
                        } else {
                            onUpdate(editing.copy(name = name.trim(), why = cleanWhy))
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
            message = "Tout son historique de validations sera effacé, et les XP correspondants aussi.",
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
