package com.felix.bestfelix.ui.notes

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import com.felix.bestfelix.domain.model.Note
import com.felix.bestfelix.domain.model.NoteCategory
import com.felix.bestfelix.ui.components.ConfirmDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteSheet(
    editing: Note?,
    defaultCategory: NoteCategory,
    onDismiss: () -> Unit,
    onCreate: (NoteCategory, String?, String) -> Unit,
    onUpdate: (Note) -> Unit,
    onDelete: (Note) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var category by remember { mutableStateOf(editing?.category ?: defaultCategory) }
    var title by rememberSaveable(editing?.id) { mutableStateOf(editing?.title.orEmpty()) }
    var body by rememberSaveable(editing?.id) { mutableStateOf(editing?.body.orEmpty()) }
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
                text = if (editing == null) "Nouvelle note" else "Modifier la note",
                style = MaterialTheme.typography.titleLarge,
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                NoteCategory.entries.forEach { candidate ->
                    FilterChip(
                        selected = category == candidate,
                        onClick = { category = candidate },
                        label = { Text(candidate.shortLabel) },
                    )
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titre (optionnel)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                label = { Text("Note") },
                minLines = 5,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = {
                        val cleanTitle = title.trim().takeIf { it.isNotEmpty() }
                        if (editing == null) {
                            onCreate(category, cleanTitle, body.trim())
                        } else {
                            onUpdate(
                                editing.copy(
                                    category = category,
                                    title = cleanTitle,
                                    body = body.trim(),
                                ),
                            )
                        }
                        onDismiss()
                    },
                    enabled = body.isNotBlank(),
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
            title = "Supprimer cette note ?",
            message = editing.title ?: editing.body.take(80),
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
