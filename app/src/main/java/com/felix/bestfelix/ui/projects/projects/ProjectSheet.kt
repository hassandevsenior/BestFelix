package com.felix.bestfelix.ui.projects.projects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import com.felix.bestfelix.domain.model.Priority
import com.felix.bestfelix.domain.model.Project
import com.felix.bestfelix.domain.model.ProjectStatus
import com.felix.bestfelix.ui.components.ConfirmDialog
import com.felix.bestfelix.ui.components.DateField
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSheet(
    editing: Project?,
    today: LocalDate,
    onDismiss: () -> Unit,
    onCreate: (String, String, String?, Priority, ProjectStatus, LocalDate?) -> Unit,
    onUpdate: (Project) -> Unit,
    onDelete: (Project) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by rememberSaveable(editing?.id) { mutableStateOf(editing?.title.orEmpty()) }
    var context by rememberSaveable(editing?.id) { mutableStateOf(editing?.context.orEmpty()) }
    var currentTask by rememberSaveable(editing?.id) { mutableStateOf(editing?.currentTask.orEmpty()) }
    var priority by remember { mutableStateOf(editing?.priority ?: Priority.MEDIUM) }
    var status by remember { mutableStateOf(editing?.status ?: ProjectStatus.ACTIVE) }
    var dueDate by remember { mutableStateOf(editing?.dueDate) }
    var confirmDelete by remember { mutableStateOf(false) }

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
                text = if (editing == null) "Nouveau projet" else "Modifier le projet",
                style = MaterialTheme.typography.titleLarge,
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = context,
                onValueChange = { context = it },
                label = { Text("Entreprise ou contexte") },
                placeholder = { Text("JDS, PrimeTech, un client…") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = currentTask,
                onValueChange = { currentTask = it },
                label = { Text("Je suis en cours sur… (optionnel)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Priorité", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Priority.entries.forEach { candidate ->
                        FilterChip(
                            selected = priority == candidate,
                            onClick = { priority = candidate },
                            label = { Text(candidate.label) },
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Statut", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProjectStatus.entries.forEach { candidate ->
                        FilterChip(
                            selected = status == candidate,
                            onClick = { status = candidate },
                            label = { Text(candidate.label) },
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Échéance (optionnelle)", style = MaterialTheme.typography.labelLarge)
                DateField(
                    label = "Ajouter une échéance",
                    date = dueDate,
                    onDateChange = { dueDate = it },
                    minDate = null,
                    maxDate = null,
                    clearable = true,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = {
                        val task = currentTask.trim().takeIf { it.isNotEmpty() }
                        if (editing == null) {
                            onCreate(title.trim(), context.trim(), task, priority, status, dueDate)
                        } else {
                            onUpdate(
                                editing.copy(
                                    title = title.trim(),
                                    context = context.trim(),
                                    currentTask = task,
                                    priority = priority,
                                    status = status,
                                    dueDate = dueDate,
                                ),
                            )
                        }
                        onDismiss()
                    },
                    enabled = title.isNotBlank() && context.isNotBlank(),
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
            title = "Supprimer « ${editing.title} » ?",
            message = "Le projet sera retiré de la liste.",
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
