package com.felix.bestfelix.ui.projects.projects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.felix.bestfelix.domain.model.NoteCategory
import com.felix.bestfelix.domain.model.Project
import com.felix.bestfelix.ui.components.EmptyState
import com.felix.bestfelix.ui.notes.NotesLink

@Composable
fun ProjectsPane(
    onOpenNotes: (NoteCategory) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProjectsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDone by rememberSaveable { mutableStateOf(false) }
    var sheet by remember { mutableStateOf<ProjectSheetTarget?>(null) }

    val rows = if (showDone) state.done else state.active

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FilterChip(
                        selected = !showDone,
                        onClick = { showDone = false },
                        label = { Text("Actifs (${state.active.size})") },
                    )
                    FilterChip(
                        selected = showDone,
                        onClick = { showDone = true },
                        label = { Text("Terminés (${state.done.size})") },
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    NotesLink(category = NoteCategory.PROJECTS, onOpenNotes = onOpenNotes)
                }
            }

            if (!state.loading && rows.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Outlined.WorkOutline,
                        title = if (showDone) "Aucun projet terminé" else "Aucun projet actif",
                        message = if (showDone) {
                            "Les projets que tu passes en « Terminé » arrivent ici."
                        } else {
                            "Ajoute un projet, sa priorité et la tâche sur laquelle tu es en ce moment."
                        },
                        actionLabel = if (showDone) null else "Ajouter un projet",
                        onAction = if (showDone) null else ({ sheet = ProjectSheetTarget.New }),
                    )
                }
            }

            items(items = rows, key = { it.project.id }) { row ->
                ProjectCard(
                    row = row,
                    onClick = { sheet = ProjectSheetTarget.Edit(row.project) },
                )
            }
        }

        ExtendedFloatingActionButton(
            onClick = { sheet = ProjectSheetTarget.New },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Text("Projet", modifier = Modifier.padding(start = 8.dp))
        }
    }

    sheet?.let { target ->
        ProjectSheet(
            editing = (target as? ProjectSheetTarget.Edit)?.project,
            today = viewModel.today(),
            onDismiss = { sheet = null },
            onCreate = viewModel::create,
            onUpdate = viewModel::update,
            onDelete = viewModel::delete,
        )
    }
}

private sealed interface ProjectSheetTarget {
    data object New : ProjectSheetTarget
    data class Edit(val project: Project) : ProjectSheetTarget
}
