package com.felix.bestfelix.ui.notes

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.felix.bestfelix.domain.model.Note
import com.felix.bestfelix.domain.model.NoteCategory
import com.felix.bestfelix.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var sheet by remember { mutableStateOf<NoteSheetTarget?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Notes") }) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = viewModel::search,
                    label = { Text("Rechercher dans les notes") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (state.query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.search("") }) {
                                Icon(Icons.Filled.Close, contentDescription = "Effacer la recherche")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )

                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = state.selected == null,
                        onClick = { viewModel.select(null) },
                        label = { Text("Toutes (${state.total})") },
                    )
                    NoteCategory.entries.forEach { category ->
                        FilterChip(
                            selected = state.selected == category,
                            onClick = { viewModel.select(category) },
                            label = { Text("${category.shortLabel} (${state.counts[category] ?: 0})") },
                        )
                    }
                }

                if (!state.loading && state.notes.isEmpty()) {
                    EmptyState(
                        icon = Icons.AutoMirrored.Outlined.StickyNote2,
                        title = if (state.query.isNotBlank()) {
                            "Aucune note trouvée"
                        } else {
                            "Aucune note ici"
                        },
                        message = if (state.query.isNotBlank()) {
                            "Essaie un autre mot, ou change de rubrique."
                        } else {
                            "Note ce qui compte : une idée, un déclencheur de rechute, " +
                                "un détail sur un projet ou un crédit."
                        },
                        actionLabel = if (state.query.isNotBlank()) null else "Écrire une note",
                        onAction = if (state.query.isNotBlank()) {
                            null
                        } else {
                            ({ sheet = NoteSheetTarget.New })
                        },
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(items = state.notes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onClick = { sheet = NoteSheetTarget.Edit(note) },
                            )
                        }
                    }
                }
            }

            ExtendedFloatingActionButton(
                onClick = { sheet = NoteSheetTarget.New },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Text("Note", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }

    sheet?.let { target ->
        NoteSheet(
            editing = (target as? NoteSheetTarget.Edit)?.note,
            defaultCategory = state.selected ?: NoteCategory.GENERAL,
            onDismiss = { sheet = null },
            onCreate = viewModel::create,
            onUpdate = viewModel::update,
            onDelete = viewModel::delete,
        )
    }
}

private sealed interface NoteSheetTarget {
    data object New : NoteSheetTarget
    data class Edit(val note: Note) : NoteSheetTarget
}
